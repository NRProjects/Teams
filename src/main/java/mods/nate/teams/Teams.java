package mods.nate.teams;

import mods.nate.teams.utils.CommandRegistration;
import mods.nate.teams.utils.DatabaseManager;
import mods.nate.teams.utils.EventRegistration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Teams extends JavaPlugin {
    public static Teams plugin;

    public static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    @Override
    public void onEnable() {
        super.onEnable();
        Teams.plugin = this;

        EventRegistration.registerEvents();
        CommandRegistration.registerCommands();
        DatabaseManager.createDatabase();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DatabaseManager.close();
    }

}
