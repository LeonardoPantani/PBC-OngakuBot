/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.HashMap;

public class Help implements CommandInterface {
    @Override
    public void handle(CommandContext context, HashMap<String, OptionMapping> args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
        eb.setColor(Color.GREEN);
        eb.setDescription("The only discord music bot you will ever need. Does not require to vote to any bot website nor buy a license for \"premium\" features. Bot made by **leopantaa**");
        eb.setFooter("Ongaku Bot");
        context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows what this bot can do.";
    }
}
