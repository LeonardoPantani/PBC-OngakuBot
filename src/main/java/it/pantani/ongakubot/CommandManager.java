/*
 * Copyright (c) 2023. Leonardo Pantani
 * https://github.com/LeonardoPantani
 */

package it.pantani.ongakubot;

import it.pantani.ongakubot.commands.Help;
import it.pantani.ongakubot.commands.Join;
import it.pantani.ongakubot.commands.Quit;
import it.pantani.ongakubot.commands.music.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Help());
        addCommand(new Join());
        addCommand(new Quit());

        addCommand(new Play());
        addCommand(new Stop());
        addCommand(new Pause());
        addCommand(new NowPlaying());
        addCommand(new Repeat());
        addCommand(new Skip());
        addCommand(new Volume());
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
        String command = event.getName();

        CommandInterface cmd = this.getCommand(command);

        HashMap<String, OptionMapping> cmd_args = new HashMap<>();
        for(OptionMapping om : event.getOptions()) {
            cmd_args.put(om.getName(), om);
        }

        if(cmd != null) {
            event.deferReply().setEphemeral(true).queue(); // specifico a discord che sto pensando

            CommandContext ctx = new CommandContext(event, cmd_args);
            cmd.handle(ctx, cmd_args);
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
}
