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
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Scanner;

import static it.pantani.ongakubot.Utils.isURL;
import static it.pantani.ongakubot.Utils.readFromConsole;

public class ConsoleHandler implements Runnable {
    private JDA jda;
    private volatile boolean stop;

    public void run() {
        Scanner reader = new Scanner(System.in);
        String raw_request;

        while(!stop) {
            raw_request = readFromConsole(reader);

            if (raw_request.equals("")) continue; // se l'utente ha premuto invio continuo
            String[] temp = raw_request.split(" ");

            String request = temp[0];
            String[] arguments = new String[temp.length - 1];
            System.arraycopy(temp, 1, arguments, 0, temp.length - 1);

            switch (request) {
                case "join":
                    if (arguments.length != 1) {
                        System.err.println("[!] Utilizzo comando errato: " + request + " <id canale>");
                        break;
                    }
                    join(arguments[0]);
                    break;
                case "quit":
                    if (arguments.length != 1) {
                        System.err.println("[!] Utilizzo comando errato: " + request + " <id canale>");
                        break;
                    }
                    quit(arguments[0]);
                    break;
                case "play":
                    if (arguments.length != 1) {
                        System.err.println("[!] Utilizzo comando errato: " + request + " <link>");
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
        System.out.println("Finito!");
    }

    private void status() {
        List<Guild> guilds = jda.getGuilds();
        if(guilds.isEmpty()) {
            System.err.println("[!] Il bot non è in nessun server");
            return;
        }

        String leftAlignFormat = "| %-30s | %-30s | %-75s |%n";
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");
        System.out.format("| %-30s | Nome canale                    | In riproduzione                                                             |%n", "Server (" + guilds.size() + ")");
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");

        for(Guild g : guilds) {
            AudioManager am = g.getAudioManager();
            GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
            Member self = g.getSelfMember();
            GuildVoiceState guildVoiceState = self.getVoiceState();
            AudioPlayer audioPlayer = gmm.audioPlayer;
            AudioTrack track = audioPlayer.getPlayingTrack();

            // nome canale
            String nomeCanale = "//";
            if(guildVoiceState.inAudioChannel()) {
                nomeCanale = guildVoiceState.getChannel().getName();
            }

            // in riproduzione
            String nometrack = "//";
            if(track != null) {
                nometrack = track.getInfo().title;
                if(audioPlayer.isPaused()) {
                    nometrack += " (in pausa)";
                }
            }
            System.out.format(leftAlignFormat, g.getName(), nomeCanale, nometrack);
        }
        System.out.format("+--------------------------------+--------------------------------+-----------------------------------------------------------------------------+%n");
    }

    private void stop() {
        List<Guild> guilds = jda.getGuilds();
        if(guilds.isEmpty()) {
            System.err.println("[!] Il bot non è in nessun server");
            return;
        }

        for(Guild g : guilds) {
            AudioManager am = g.getAudioManager();
            GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
            gmm.scheduler.player.stopTrack();
            gmm.scheduler.queue.clear();
            System.out.println("> Player chiuso nel server " + g.getName());
        }
    }

    private void play(String link) {
        if (!isURL(link)) {
            link = "ytsearch:" + link;
        }

        List<Guild> guilds = jda.getGuilds();
        if(guilds.isEmpty()) {
            System.err.println("[!] Il bot non è in nessun server");
            return;
        }

        for(Guild g : guilds) {
            Member m = g.getSelfMember();
            GuildVoiceState gvs = m.getVoiceState();
            if(!gvs.inAudioChannel()) continue;
            try {
                PlayerManager.getInstance().loadAndPlay(gvs, link);
                System.out.println("> In riproduzione il brano specificato nel server " + g.getName());
            } catch(Exception e) {
                System.err.println("[!] Errore durante l'elaborazione del link.");
                return;
            }
        }
    }

    private void join(String channelID) {
        if(jda == null) { System.err.println("[!] Impossibile elaborare questa richiesta"); return; }
        GuildChannel guildChannel = jda.getGuildChannelById(channelID);
        if(guildChannel == null) {
            System.err.println("[!] Non esiste un canale con quell'id");
            return;
        }
        Guild gilda = guildChannel.getGuild();

        final AudioManager audioManager = gilda.getAudioManager();
        try {
            audioManager.openAudioConnection((AudioChannel) guildChannel);
            System.out.println("> Bot entrato nel canale '" + guildChannel.getName() + "'.");
        } catch(InsufficientPermissionException e) {
            System.err.println("[!] Il bot non ha abbastanza permessi per entrare nel canale '" + guildChannel.getName() + "'.");
        }
    }

    private void quit(String channelID) {
        if(jda == null) { System.err.println("[!] Impossibile elaborare questa richiesta"); return; }
        GuildChannel guildChannel = jda.getGuildChannelById(channelID);
        if(guildChannel == null) {
            System.err.println("[!] Non esiste un canale con quell'id");
            return;
        }
        Guild gilda = guildChannel.getGuild();
        Member m = gilda.getSelfMember();
        VoiceChannel vc = (VoiceChannel) guildChannel;

        if(!vc.getMembers().contains(m)) {
            System.err.println("[!] Il bot non è nel canale '" + guildChannel.getName() + "'.");
            return;
        }

        final AudioManager audioManager = gilda.getAudioManager();
        audioManager.closeAudioConnection();
        System.out.println("> Bot uscito dal canale '" + guildChannel.getName() + "'.");
    }

    private void quitAll() {
        if(jda == null) { System.err.println("[!] Impossibile elaborare questa richiesta"); return; }
        List<AudioManager> guildChannels = jda.getAudioManagers();
        if(guildChannels.isEmpty()) {
            System.err.println("[!] Il bot non è in nessun canale vocale");
            return;
        }

        for(AudioManager am : guildChannels) {
            am.closeAudioConnection();
            System.out.println("> Bot uscito dal canale.");
        }
    }

    private void help() {
        System.out.println("> LISTA COMANDI:");
        System.out.println("join <id canale>   - Fa entrare il bot nel canale con quell'id");
        System.out.println("quitAll            - Fa uscire il bot da tutti i canali in cui è");
        System.out.println("quit <id canale>   - Fa uscire il bot dal canale con quell'id");
        System.out.println("status             - Mostra lo stato del bot nei server");
        System.out.println("play <link>        - Riproduce su in tutti i canali quella traccia");
        System.out.println("stop               - Stoppa la riproduzione delle tracce in tutti i canali");
    }

    private void unknownCommand() {
        System.out.println("> Comando sconosciuto, scrivi 'help' per una lista di comandi.");
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public void end() {
        stop = true;
        Robot robot;
        try {
            robot = new Robot();

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch(AWTException e) {
            e.printStackTrace();
        }
    }
}
