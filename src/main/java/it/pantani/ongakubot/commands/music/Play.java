/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static it.pantani.ongakubot.Utils.isURL;

public class Play implements CommandInterface {
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

        if (!selfVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be in my voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        String link = args.get("url_or_text").getAsString();

        if (!isURL(link)) {
            link = "ytsearch:" + link;
        }

        try {
            PlayerManager.getInstance().loadAndPlay(getName(), context, link, false);
        } catch(IllegalStateException e) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("An error occurred while processing the link");
            eb.setFooter("Ongaku Bot");

            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
        } catch(RuntimeException ex) {
            link = "ytsearch:" + link;
            PlayerManager.getInstance().loadAndPlay(getName(), context, link, false);
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Makes the bot play music.";
    }

    @Override
    public List<OptionData> getArgs() {
        return List.of(
                new OptionData(OptionType.STRING, "url_or_text", "Url of resource or name to search", true)
        );
    }
}
