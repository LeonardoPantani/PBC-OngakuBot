/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
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

public class Join implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if (!callerVoiceState.inAudioChannel()) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "You must be in a voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        final AudioManager audioManager = guild.getAudioManager();
        final AudioChannel callerChannel = callerVoiceState.getChannel();

        assert callerChannel != null;

        try {
            audioManager.openAudioConnection(callerChannel);
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "I joined `" + callerChannel.getName() + "` channel.")).queue();
        } catch(InsufficientPermissionException e) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "Cannot join `" + callerChannel.getName() + "` channel because I do not have enough permissions.")).queue();
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
