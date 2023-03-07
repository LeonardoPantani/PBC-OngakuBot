/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;

public class Repeat implements CommandInterface {
    @Override
    public void handle(CommandContext context, HashMap<String, OptionMapping> args) {
        final Member self = context.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("I must be inside a voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final Member member = context.getEvent().getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be inside a voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be in my voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final AudioManager audioManager = context.getGuild().getAudioManager();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        final boolean newRepeating = !musicManager.scheduler.repeating;

        musicManager.scheduler.repeating = newRepeating;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
        eb.setColor(Color.GREEN);
        eb.setDescription("Current track " + (newRepeating ? "is now" : "is no longer") + " repeating.");
        eb.setFooter("Ongaku Bot");
        context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getHelp() {
        return "Enables or disables repetition of the current track.";
    }
}
