package it.pantani.ongakubot.commands.music;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.DatabaseManager;
import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.HashMap;

public class DeleteLogChannel implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        DatabaseManager.deleteLogChannel(guild.getId(), result -> {
            if(result.equals("true")) {
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Log channel deleted.")).queue();
            } else {
                hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "Log channel is not set.")).queue();
            }
        });

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "deletelogchannel";
    }

    @Override
    public String getHelp() {
        return "Deletes the log channel of this server, if previously set.";
    }
}
