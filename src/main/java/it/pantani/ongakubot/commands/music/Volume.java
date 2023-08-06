/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Volume implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        int volume = args.get("volume").getAsInt();
        if(volume < 0 || volume > 200) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "Volume value must be between `" + 0 + "` and `" + 200 + "`")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if (!Objects.equals(callerVoiceState.getChannel(), selfVoiceState.getChannel())) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "I must be in your voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Volume set to `" + volume + "` (before was `" + audioPlayer.getVolume() + "`)")).queue();
        audioPlayer.setVolume(volume);
        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getHelp() {
        return "Sets the volume of this bot for all users inside the channel.";
    }

    @Override
    public List<OptionData> getArgs() {
        return Collections.singletonList(
                new OptionData(OptionType.INTEGER, "volume", "Volume between 0 and 200 (100 is default)", true)
        );
    }
}
