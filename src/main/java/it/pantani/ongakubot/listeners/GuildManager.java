/*
 * Copyright (c) 2024. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.listeners;

import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class GuildManager extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        super.onGuildJoin(event);

        Guild guild = event.getGuild();
        DefaultGuildChannelUnion tryChannel = guild.getDefaultChannel();
        if(tryChannel == null) return;

        TextChannel channel = tryChannel.asTextChannel();

        channel.sendMessageEmbeds(Utils.createEmbed("Thanks for adding me, the **Ongaku Bot \uD83C\uDFB6**!", Color.YELLOW,
                "You can use this bot to, obviously play music from different sources, single videos or entire playlists. Type `/play <url or text>` to start listening!" +
                "\n\n" +
                "**Ongaku Bot \uD83C\uDFB6** uses the Discord Command System so you can type `/` on a text channel visible to the bot to see all the commands that offers.")).queue();
    }
}
