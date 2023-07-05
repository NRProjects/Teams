package mods.nate.teams.utils;

import mods.nate.teams.Teams;
import mods.nate.teams.commands.TeamsCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

public class CommandRegistration {
    public static void registerCommands() {
        setupCommand("teams", new TeamsCommand());
    }

    private static void setupCommand(String commandLabel, Object executor) {
        PluginCommand command = Teams.plugin.getCommand(commandLabel);
        if (command != null) {
            command.setExecutor((CommandExecutor) executor);
            if (executor instanceof TabExecutor) {
                command.setTabCompleter((TabExecutor) executor);
            }
        }
    }
}
