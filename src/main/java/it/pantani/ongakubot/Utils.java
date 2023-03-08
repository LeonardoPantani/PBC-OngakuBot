/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Utils {
    public enum Status {
        HANDLE_OK,
        HANDLE_ERROR,
        HANDLE_INFO
    }

    public static boolean isURL(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    public static String readFromConsole(Scanner reader) {
        String input = "";

        try {
            input = reader.nextLine();
        } catch(NoSuchElementException ignored) { } // per evitare errore se si preme CTRL+C su Windows

        return input;
    }

    public static String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static MessageEmbed createEmbed(String commandName, Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(commandName.toUpperCase() + " " + "COMMAND");
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"));

        return eb.build();
    }

    public static MessageEmbed createEmbed(Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"));

        return eb.build();
    }
}
