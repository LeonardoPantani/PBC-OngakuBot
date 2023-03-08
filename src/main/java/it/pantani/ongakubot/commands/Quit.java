/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class Quit implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if (!Objects.equals(callerVoiceState.getChannel(), selfVoiceState.getChannel())) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "I must be in your voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        final AudioManager audioManager = guild.getAudioManager();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        audioManager.closeAudioConnection();

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "I left the voice channel.")).queue();
        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave the voice channel.";
    }
}
