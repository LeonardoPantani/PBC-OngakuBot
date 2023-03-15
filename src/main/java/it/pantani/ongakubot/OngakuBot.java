/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import io.github.cdimascio.dotenv.Dotenv;
import it.pantani.ongakubot.listeners.CommandManager;
import it.pantani.ongakubot.listeners.GuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class OngakuBot {
    public static final long startTime = System.currentTimeMillis();
    private static final Dotenv config = Dotenv.configure().filename(".env").load();

    public OngakuBot() throws InterruptedException {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        Thread inputHandler = new Thread(consoleHandler);

        System.out.println("> Attivazione del bot...");

        JDABuilder jdaBuilder = JDABuilder.createDefault(config.get("BOT_TOKEN"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new CommandManager())
                .addEventListeners(new GuildManager())
                .setActivity(Activity.listening("/help"));

        // creo il jda
        JDA jda = jdaBuilder.build();

        // aspetto la preparazione del bot
        jda.awaitReady();

        // gestisce tutta la parte di comandi inviati dall'amministratore del server per vederne lo stato
        consoleHandler.setJda(jda);
        inputHandler.start();

        // chiusura input handler
        try {
            inputHandler.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigValue(String key) {
        return config.get(key);
    }

    public static void main(String[] args) throws InterruptedException {
        OngakuBot bot = new OngakuBot();
    }
}
