package mods.nate.teams.utils;

import mods.nate.teams.Teams;
import mods.nate.teams.listeners.TeamsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventRegistration {

    public static void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new TeamsListener(), Teams.plugin);
    }
}
