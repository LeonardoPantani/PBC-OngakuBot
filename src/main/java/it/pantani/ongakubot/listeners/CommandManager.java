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

/**
 This class represents a CommandManager that handles various commands for the Discord bot.
 */
public class CommandManager extends ListenerAdapter {
    private final List<CommandInterface> commands = new ArrayList<>();

    /**
     Constructs a new CommandManager and adds various commands to its list.
     */
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
        addCommand(new DeleteLogChannel());
    }

    /**
     Adds a command to the list of commands, checking for name conflicts.

     @param cmd The command to add.
     @throws IllegalArgumentException if a command with the same name already exists.
     */
    private void addCommand(CommandInterface cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("[!] A command with this name already exists.");
        }

        commands.add(cmd);
    }

    /**
     Returns the list of all registered commands.
     @return The list of commands.
     */
    public List<CommandInterface> getCommands() {
        return commands;
    }

    /**
     Finds and returns a command based on its name.

     @param search The name of the command to search for.
     @return The command with the specified name or null if not found.
     */
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
        event.deferReply().setEphemeral(true).queue(); // Tell Discord we're thinking.

        if(event.getGuild() == null) { // It's a direct message (DM).
            // Prompt the user to add the bot to a server.
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "I cannot play music without being in a server. Please add me to your server by clicking the button below."))
                    .addActionRow(
                            Button.link("https://discord.com/api/oauth2/authorize?client_id=" + OngakuBot.getConfigValue("BOT_DISCORD_ID") + "&permissions=0&scope=bot", "Add bot to a server")
                    ).queue();
            return;
        }

        // Get the command and its arguments from the interaction.
        CommandInterface cmd = this.getCommand(event.getName());
        HashMap<String, OptionMapping> cmd_args = new HashMap<>();
        for(OptionMapping om : event.getOptions()) {
            cmd_args.put(om.getName(), om);
        }

        if(cmd != null) {
            // Handle the command based on the interaction details.
            cmd.handle(event.getHook(), cmd_args, event.getGuild(), event.getGuild().getSelfMember(), event.getInteraction().getMember());
        }

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        event.deferReply().setEphemeral(true).queue(); // Tell Discord we're thinking.
        HashMap<String, OptionMapping> cmd_args = new HashMap<>();

        if(event.getGuild() == null) { // It's a direct message (DM).
            // Prompt the user to add the bot to a server.
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "I cannot play music without being in a server. Please add me to your server by clicking the button below."))
                    .addActionRow(
                            Button.link("https://discord.com/api/oauth2/authorize?client_id=" + OngakuBot.getConfigValue("BOT_DISCORD_ID") + "&permissions=0&scope=bot", "Add bot to a server")
                    ).queue();
        }

        // Get the command and its arguments from the button interaction.
        CommandInterface cmd = this.getCommand(event.getComponentId());
        if(cmd != null) {
            // Handle the command based on the button interaction details.
            cmd.handle(event.getHook(), cmd_args, event.getGuild(), event.getGuild().getSelfMember(), event.getInteraction().getMember());
        } else {
            // Handle the case when the button interaction does not match any command.
            event.getHook().sendMessageEmbeds(Utils.createEmbed(Color.RED, "Wrong button interaction.")).queue();
        }

    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        super.onGuildReady(event);
        List<CommandData> commandData = new ArrayList<>();

        // Prepare command data for slash commands registration.
        for(CommandInterface cmd : commands) {
            commandData.add(Commands.slash(cmd.getName(), cmd.getHelp()).addOptions(cmd.getArgs()));
        }
        event.getJDA().updateCommands().addCommands(commandData).queue();

    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("> Bot ready in " + (System.currentTimeMillis() - OngakuBot.startTime) + "ms.");
    }
}