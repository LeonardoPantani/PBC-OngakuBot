package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class SetLogChannel implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        GuildChannelUnion channel = args.get("channel").getAsChannel();

        Utils.logChannels.put(guild.getIdLong(), channel);

        hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.GREEN, "Set log channel to `" + channel.getName() + "`.")).queue();

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
        return List.of(
                new OptionData(OptionType.CHANNEL, "channel", "Channel you want me to send logs to", true)
        );
    }
}
