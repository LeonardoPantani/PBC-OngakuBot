/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.HashMap;

public class Help implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.BLUE, "The only discord music bot you will ever need. Does not require to vote to any bot website nor buy a license for \"premium\" features.")).queue();
        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows what this bot can do.";
    }
}
