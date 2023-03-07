/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;

public class Join implements CommandInterface {
    @Override
    public void handle(CommandContext context, HashMap<String, OptionMapping> args) {
        final MessageChannel channel = context.getEvent().getChannel();
        final Member self = context.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = context.getEvent().getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be in a voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final AudioManager audioManager = context.getGuild().getAudioManager();
        final AudioChannel memberChannel = memberVoiceState.getChannel();

        try {
            audioManager.openAudioConnection(memberChannel);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.GREEN);
            eb.setDescription("I joined `" + memberChannel.getName() + "` channel.");
            eb.setFooter("Ongaku Bot");

            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
        } catch(InsufficientPermissionException e) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("Cannot join `" + memberChannel.getName() + "` channel because I do not have enough permissions.");
            eb.setFooter("Ongaku Bot");

            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
        }
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
