/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot.listeners;

import it.pantani.ongakubot.CommandInterface;
import it.pantani.ongakubot.OngakuBot;
import it.pantani.ongakubot.Utils;
import it.pantani.ongakubot.commands.Help;
import it.pantani.ongakubot.commands.Join;
import it.pantani.ongakubot.commands.Ping;
import it.pantani.ongakubot.commands.Quit;
import it.pantani.ongakubot.commands.music.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Help());
        addCommand(new Join());
        addCommand(new Quit());
        addCommand(new Ping());

        addCommand(new Play());
        addCommand(new Stop());
        addCommand(new Pause());
        addCommand(new NowPlaying());
        addCommand(new Repeat());
        addCommand(new Skip());
        addCommand(new Volume());
        addCommand(new Queue());
        addCommand(new Rewind());
        addCommand(new Earrape());
        addCommand(new Filter());

        addCommand(new SetLogChannel());
    }

    private void addCommand(CommandInterface cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("[!] A command with this name already exists.");
        }

        commands.add(cmd);
    }

    public List<CommandInterface> getCommands() {
        return commands;
    }

    public CommandInterface getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (CommandInterface cmd : this.commands) {
            if (cmd.getName().equals(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    // -----------

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        event.deferReply().setEphemeral(true).queue(); // specifico a discord che sto pensando

        if(event.getGuild() == null) { // è un DM
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "I cannot play music without being in a server. Please add me to your server by clicking the button below."))
                    .addActionRow(
                            Button.link("https://discord.com/api/oauth2/authorize?client_id=" + OngakuBot.getConfigValue("BOT_DISCORD_ID") + "&permissions=0&scope=bot", "Add bot to a server")
                    ).queue();
            return;
        }

        CommandInterface cmd = this.getCommand(event.getName());
        HashMap<String, OptionMapping> cmd_args = new HashMap<>();
        for(OptionMapping om : event.getOptions()) {
            cmd_args.put(om.getName(), om);
        }

        if(cmd != null) {
            cmd.handle(event.getHook(), cmd_args, event.getGuild(), event.getGuild().getSelfMember(), event.getInteraction().getMember());
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        event.deferReply().setEphemeral(true).queue(); // specifico a discord che sto pensando
        HashMap<String, OptionMapping> cmd_args = new HashMap<>();

        if(event.getGuild() == null) { // è un DM
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "I cannot play music without being in a server. Please add me to your server by clicking the button below."))
                    .addActionRow(
                            Button.link("https://discord.com/api/oauth2/authorize?client_id=" + OngakuBot.getConfigValue("BOT_DISCORD_ID") + "&permissions=0&scope=bot", "Add bot to a server")
                    ).queue();
        }

        CommandInterface cmd = this.getCommand(event.getComponentId());
        if(cmd != null) {
            cmd.handle(event.getHook(), cmd_args, event.getGuild(), event.getGuild().getSelfMember(), event.getInteraction().getMember());
        } else {
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "Wrong button interaction.")).queue();
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        super.onGuildReady(event);
        List<CommandData> commandData = new ArrayList<>();

        for(CommandInterface cmd : commands) {
            commandData.add(Commands.slash(cmd.getName(), cmd.getHelp()).addOptions(cmd.getArgs()));
        }
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("> Bot pronto in " + (System.currentTimeMillis() - OngakuBot.startTime) + "ms.");
    }
}
