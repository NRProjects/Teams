package mods.nate.teams.commands;

import mods.nate.teams.teams.TeamsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static mods.nate.teams.utils.ChatUtils.chatPrefix;
import static mods.nate.teams.utils.ChatUtils.sendMessage;

public class TeamsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            displayHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        UUID playerUUID = player.getUniqueId();

        switch (subCommand) {
            case "create" -> {
                if (args.length == 1) {
                    sendMessage(sender, chatPrefix() + "&eMissing team name. Usage: &a/team create <name>");
                    break;
                }

                if (TeamsManager.hasTeam(playerUUID)) {
                    sendMessage(sender, chatPrefix() + "&eYou already have a team!");
                    break;
                }

                String teamName = args[1];
                TeamsManager.createTeam(teamName, playerUUID);
                sendMessage(sender, chatPrefix() + "&eYour new team is now called " + "&a" + teamName);
            }

            case "disband" -> {
                String teamName = TeamsManager.getTeamName(playerUUID);
                if (!TeamsManager.hasTeam(playerUUID)) {
                    sendMessage(sender, chatPrefix() + "&eYou don't have a team to disband");
                    break;
                }

                if (!TeamsManager.isTeamLeader(playerUUID)) {
                    sendMessage(sender, chatPrefix() + "&eYou are not the leader of " + teamName);
                    break;
                }

                TeamsManager.disbandTeam(playerUUID);
                sendMessage(sender, chatPrefix() + "&eYou disbanded your team " + "&a" + teamName);
            }

            case "rename" -> {
                if (args.length == 1) {
                    sendMessage(sender, chatPrefix() + "&aUsage: /teams rename <new name>");
                    break;
                }

                if (!TeamsManager.hasTeam(playerUUID)) {
                    sendMessage(sender, chatPrefix() + "&eYou don't have a team to rename");
                    break;
                }

                if (!TeamsManager.isTeamLeader(playerUUID)) {
                    sendMessage(sender, chatPrefix() + "&eOnly the team leader can rename the team");
                    break;
                }

                String newName = args[1];
                TeamsManager.renameTeam(newName);
                sendMessage(sender, chatPrefix() + "You've renamed your team to " + newName);
            }

            case "invite" -> {
                if (args.length > 1) {

                }
            }

            case "revokeinvite" -> {
                if (args.length > 1) {

                }
            }

            case "join" -> {
                if (args.length > 1) {
                    TeamsManager.hasInvite();
                    TeamsManager.joinTeam();
                } else {

                }
            }

            case "leave" -> {
                TeamsManager.leaveTeam();
            }

            case "kick" -> {
                if (args.length > 1) {

                    TeamsManager.kickPlayer();
                } else {

                }

            }

            case "promote" -> {
                if (args.length > 1) {
                    TeamsManager.promoteTeamMember();
                } else {

                }

            }

            case "demote" -> {
                if (args.length > 1) {
                    TeamsManager.demoteTeamMember();
                } else {

                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> validArgs = Arrays.asList(
                "create",
                "disband",
                "rename",
                "invite",
                "revokeinvite",
                "join",
                "leave",
                "kick",
                "promote",
                "demote"
        );

        if (args.length == 1) {
            return validArgs.stream()
                    .filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 0) {
            return validArgs;
        }
        return Collections.emptyList();
    }

    private void displayHelp(CommandSender sender) {
        sendMessage(sender,
                """
                        &7=== Showing help for &a/teams &7===\s
                        &a/teams create &e<name> &7- Create a new team
                        &a/teams rename &e<new name> &7- Rename your team
                        &a/teams disband &7- Disband your team
                        &a/teams invite &e<player> &7- Invite a player to your team
                        &a/teams revokeinvite &e<player> &7- Invite a player to your team
                        &a/teams kick &e<player> &7- Kick a player from your team
                        &a/teams kick &e<player> &7- Kick a player from your team
                        &a/teams promote &e<player> &7- Promote a player within your team
                        &a/teams demote &e<player> &7- Demote a player within your team
                        """);
    }
}
