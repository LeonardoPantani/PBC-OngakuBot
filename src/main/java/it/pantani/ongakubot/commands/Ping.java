/*
 * Copyright (c) 2024. Leonardo Pantani
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

public class Ping implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Latency: " + hook.getJDA().getGatewayPing() + "ms")).queue();
        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Shows the ping of this bot.";
    }
}
