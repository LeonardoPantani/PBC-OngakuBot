/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class Rewind implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if (!Objects.equals(callerVoiceState.getChannel(), selfVoiceState.getChannel())) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "I must be in your voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_OK;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(audioPlayer.getPlayingTrack() == null) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.BLUE, "There are not any tracks currently playing.")).queue();
            return Utils.Status.HANDLE_INFO;
        }

        AudioTrack toRewind = musicManager.audioPlayer.getPlayingTrack().makeClone();
        musicManager.audioPlayer.stopTrack();
        musicManager.audioPlayer.playTrack(toRewind);

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Rewound current playing track.")).queue();

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "rewind";
    }

    @Override
    public String getHelp() {
        return "Rewinds current track to its beginning.";
    }
}
