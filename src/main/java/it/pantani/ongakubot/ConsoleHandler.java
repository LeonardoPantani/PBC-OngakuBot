/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Scanner;

import static it.pantani.ongakubot.Utils.isURL;
import static it.pantani.ongakubot.Utils.readFromConsole;

/**
 * This class represents a console command handler that processes user commands for the OngakuBot.
 * It implements the Runnable interface to run as a separate thread.
 */
public class ConsoleHandler implements Runnable {
    // Reference to the JDA (Java Discord API) instance
    private JDA jda;

    // Variable to control the termination of the console handler
    private volatile boolean stop;

    /**
     * The run method of the Runnable interface.
     * It continuously reads commands from the console and processes them until the stop flag is set to true.
     */
    public void run() {
        Scanner reader = new Scanner(System.in);
        String raw_request;

        while (!stop) {
            raw_request = readFromConsole(reader);

            // Ignore empty input (when the user presses enter without typing a command)
            if (raw_request.equals("")) continue;

            // Split the command into the request and arguments
            String[] temp = raw_request.split(" ");
            String request = temp[0];
            String[] arguments = new String[temp.length - 1];
            System.arraycopy(temp, 1, arguments, 0, temp.length - 1);

            // Process the command based on the request
            switch (request) {
                case "join":
                    if (arguments.length != 1) {
                        System.err.println("[!] Incorrect command usage: " + request + " <channel id>");
                        break;
                    }
                    join(arguments[0]);
                    break;
                case "quit":
                    if (arguments.length != 1) {
                        System.err.println("[!] Incorrect command usage: " + request + " <channel id>");
                        break;
                    }
                    quit(arguments[0]);
                    break;
                case "play":
                    if (arguments.length != 1) {
                        System.err.println("[!] Incorrect command usage: " + request + " <link>");
                        break;
                    }
                    play(arguments[0]);
                    break;
                case "stop":
                    stop();
                    break;
                case "status":
                    status();
                    break;
                case "quitAll":
                    quitAll();
                    break;
                case "help":
                    help();
                    break;
                default:
                    unknownCommand();
                    break;
            }
        }
        System.out.println("Done!");
    }

    /**
     * Display the status of the bot in all the servers it is connected to.
     * Shows the server name, channel name, and currently playing track (if any).
     */
    private void status() {
        List<Guild> guilds = jda.getGuilds();
        if (guilds.isEmpty()) {
            System.err.println("[!] The bot is not in any server");
            return;
        }

        String leftAlignFormat = "| %-30s | %-30s | %-75s |%n";
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");
        System.out.format("| %-30s | Channel name                   | Currently playing                                                           |%n", "Server (" + guilds.size() + ")");
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");

        for (Guild g : guilds) {
            AudioManager am = g.getAudioManager();
            GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
            Member self = g.getSelfMember();
            GuildVoiceState guildVoiceState = self.getVoiceState();
            AudioPlayer audioPlayer = gmm.audioPlayer;
            AudioTrack track = audioPlayer.getPlayingTrack();

            // Get the name of the channel where the bot is currently connected
            String channelName = "//";
            if (guildVoiceState.inAudioChannel()) {
                channelName = guildVoiceState.getChannel().getName();
            }

            // Get the name of the currently playing track (if any) with optional " (paused)" text
            String trackInfo = "//";
            if (track != null) {
                trackInfo = track.getInfo().title;
                if (audioPlayer.isPaused()) {
                    trackInfo += " (paused)";
                }
            }
            System.out.format(leftAlignFormat, g.getName(), channelName, trackInfo);
        }
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");
    }

    /**
     * Stops track playback in all channels where the bot is connected.
     */
    private void stop() {
        List<Guild> guilds = jda.getGuilds();
        if (guilds.isEmpty()) {
            System.err.println("[!] The bot is not in any server");
            return;
        }

        for (Guild g : guilds) {
            GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
            gmm.scheduler.player.stopTrack();
            gmm.scheduler.queue.clear();
            System.out.println("> Player terminated in server " + g.getName());
        }
    }

    /**
     * Plays a song specified by the link in all channels where the bot is connected.
     * If the link is not a valid URL, it treats it as a YouTube search query.
     *
     * @param link The link or search query for the song.
     */
    private void play(String link) {
        if (!isURL(link)) {
            link = "ytsearch:" + link;
        }

        List<Guild> guilds = jda.getGuilds();
        if (guilds.isEmpty()) {
            System.err.println("[!] The bot is not in any server");
            return;
        }


        int tot = 0;
        for (Guild g : guilds) {
            Member m = g.getSelfMember();
            GuildVoiceState gvs = m.getVoiceState();
            if (!gvs.inAudioChannel()) continue;
            try {
                PlayerManager.getInstance().loadAndPlay(gvs, link);
                System.out.println("> Playing the song specified in the server " + g.getName());
                tot++;
            } catch (Exception e) {
                System.err.println("[!] Error while processing link.");
                return;
            }
        }

        if(tot == 0) {
            System.err.println("[!] The bot is not in any server channel");
        } else {
            System.out.println("> Playing the song in " + tot + " server(s)");
        }
    }

    /**
     * Makes the bot join the voice channel with the specified channel ID in a server.
     *
     * @param channelID The ID of the voice channel to join.
     */
    private void join(String channelID) {
        if (jda == null) {
            System.err.println("[!] Unable to process this request");
            return;
        }
        GuildChannel guildChannel = jda.getGuildChannelById(channelID);
        if (guildChannel == null) {
            System.err.println("[!] There is no channel with that id");
            return;
        }
        Guild guild = guildChannel.getGuild();

        final AudioManager audioManager = guild.getAudioManager();
        try {
            audioManager.openAudioConnection((VoiceChannel) guildChannel);
            System.out.println("> Bot entered the channel '" + guildChannel.getName() + "'.");
        } catch (InsufficientPermissionException e) {
            System.err.println("[!] The bot does not have enough permissions to enter the channel '" + guildChannel.getName() + "'.");
        }
    }

    /**
     * Makes the bot quit the voice channel with the specified channel ID in a server.
     *
     * @param channelID The ID of the voice channel to quit.
     */
    private void quit(String channelID) {
        if (jda == null) {
            System.err.println("[!] Unable to process this request");
            return;
        }
        GuildChannel guildChannel = jda.getGuildChannelById(channelID);
        if (guildChannel == null) {
            System.err.println("[!] There is no channel with that id");
            return;
        }
        Guild guild = guildChannel.getGuild();
        Member self = guild.getSelfMember();
        VoiceChannel vc = (VoiceChannel) guildChannel;

        if (!vc.getMembers().contains(self)) {
            System.err.println("[!] The bot is not in the channel '" + guildChannel.getName() + "'.");
            return;
        }

        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        System.out.println("> Bot left the channel '" + guildChannel.getName() + "'.");
    }

    /**
     * Makes the bot quit all the voice channels it is connected to in all servers.
     */
    private void quitAll() {
        if (jda == null) {
            System.err.println("[!] Unable to process this request");
            return;
        }
        List<AudioManager> guildChannels = jda.getAudioManagers();
        if (guildChannels.isEmpty()) {
            System.err.println("[!] The bot is not in any voice channel");
            return;
        }

        for (AudioManager am : guildChannels) {
            am.closeAudioConnection();
            System.out.println("> Bot left the channel.");
        }
    }

    /**
     * Displays a list of available commands and their usage.
     */
    private void help() {
        System.out.println("> COMMANDS LIST:");
        System.out.println("join <channel id> - Makes the bot join the channel with that id");
        System.out.println("quitAll           - Quits the bot from all channels it is in");
        System.out.println("quit <channel id> - Gets the bot out of the channel with that id");
        System.out.println("status            - Shows the status of the bot in the servers");
        System.out.println("play <link>       - Plays the specified song in all connected channels");
        System.out.println("stop              - Stops track playback on all channels");
    }

    /**
     * Displays a message for an unknown command.
     */
    private void unknownCommand() {
        System.out.println("> Unknown command, type 'help' for a list of commands.");
    }

    /**
     * Sets the JDA (Java Discord API) instance to be used by the console handler.
     *
     * @param jda The JDA instance to be set.
     */
    public void setJda(JDA jda) {
        this.jda = jda;
    }

    /**
     * Sets the stop flag to terminate the console handler thread.
     * Also simulates pressing the "Enter" key to unblock the console input.
     */
    public void end() {
        stop = true;
        Robot robot;
        try {
            robot = new Robot();

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
