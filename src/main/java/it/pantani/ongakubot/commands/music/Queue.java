/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static it.pantani.ongakubot.Utils.formatTime;

public class Queue implements CommandInterface {
    @Override
    public void handle(CommandContext context, HashMap<String, OptionMapping> args) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        final AudioTrack currentTrack = audioPlayer.getPlayingTrack();

        if (queue.isEmpty() && currentTrack == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.BLUE);
            eb.setDescription("Queue is empty.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final StringBuilder ret = new StringBuilder();
        ret.append("** Current Queue **");

        if(currentTrack != null) {
            AudioTrackInfo ati = currentTrack.getInfo();

            ret.append(":point_right: ").append(" `");
            if(ati.title.length() > 76) { ret.append(ati.title, 0, 75); ret.append("..."); } else { ret.append(ati.title); }
            ret.append("` by `");
            if(ati.author.length() > 76) { ret.append(ati.author, 0, 75); ret.append("..."); } else { ret.append(ati.author); }
            ret.append("` [`").append(formatTime(ati.length)).append("` duration] :notes:\n");
        }

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            ret.append('#').append(i + 1).append(" `");
            if(info.title.length() > 76) { ret.append(info.title, 0, 75); ret.append("..."); } else { ret.append(info.title); }
            ret.append("` by `");
            if(info.author.length() > 76) { ret.append(info.author, 0, 75); ret.append("..."); } else { ret.append(info.author); }
            ret.append("` [`").append(formatTime(track.getDuration())).append("` duration]\n");
        }

        if (trackList.size() > trackCount) {
            ret.append("And `").append(trackList.size() - trackCount).append("` others...");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
        eb.setColor(Color.GREEN);
        eb.setDescription(ret);
        eb.setFooter("Ongaku Bot");
        context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Shows tracks in the current queue.";
    }
}
