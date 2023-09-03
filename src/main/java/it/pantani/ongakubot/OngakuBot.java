/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import it.pantani.ongakubot.listeners.CommandManager;
import it.pantani.ongakubot.listeners.GuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**

 This class represents the main entry point for the OngakuBot application.
 It initializes the database based on the configuration provided in the 'config.properties' file.
 Then, it sets up the Discord bot, registers event listeners, and starts the bot's main loop.
 */

public class OngakuBot {
    // Variable to store the start time of the bot
    public static final long startTime = System.currentTimeMillis();

    public static Properties properties = new Properties();

    /**
     * Constructor for the OngakuBot class.
     * It initializes the database based on the 'DB_TYPE' configuration in the 'config.properties' file.
     * If 'DB_TYPE' is set to 'sqlite', it initializes an SQLite database.
     * If 'DB_TYPE' is set to 'mysql', it initializes a MySQL database.
     * Otherwise, it displays an error message indicating an invalid 'DB_TYPE'.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    private OngakuBot() throws InterruptedException {
        // Database initialization based on configuration
        boolean dbStatus = false;

        if (getConfigValue("DB_TYPE").equalsIgnoreCase("sqlite")) {
            dbStatus = DatabaseManager.initializeDB(getConfigValue("DB_FILENAME"));
        } else if (getConfigValue("DB_TYPE").equalsIgnoreCase("mysql")) {
            dbStatus = DatabaseManager.initializeDB(getConfigValue("DB_HOST"), Integer.parseInt(getConfigValue("DB_PORT")), getConfigValue("DB_NAME"), getConfigValue("DB_USERNAME"), getConfigValue("DB_PASSWORD"));
        } else {
            System.err.println("[!] config.properties file contains an invalid value for the key 'DB_TYPE'. Accepted values are: sqlite, mysql");
            return;
        }

        if(!dbStatus) return; // cannot proceed without db configuration


        // Setting up the console handler for admin server commands
        ConsoleHandler consoleHandler = new ConsoleHandler();
        Thread inputHandler = new Thread(consoleHandler);

        System.out.println("> Preparing bot...");

        // Building the JDA (Java Discord API) instance
        JDABuilder jdaBuilder = JDABuilder.createDefault(getConfigValue("BOT_TOKEN"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new CommandManager())
                .addEventListeners(new GuildManager())
                .setActivity(Activity.listening("/help"));

        // Creating the JDA instance
        JDA jda = jdaBuilder.build();

        // Waiting for the JDA to be ready
        jda.awaitReady();

        // Handling server administrator commands for viewing bot status
        consoleHandler.setJda(jda);
        inputHandler.start();

        // Closing the input handler
        try {
            inputHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the configuration value for the given key from the 'config.properties' file.
     *
     * @param key The key for which the configuration value is to be retrieved.
     * @return The configuration value associated with the provided key.
     */
    public static String getConfigValue(String key) {
        return properties.getProperty(key);
    }

    private static boolean validateConfig() {
        int invalidKeys = 0;
        String toCheck;

        // BOT_TOKEN
        toCheck = getConfigValue("BOT_TOKEN");
        if(toCheck == null || toCheck.length() != 70 || toCheck.charAt(24) != '.' || toCheck.charAt(31) != '.') { System.err.println("[!] Invalid Key: BOT_TOKEN"); invalidKeys++; }

        // BOT_NAME
        toCheck = getConfigValue("BOT_NAME");
        if(toCheck == null) { System.err.println("[!] Invalid Key: BOT_NAME"); invalidKeys++; }

        // BOT_AUTHOR
        toCheck = getConfigValue("BOT_AUTHOR");
        if(toCheck == null) { System.err.println("[!] Invalid Key: BOT_AUTHOR"); invalidKeys++; }

        // BOT_VERSION
        toCheck = getConfigValue("BOT_VERSION");
        if(toCheck == null) { System.err.println("[!] Invalid Key: BOT_VERSION"); invalidKeys++; }

        // BOT_DISCORD_ID
        toCheck = getConfigValue("BOT_DISCORD_ID");
        if(toCheck == null || toCheck.length() != 18 || !toCheck.matches("[0-9]+")) { System.err.println("[!] Invalid Key: BOT_DISCORD_ID"); invalidKeys++; }

        // BOT_AVATAR_URL
        toCheck = getConfigValue("BOT_AVATAR_URL");
        if(toCheck == null) { System.err.println("[!] Invalid Key: BOT_AVATAR_URL"); invalidKeys++; }

        // DB_TYPE
        toCheck = getConfigValue("DB_TYPE");
        if(toCheck == null || (!toCheck.equalsIgnoreCase("sqlite") && !toCheck.equalsIgnoreCase("mysql"))) { System.err.println("[!] Invalid Key: DB_TYPE. Valid types are: sqlite, mysql"); invalidKeys++; }

        // DB_FILENAME
        toCheck = getConfigValue("DB_FILENAME");
        if(toCheck == null) { System.err.println("[!] Invalid Key: DB_FILENAME"); invalidKeys++; }

        // DB_HOST
        toCheck = getConfigValue("DB_HOST");
        if(toCheck != null) {
            try {
                InetAddress.getByName(toCheck);
            } catch (UnknownHostException e) {
                System.err.println("[!] Invalid Key: DB_HOST");
                invalidKeys++;
            }
        } else {
            System.err.println("[!] Invalid Key: DB_HOST");
            invalidKeys++;
        }

        // DB_PORT
        toCheck = getConfigValue("DB_PORT");
        if(toCheck == null || Integer.parseInt(toCheck) < 1 || Integer.parseInt(toCheck) > 65535) { System.err.println("[!] Invalid Key: DB_PORT"); invalidKeys++; }

        // DB_NAME
        toCheck = getConfigValue("DB_NAME");
        if(toCheck == null) { System.err.println("[!] Invalid Key: DB_NAME"); invalidKeys++; }

        // DB_USERNAME
        toCheck = getConfigValue("DB_USERNAME");
        if(toCheck == null) { System.err.println("[!] Invalid Key: DB_USERNAME"); invalidKeys++; }

        // DB_PASSWORD
        toCheck = getConfigValue("DB_PASSWORD");
        if(toCheck == null) { System.err.println("[!] Invalid Key: DB_PASSWORD"); invalidKeys++; }

        return invalidKeys == 0;
    }


    private static boolean initializeConfig() throws IOException {
        File external = new File("config.properties");
        if (external.exists()) {
            properties.load(new FileInputStream(external));
            return true; // config read correctly
        } else {
            // config.properties not found in current path, copying from resources
            InputStream defaultConfigStream = OngakuBot.class.getClassLoader().getResourceAsStream("default_config.properties");
            if (defaultConfigStream != null) {
                properties.load(defaultConfigStream);

                try (FileOutputStream fos = new FileOutputStream(external)) {
                    properties.store(fos, "Edit this configuration file...");
                }

                return false; // file just created, changes required for the bot to work
            } else {
                throw new FileNotFoundException("[!] 'default_config.properties' file not found in the classpath.");
            }
        }

    }

    /**
     * Main method of the OngakuBot application.
     * It loads the 'config.properties' file to read the configuration.
     * If the 'config.properties' file is missing, it displays an error message.
     * Then, it creates an instance of OngakuBot, starting the application.
     *
     * @param args Command-line arguments (not used in this application).
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            if (!initializeConfig()) {
                System.out.println("> Config file 'config.properties' created. Edit it and execute the jar file again to start the bot.");
                return;
            } else {
                System.out.println("> Config file read successfully.");
            }

            if(!validateConfig()) {
                System.err.println("> Config file 'config.properties' validation failed.");
                return;
            } else {
                System.out.println("> Config file validation succeeded.");
            }

            // Creare un'istanza di OngakuBot
            OngakuBot bot = new OngakuBot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}