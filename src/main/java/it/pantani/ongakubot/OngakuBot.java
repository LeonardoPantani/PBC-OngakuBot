/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import it.pantani.ongakubot.listeners.CommandManager;
import it.pantani.ongakubot.listeners.GuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**

 This class represents the main entry point for the OngakuBot application.
 It initializes the database based on the configuration provided in the 'env' file.
 Then, it sets up the Discord bot, registers event listeners, and starts the bot's main loop.
 */

public class OngakuBot {
    // Variable to store the start time of the bot
    public static final long startTime = System.currentTimeMillis();

    // Configuration object to read environment variables
    private static Dotenv config;

    /**
     * Constructor for the OngakuBot class.
     * It initializes the database based on the 'DB_TYPE' configuration in the 'env' file.
     * If 'DB_TYPE' is set to 'sqlite', it initializes an SQLite database.
     * If 'DB_TYPE' is set to 'mysql', it initializes a MySQL database.
     * Otherwise, it displays an error message indicating an invalid 'DB_TYPE'.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public OngakuBot() throws InterruptedException {
        // Database initialization based on configuration
        if (getConfigValue("DB_TYPE").equalsIgnoreCase("sqlite")) {
            DatabaseManager.initializeDB(getConfigValue("DB_FILENAME"));
        } else if (getConfigValue("DB_TYPE").equalsIgnoreCase("mysql")) {
            DatabaseManager.initializeDB(getConfigValue("DB_HOST"), getConfigValue("DB_NAME"), getConfigValue("DB_USERNAME"), getConfigValue("DB_PASSWORD"));
        } else {
            System.err.println("[!] env file contains an invalid value for the key 'DB_TYPE'. Accepted values are: sqlite, mysql");
            return;
        }

        // Setting up the console handler for admin server commands
        ConsoleHandler consoleHandler = new ConsoleHandler();
        Thread inputHandler = new Thread(consoleHandler);

        System.out.println("> Bot activation...");

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
     * Retrieves the configuration value for the given key from the 'env' file.
     *
     * @param key The key for which the configuration value is to be retrieved.
     * @return The configuration value associated with the provided key.
     */
    public static String getConfigValue(String key) {
        return config.get(key);
    }

    /**
     * Main method of the OngakuBot application.
     * It loads the 'env' file to read the configuration.
     * If the 'env' file is missing, it displays an error message.
     * Then, it creates an instance of OngakuBot, starting the application.
     *
     * @param args Command-line arguments (not used in this application).
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            config = Dotenv.configure().filename("env").load();
        } catch (DotenvException e) {
            System.err.println("[!] Copy the env.example file to an 'env' file in the same folder and edit it.");
            return;
        }

        // Create an instance of OngakuBot
        OngakuBot bot = new OngakuBot();
    }

}