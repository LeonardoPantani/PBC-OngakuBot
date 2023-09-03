package it.pantani.ongakubot.commands.music;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.DatabaseManager;
import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;

public class SetLogChannel implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        GuildChannelUnion channel = args.get("channel").getAsChannel();

        try {
            DatabaseManager.setLogChannel(guild.getId(), channel.asTextChannel().getId(), result -> {
                if (result.equals("true")) {
                    hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Set log channel to `" + channel.getName() + "`.")).queue();
                } else {
                    hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "Log channel not set due to a error.")).queue();

                }
            });
        } catch (IllegalStateException e) { // if the user gives the command a channel type different from a text channel
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "You must specify a *Text Channel* for this command to work.")).queue();
        }

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "setlogchannel";
    }

    @Override
    public String getHelp() {
        return "Sets the text channel where the bot sends song requests.";
    }

    @Override
    public java.util.List<OptionData> getArgs() {
        return Collections.singletonList(
                new OptionData(OptionType.CHANNEL, "channel", "Channel you want me to send logs to", true)
        );
    }
}
