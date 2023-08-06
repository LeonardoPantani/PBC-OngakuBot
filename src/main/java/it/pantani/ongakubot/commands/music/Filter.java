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

public class Filter implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        String filter_name = args.get("filter_name").getAsString();

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

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "This command is coming soon.")).queue();

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "filter";
    }

    @Override
    public String getHelp() {
        return "Apply filters to playings tracks.";
    }

    @Override
    public List<OptionData> getArgs() {
        return Collections.singletonList(
                new OptionData(OptionType.STRING, "filter_name", "Choose a filter to apply to the playing track. To remove the filter do not use arguments.", false)
        );
    }
}
