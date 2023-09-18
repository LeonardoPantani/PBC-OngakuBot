/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;

public interface CommandInterface {
    Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller);

    String getName();

    String getHelp();

    default List<OptionData> getArgs() {
        return List.of();
        /*
            To add if the new command has argoments:

        return Collections.unmodifiableList(Arrays.asList(
                new OptionData(OptionType.STRING, "arg_name", "arg_desc", true)
        ));
         */
    }
}
