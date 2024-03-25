/*
 * Copyright (c) 2024. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;

/**
 * This class represents a command to make the bot join the audio channel of the user who invokes it.
 * It implements the CommandInterface interface and provides methods to handle the join command.
 */
public class Join implements CommandInterface {

    /**
     * Handles the join command by making the bot join the audio channel of the user who invoked the command.
     * This version of the handle method delegates to the overloaded handle method with showMessage set to true.
     *
     * @param hook   The InteractionHook for sending messages back to Discord.
     * @param args   The arguments passed with the command (unused in this command).
     * @param guild  The Guild in which the command was invoked.
     * @param self   The bot's Member object representing itself.
     * @param caller The Member object representing the user who invoked the command.
     * @return The status of the handling process (HANDLE_OK, HANDLE_ERROR, or HANDLE_INFO).
     */
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        return handle(hook, args, guild, self, caller, true);
    }

    /**
     * Handles the join command by making the bot join the audio channel of the user who invoked the command.
     *
     * @param hook        The InteractionHook for sending messages back to Discord.
     * @param args        The arguments passed with the command (unused in this command).
     * @param guild       The Guild in which the command was invoked.
     * @param self        The bot's Member object representing itself.
     * @param caller      The Member object representing the user who invoked the command.
     * @param showMessage If true, show a message indicating the success or failure of the join operation.
     * @return The status of the handling process (HANDLE_OK, HANDLE_ERROR, or HANDLE_INFO).
     */
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller, boolean showMessage) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        // Check if the user who invoked the command is in a voice channel
        if (!callerVoiceState.inAudioChannel()) {
            if (showMessage)
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "You must be in a voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        // Check if the bot is already playing a track in another channel
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack track = audioPlayer.getPlayingTrack();
        if (track != null) {
            if (showMessage)
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "I cannot join your channel because I am playing a track on another channel.")).queue();
            return Utils.Status.HANDLE_INFO;
        }

        final AudioManager audioManager = guild.getAudioManager();
        final AudioChannel callerChannel = callerVoiceState.getChannel();

        assert callerChannel != null;

        try {
            // Join the audio channel of the user who invoked the command
            audioManager.openAudioConnection(callerChannel);
            if (showMessage)
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "I joined `" + callerChannel.getName() + "` channel.")).queue();
        } catch (InsufficientPermissionException e) {
            if (showMessage)
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "Cannot join `" + callerChannel.getName() + "` channel because I do not have enough permissions.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getHelp() {
        return "Makes the bot enter the audio channel of the user who invokes it.";
    }
}
