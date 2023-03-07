/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import io.github.cdimascio.dotenv.Dotenv;
import it.pantani.ongakubot.commands.Listener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class OngakuBot {
    public static long startTime;
    public static JDA jda;

    public OngakuBot() throws InterruptedException {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        Thread inputHandler = new Thread(consoleHandler);

        startTime = System.currentTimeMillis();
        System.out.println("> Attivazione del bot...");

        Dotenv config = Dotenv.configure().filename(".env").load();

        JDABuilder jdaBuilder = JDABuilder.createDefault(config.get("BOT_TOKEN"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new Listener(consoleHandler))
                .addEventListeners(new CommandManager())
                .setActivity(Activity.listening("/help"));

        // creo il jda
        jda = jdaBuilder.build();

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

    public static void main(String[] args) throws InterruptedException {
        OngakuBot bot = new OngakuBot();
    }
}
