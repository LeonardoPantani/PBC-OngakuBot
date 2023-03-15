/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static it.pantani.ongakubot.Utils.formatTime;

public class Queue implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        final AudioTrack currentTrack = audioPlayer.getPlayingTrack();

        if (queue.isEmpty() && currentTrack == null) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.BLUE, "Queue is empty.")).queue();
            return Utils.Status.HANDLE_INFO;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final StringBuilder ret = new StringBuilder();
        ret.append("** Current Queue **\n");

        if(currentTrack != null) {
            AudioTrackInfo ati = currentTrack.getInfo();
            String duration = formatTime(ati.length, ":");

            ret.append(":point_right: ").append(" [");
            if(ati.title.length() > 76) { ret.append(ati.title, 0, 75); ret.append("..."); } else { ret.append(ati.title); }
            ret.append("](").append(ati.uri).append(") by `");
            if(ati.author.length() > 76) { ret.append(ati.author, 0, 75); ret.append("..."); } else { ret.append(ati.author); }
            ret.append("` [`");
            if(duration != null)
                ret.append(duration);
            else
                ret.append("LIVE");
            ret.append("`] :notes:\n");
        }

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();
            final String duration = formatTime(track.getDuration(), ":");

            ret.append('#').append(i + 1).append("  [");
            if(info.title.length() > 76) { ret.append(info.title, 0, 75); ret.append("..."); } else { ret.append(info.title); }
            ret.append("](").append(info.uri).append(") by `");
            if(info.author.length() > 76) { ret.append(info.author, 0, 75); ret.append("..."); } else { ret.append(info.author); }
            ret.append("` [`");
            if(duration != null)
                ret.append(duration);
            else
                ret.append("LIVE");
            ret.append("`]\n");
        }

        if (trackList.size() > trackCount) {
            ret.append("And `").append(trackList.size() - trackCount).append("` others...");
        }

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, ret.toString())).queue();

        return Utils.Status.HANDLE_OK;
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
