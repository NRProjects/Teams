package mods.nate.teams.commands;

import mods.nate.teams.teams.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
            displayHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        UUID playerUUID = player.getUniqueId();

        switch (subCommand) {
            case "create" -> {
                if (args.length == 1) {
                    // Missing team name
                    sendMessage(player, chatPrefix() + "&eMissing team name. Usage: &a/team create <name>");
                    break;
                }

                if (TeamsManager.hasTeam(playerUUID)) {
                    // Player already has a team
                    sendMessage(player, chatPrefix() + "&eYou already have a team!");
                    break;
                }

                // Get the team name from the arguments
                String teamName = args[1];

                TeamsManager.createTeam(teamName, playerUUID);
            }

            case "disband" -> {
                // Get the current team name
                String teamName = TeamsManager.getTeamNameFromPlayer(playerUUID);

                if (!TeamsManager.hasTeam(playerUUID)) {
                    // Player doesn't have a team to disband
                    sendMessage(player, chatPrefix() + "&eYou don't have a team to disband");
                    break;
                }

                if (!TeamsManager.isTeamLeader(playerUUID)) {
                    // Player is not the leader of the team
                    sendMessage(player, chatPrefix() + "&eYou are not the leader of " + teamName);
                    break;
                }

                TeamsManager.disbandTeam(playerUUID);
                sendMessage(player, chatPrefix() + "&eYou disbanded your team " + "&a" + teamName);
            }

            case "rename" -> {
                if (args.length == 1) {
                    // Missing team name in arguments
                    sendMessage(player, chatPrefix() + "&eMissing new team name. Usage: &a/teams rename <new name>");
                    break;
                }

                if (!TeamsManager.hasTeam(playerUUID)) {
                    // Player doesn't have a team to rename
                    sendMessage(player, chatPrefix() + "&eYou don't have a team to rename");
                    break;
                }

                if (!TeamsManager.isTeamLeader(playerUUID)) {
                    // Player is not the leader of the team
                    sendMessage(player, chatPrefix() + "&eOnly the team leader can rename the team");
                    break;
                }

                // Get the team name from the arguments
                String newName = args[1];

                TeamsManager.renameTeam(newName);
                sendMessage(player, chatPrefix() + "You've renamed your team to " + newName);
            }

            case "invite" -> {
                if (args.length == 1) {
                    // Missing player to invite
                    sendMessage(player, chatPrefix() + "&eUsage: &a/teams invite <player>");
                    break;
                }

                // Getting the player name from the command
                String invitedPlayerName = args[1];

                // Get the Player object from the command
                Player invitedPlayer = Bukkit.getPlayer(invitedPlayerName);

                // Get the UUID of the inviting player
                UUID invitingPlayerUUID = player.getUniqueId();

                // Get the team name of the inviting player
                String teamName = TeamsManager.getTeamNameFromPlayer(invitingPlayerUUID);

                if (invitedPlayer == null) {
                    // Player to invite is not online or doesn't exist
                    sendMessage(player, chatPrefix() + "&ePlayer is not online or does not exist");
                    break;
                }

                // Get the UUID of the player being invited
                UUID invitedPlayerUUID = invitedPlayer.getUniqueId();

                if (!TeamsManager.hasTeam(invitingPlayerUUID)) {
                    // Inviting player has no team
                    sendMessage(player, "Inviting player has no team");
                    break;
                }

                if (TeamsManager.hasTeam(invitedPlayerUUID)) {
                    // Invited player is already a member of another team
                    sendMessage(player,  chatPrefix() + "&a" + invitedPlayerName + " &eis already a member of another team");
                    break;
                }

                if (TeamsManager.hasValidInvite(invitedPlayerUUID)) {
                    // Player has already been invited to join a team
                    sendMessage(player, "Youve already invited this player");
                    break;
                }

                TeamsManager.invitePlayer(invitedPlayerUUID);

                sendMessage(player, chatPrefix() + "&eYou have invited &a" + invitedPlayerName + " &eto &a" + teamName);
                sendMessage(invitedPlayer, chatPrefix() + "&eYou have been invited to join &a" + teamName);
            }

            case "revokeinvite" -> {
                if (args.length > 1) {

                }
            }

            case "join" -> {
                if (args.length == 1) {
                    // Missing team name
                    sendMessage(player, chatPrefix() + "&eUsage: &a/teams join <team name>");
                    break;
                }

                String teamName = args[1];
                UUID joiningPlayerUUID = player.getUniqueId();

                if (TeamsManager.hasTeam(joiningPlayerUUID)) {
                    // If player is already in a team
                    sendMessage(player, chatPrefix() + "&eYou are already apart of a team");
                    break;
                }

                if (!TeamsManager.hasValidInvite(joiningPlayerUUID)) {
                    // Joining player doesn't have a valid invite
                    sendMessage(player, chatPrefix() + "&eYou have not been invited to &a" + teamName);
                    break;
                }

                TeamsManager.joinTeam(joiningPlayerUUID, teamName);
                sendMessage(player, chatPrefix() + "&eYou have joined team &a" + teamName);
            }

            case "leave" -> {
                if (args.length == 1) {
                    sendMessage(player, chatPrefix() + "&eMissing new team name. Usage: &a/teams leave <team name>");
                    break;
                }

                String playerInput = args[1];

                if (!TeamsManager.teamExists(playerInput)) {
                    sendMessage(player, chatPrefix() + "&eTeam &a" + playerInput +  " &edoes not exist.");
                    break;
                }

                TeamsManager.leaveTeam(playerUUID);
                sendMessage(player, chatPrefix() + "&eYou have left team &a" + playerInput);
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

            case "debug" -> {
                String teamName = args[1];
                int teamId = TeamsManager.getTeamID(teamName);
                sendMessage(player, String.valueOf(teamId));
            }

            case "list" -> {
                int page = 1;
                int teamsPerPage = 9;

                if (args.length >= 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sendMessage(player,"&cInvalid page number");
                        break;
                    }
                }

                Map<String, Object> result = TeamsManager.listTeams(page, teamsPerPage);

                if (!(boolean) result.get("isValidPage")) {
                    sendMessage(player, "&cInvalid page number");
                    break;
                }

                int totalPages = (int) result.get("totalPages");
                @SuppressWarnings("unchecked") // This will always return a List<String> so it doesn't matter
                List<String> teamsOnPage = (List<String>) result.get("teams");

                sendMessage(player, "&7=== Showing all &ateams &7- Page &a" + page + "/" + totalPages + "&7 ===\s");

                teamsOnPage.forEach(team -> sendMessage(player, team));
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
                "demote",
                "list,"
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
                        &a/teams join &e<team name> &7- Join a team
                        &a/teams leave &e<team name> &7- Leave a team
                        &a/teams kick &e<player> &7- Kick a player from your team
                        &a/teams promote &e<player> &7- Promote a player within your team
                        &a/teams demote &e<player> &7- Demote a player within your team
                        """);
    }
}
