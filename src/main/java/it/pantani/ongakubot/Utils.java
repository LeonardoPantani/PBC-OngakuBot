/*
 * Copyright (c) 2024. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * This class contains utility methods used in the OngakuBot application.
 */
public class Utils {
    // Enum to represent different handling statuses
    public enum Status {
        HANDLE_OK,
        HANDLE_ERROR,
        HANDLE_INFO
    }

    /**
     * Checks if the given string is a valid URL by attempting to open a stream to the URL.
     *
     * @param url The string to check for URL validity.
     * @return true if the string is a valid URL, false otherwise.
     */
    public static boolean isURL(String url) {
        try {
            // Try to open a stream to the URL; if successful, it is a valid URL
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    /**
     * Reads a line of input from the console using the provided Scanner.
     *
     * @param reader The Scanner object to read input from the console.
     * @return The line of input read from the console.
     */
    public static String readFromConsole(Scanner reader) {
        String input = "";

        try {
            input = reader.nextLine();
        } catch (NoSuchElementException ignored) { } // To avoid an error if CTRL+C is pressed on Windows

        return input;
    }

    /**
     * Formats a time duration in milliseconds into a string representation with the given separator.
     * The time is formatted in the format "hh" + separator + "mm" + separator + "ss".
     *
     * @param timeInMillis The time duration in milliseconds to be formatted.
     * @param separator    The separator to be used between hours, minutes, and seconds.
     * @return The formatted time string.
     */
    public static String formatTime(long timeInMillis, String separator) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);

        if (hours == 2562047788015L) {
            return null; // To handle special case where the time is infinite
        }

        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d" + separator + "%02d" + separator + "%02d", hours, minutes, seconds);
    }

    /**
     * Formats a timestamp in milliseconds into a string representation with the given separator.
     * The timestamp is formatted in the format "yyyy" + separator + "MM" + separator + "dd" + separator + "HH" + separator + "mm" + separator + "ss".
     *
     * @param timeInMillis The timestamp in milliseconds to be formatted.
     * @param separator    The separator to be used between date and time components.
     * @return The formatted date string.
     */
    public static String formatDate(long timeInMillis, String separator) {
        Date currentDate = new Date(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd" + separator + "HH" + separator + "mm" + separator + "ss");
        return dateFormat.format(currentDate);
    }

    /**
     * Creates a MessageEmbed object with the provided title, color, and text.
     *
     * @param title The title for the MessageEmbed.
     * @param color The color to be used for the MessageEmbed.
     * @param text  The main text content of the MessageEmbed.
     * @return The created MessageEmbed object.
     */
    public static MessageEmbed createEmbed(String title, Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title.toUpperCase());
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"), OngakuBot.getConfigValue("BOT_AVATAR_URL"));

        return eb.build();
    }

    /**
     * Creates a MessageEmbed object with the provided color and text.
     *
     * @param color The color to be used for the MessageEmbed.
     * @param text  The main text content of the MessageEmbed.
     * @return The created MessageEmbed object.
     */
    public static MessageEmbed createEmbed(Color color, String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(color);
        eb.setDescription(text);
        eb.setFooter(OngakuBot.getConfigValue("BOT_NAME") + " v" + OngakuBot.getConfigValue("BOT_VERSION") + " | Author: " + OngakuBot.getConfigValue("BOT_AUTHOR"), OngakuBot.getConfigValue("BOT_AVATAR_URL"));

        return eb.build();
    }
}
