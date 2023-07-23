/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Utils {
    public enum Status {
        HANDLE_OK,
        HANDLE_ERROR,
        HANDLE_INFO
    }

    public static HashMap<Long, Object> logChannels = new HashMap<Long, Object>();

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

    public static String formatTime(long timeInMillis, String separator) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);

        if(hours == 2562047788015L) {
            return null;
        }

        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d" + separator + "%02d" + separator + "%02d", hours, minutes, seconds);
    }



    public static String formatDate(long timeInMillis, String separator) {
        Date currentDate = new Date(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd" + separator + "" + separator + "HH" + separator + "mm" + separator + "ss");
        return dateFormat.format(currentDate);
    }

    public static MessageEmbed createEmbed(String title, Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title.toUpperCase());
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"), "https://cdn.discordapp.com/avatars/933087337196441624/571bd8a833956617ebf0506b075832d2.png");

        return eb.build();
    }

    public static MessageEmbed createEmbed(Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"), "https://cdn.discordapp.com/avatars/933087337196441624/571bd8a833956617ebf0506b075832d2.png");

        return eb.build();
    }
}
