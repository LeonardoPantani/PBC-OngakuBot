/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;

public class CommandContext {
    private final SlashCommandInteractionEvent event;
    private final HashMap<String, OptionMapping> args;

    public CommandContext(SlashCommandInteractionEvent event, HashMap<String, OptionMapping> args) {
        this.event = event;
        this.args = args;
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public HashMap<String, OptionMapping> getArgs() {
        return args;
    }

    public Guild getGuild() {
        return event.getGuild();
    }
}