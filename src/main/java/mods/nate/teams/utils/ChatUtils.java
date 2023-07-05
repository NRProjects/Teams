package mods.nate.teams.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;

public class ChatUtils {
    public static String chatPrefix() {
        return "&8[&aTeams&8] ";
    }

    public static void sendMessage(ServerOperator sender, String message) {
        if (sender instanceof CommandSender target) {
            target.sendMessage(coloredChat(message));
        }
    }

    public static String coloredChat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
