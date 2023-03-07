/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandManager;
import it.pantani.ongakubot.ConsoleHandler;
import it.pantani.ongakubot.OngakuBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter {
    private final CommandManager manager = new CommandManager();

    public Listener(ConsoleHandler consoleHandler) {
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("> Bot pronto in " + (System.currentTimeMillis() - OngakuBot.startTime) + "ms.");
    }
}

