/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.commands.Join;
import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static it.pantani.ongakubot.Utils.isURL;

public class Play implements CommandInterface {
    @Override
    public Utils.Status handle(InteractionHook hook, HashMap<String, OptionMapping> args, Guild guild, Member self, Member caller) {
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final GuildVoiceState callerVoiceState = caller.getVoiceState();

        assert selfVoiceState != null;
        assert callerVoiceState != null;

        if(callerVoiceState.getChannel() == null) { // se l'utente non è in nessun canale
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "You must be inside a voice channel for this command to work.")).queue();
            return Utils.Status.HANDLE_ERROR;
        }

        if(selfVoiceState.getChannel() == null) { // se il bot non è in nessun canale lo faccio entrare
            new Join().handle(hook, new HashMap<>(), guild, self, caller, false);
        } else { // il bot è già in un canale
            if(!callerVoiceState.getChannel().equals(selfVoiceState.getChannel())) { // se non è nello stesso canale
                final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
                final AudioPlayer audioPlayer = musicManager.audioPlayer;
                final AudioTrack track = audioPlayer.getPlayingTrack();

                if(track != null) { // se il bot è in un canale diverso dall'utente e sta riproducendo allora mostro un errore
                    hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "This command does not work while I am playing a track in another channel.")).queue();
                    return Utils.Status.HANDLE_ERROR;
                } else { // se non sto riproducendo nulla nell'altro canale allora mi sposto
                    new Join().handle(hook, new HashMap<>(), guild, self, caller, false);
                }
            }
        }

        String link = args.get("url_or_text").getAsString();
        if (!isURL(link)) {
            link = "ytsearch:" + link;
        }

        try {
            PlayerManager.getInstance().loadAndPlay(hook, guild, link);
        } catch(IllegalStateException e) {
            hook.sendMessageEmbeds(Utils.createEmbed(getName(), Color.RED, "An error occurred while processing the link")).queue();
        } catch(RuntimeException ex) {
            PlayerManager.getInstance().loadAndPlay(hook, guild, "ytsearch:" + link);
        }

        return Utils.Status.HANDLE_OK;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Makes the bot play music.";
    }

    @Override
    public List<OptionData> getArgs() {
        return List.of(
                new OptionData(OptionType.STRING, "url_or_text", "Url of resource or name to search", true)
        );
    }
}
