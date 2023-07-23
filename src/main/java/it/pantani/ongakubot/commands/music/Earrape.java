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
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Earrape implements CommandInterface {
    private final ByteArrayOutputStream outputStream = null;
    boolean earrapeState;
    long dateStarted;

    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        if(args.get("state") == null) {
            earrapeState = false;
        } else {
            earrapeState = args.get("state").getAsBoolean();
        }

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if (!Objects.equals(callerVoiceState.getChannel(), selfVoiceState.getChannel())) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "I must be in your voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(earrapeState) { // attivare
            audioPlayer.setVolume(500);
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Earrape mode activated")).queue();
        } else { // disattivare
            audioPlayer.setVolume(100);
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Earrape mode deactivated")).queue();
        }

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "earrape";
    }

    @Override
    public String getHelp() {
        return "Sets audio to earrape.";
    }

    @Override
    public java.util.List<OptionData> getArgs() {
        return List.of(
                new OptionData(OptionType.BOOLEAN, "state", "Activate or deactivate earrape mode", false)
        );
    }
}