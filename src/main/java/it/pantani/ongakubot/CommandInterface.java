/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;

public interface CommandInterface {
    void handle(CommandContext context, HashMap<String, OptionMapping> args);

    String getName();

    String getHelp();

    default List<OptionData> getArgs() {
        return List.of();
        /*
        return Arrays.asList(
                    new OptionData(OptionType.STRING, "nome_argomento", "descrizione_argomento", true)
            );
         */
    }
}
