/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.commands.music;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.commands.Join;
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

        if (selfVoiceState.getChannel() == null || callerVoiceState.getChannel() == null || !callerVoiceState.getChannel().equals(selfVoiceState.getChannel())) { // se l'utente e il bot non sono nello stesso canale
            if(new Join().handle(hook, new HashMap<>(), guild, self, caller) != Utils.Status.HANDLE_OK) { // provo ad entrare, se non ci riesco mostro avviso TODO: ottimizzare
                return Utils.Status.HANDLE_ERROR;
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
