/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import it.pantani.ongakubot.CommandContext;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Volume implements CommandInterface {
    @Override
    public void handle(CommandContext context, HashMap<String, OptionMapping> args) {
        int volume = args.get("volume").getAsInt();

        if(volume < 0 || volume > 200) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("Volume value must be between `" + 0 + "` and `" + 200 + "`");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final Member self = context.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("I must be inside a voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final Member member = context.getEvent().getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be inside a voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
            eb.setColor(Color.RED);
            eb.setDescription("You must be in my voice channel for this command to work.");
            eb.setFooter("Ongaku Bot");
            context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(getName().toUpperCase() + " " + "COMMAND");
        eb.setColor(Color.GREEN);
        eb.setDescription("Volume set to `" + volume + "` (before was `" + audioPlayer.getVolume() + "`)");
        eb.setFooter("Ongaku Bot");
        context.getEvent().getHook().sendMessageEmbeds(eb.build()).setEphemeral(true).queue();

        audioPlayer.setVolume(volume);
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getHelp() {
        return "Sets the volume of this bot for all users inside the channel.";
    }

    @Override
    public List<OptionData> getArgs() {
        return List.of(
                new OptionData(OptionType.INTEGER, "volume", "Volume between 0 and 200 (100 is default)", true)
        );
    }
}
