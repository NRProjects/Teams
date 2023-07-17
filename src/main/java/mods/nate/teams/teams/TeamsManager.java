package mods.nate.teams.teams;

import mods.nate.teams.Teams;
import mods.nate.teams.utils.ChatUtils;
import mods.nate.teams.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static mods.nate.teams.utils.ChatUtils.chatPrefix;
import static mods.nate.teams.utils.ChatUtils.sendMessage;

public class TeamsManager {
    private static final Map<UUID, Long> invites = new HashMap<>();

    public static void createTeam(String name, UUID creatorUUID) {
        Player player = Bukkit.getPlayer(creatorUUID);

        if (hasTeam(creatorUUID)) {
            // Check if the player is already in team
            sendMessage(player, chatPrefix() + "&eYou are already in a team");
            return;
        }

        if (teamExists(name)) {
            // Check if the team name is already taken
            sendMessage(player, chatPrefix() + "&eTeam name is already taken");
            return;
        }

        // Create the new team in the database
        DatabaseManager.queryDB("INSERT INTO Teams (Name, Leader) VALUES (?, ?);", name, creatorUUID.toString());
        sendMessage(player, chatPrefix() + "&eYour new team is now called " + "&a" + name);
    }

    public static void disbandTeam(UUID leader) {
        DatabaseManager.queryDB("DELETE FROM Teams WHERE Leader = ?;", leader.toString());
    }

    public static void joinTeam(UUID joiningUUID, String teamName) {
        int teamId = getTeamID(teamName);
        Player player = Bukkit.getPlayer(joiningUUID);

        // Check if the team exists
        if (!teamExists(teamName)) {
            ChatUtils.sendMessage(player, ChatUtils.chatPrefix() + "&eTeam &a" + teamName + " &edoes not exist.");
            return;
        }

        try {
            // Fetch current members of the team
            ResultSet rs = DatabaseManager.queryDB("SELECT Members FROM Teams WHERE ID = ?;", teamId);

            if (rs.next()) {
                // If members exist, append the new player's UUID, else, set it as the new player's UUID
                String members = rs.getString("Members");
                members = (members == null || members.isEmpty()) ? String.valueOf(joiningUUID) : members + ";" + joiningUUID;


                // Update the team members in the database
                DatabaseManager.queryDB("UPDATE Teams SET Members = ? WHERE ID = ?;", members, teamId);

                // If all operations succeed, remove the player from the invites list
                invites.remove(joiningUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void leaveTeam(UUID leavingUUID) {
        Player player = Bukkit.getPlayer(leavingUUID);

        if (isTeamLeader(leavingUUID)) {
            sendMessage(player, chatPrefix() + "&eYou are the current team leader, please use &a/team disband &einstead");
            return;
        }

        if(!hasTeam(leavingUUID)) {
            sendMessage(player, chatPrefix() + "&eYou are currently not part of a team");
            return;
        }

        String teamName = getTeamNameFromPlayer(leavingUUID);
        Integer teamId = getTeamID(teamName);

        try {
            ResultSet rs = DatabaseManager.queryDB("SELECT Members FROM Teams WHERE ID = ?;", teamId);

            if (rs.next()) {
                String members = rs.getString("Members");

                if (members != null && !members.isEmpty()) {
                    members = members.replace( ";" + leavingUUID.toString(), "").replace(leavingUUID.toString(), "");

                    DatabaseManager.queryDB("UPDATE Teams SET Members = ? WHERE ID = ?;", members, teamId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void kickPlayer() {

    }

    public static void renameTeam(String name) {

    }

    public static void promoteTeamMember() {

    }

    public static void demoteTeamMember() {

    }

    public static boolean hasTeam(UUID playerUUID) {
        // Check if the player is a leader or a member of a team
        ResultSet rs = DatabaseManager.queryDB("SELECT COUNT(1) FROM Teams WHERE Leader = ? OR Members LIKE ? LIMIT 1;", playerUUID.toString(), wildcard(playerUUID.toString()));
        try {
            return rs.getInt(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasValidInvite(UUID playerUUID) {
        // Check if the player has a valid invite
        long inviteTime = Optional.ofNullable(invites.get(playerUUID)).orElse(0L);
        long currentTime = System.currentTimeMillis();
        long expirationTime = inviteTime + (60 * 1000);

        return currentTime <= expirationTime;
    }

    public static void invitePlayer(UUID playerUUID) {
        // Invite the player and set an expiration time
        long currentTime = System.currentTimeMillis();
        invites.put(playerUUID, currentTime);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Remove the invite after 60 seconds
                invites.remove(playerUUID);
            }
        }.runTaskLater(Teams.plugin, 60 * 20);
    }

    public static boolean isTeamLeader(UUID playerUUID) {
        // Check if the player is a team leader
        ResultSet rs = DatabaseManager.queryDB("SELECT COUNT(1) FROM Teams WHERE Leader = ? LIMIT 1;", playerUUID.toString());
        try {
            if (rs.next()) {
                return rs.getInt(1) != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInSpecifiedTeam(UUID playerUUID) {
        int teamId = getTeamID(playerUUID);

        ResultSet rs = DatabaseManager.queryDB("SELECT COUNT(1) FROM Teams WHERE ID = ? AND (Leader = ? OR Members LIKE ?) LIMIT 1;");
        Teams.LOGGER.info("THIS WASNT SETUP CORRECTLY RED ALERT");
        return false;
    }

    public static int getTeamID(String teamName) {
        String teamNameLower = teamName.toLowerCase();

        ResultSet rs = DatabaseManager.queryDB("SELECT ID FROM Teams WHERE LOWER(Name) = LOWER(?) LIMIT 1;", teamNameLower);
        try {
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getTeamID(UUID playerUUID) {
        ResultSet rs = DatabaseManager.queryDB("SELECT ID FROM Teams WHERE Leader = ? OR Members LIKE ? LIMIT 1;", playerUUID.toString());
        try {
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static String getTeamNameFromPlayer(UUID playerUUID) {
        // Get the team name for the given player (leader or member)
        ResultSet rs = DatabaseManager.queryDB("SELECT Name FROM Teams WHERE Leader = ? OR Members LIKE ? LIMIT 1;", playerUUID.toString(), wildcard(playerUUID.toString()));
        try {
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean teamExists(String teamName) {
        int teamId = getTeamID(teamName.toLowerCase());

        // -1 is not a possible ID for a team
        return teamId > 0;
    }

    public static Map<String, Object> listTeams(int page, int teamsPerPage) {
        // Calculate the start index (offset) for the SQL query
        int startIndex = (page - 1) * teamsPerPage;

        // Get total teams from the database
        int totalTeams = getTotalTeams();

        // If there was an error retrieving the total teams, return an empty result with an error flag
        if (totalTeams == -1) {
            return createEmptyResult(false);
        }

        // Calculate the total number of pages based on the teams and teams per page
        int totalPages = (int) Math.ceil((double) totalTeams / teamsPerPage);

        // Check if page number is valid
        if (page < 1 || page > totalPages) {
            return createEmptyResult(false);
        }

        // Retrieve the teams for the specified page
        return getTeamsOnPage(startIndex, teamsPerPage, totalPages);
    }

    private static int getTotalTeams() {
        // Execute a query to count the total number of teams in the database
        ResultSet countRS = DatabaseManager.queryDB("SELECT COUNT(*) FROM Teams");

        try {
            if (countRS.next()) {
                // Return the count value from the result set
                return countRS.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 indicating an error in retrieving the total teams count
        return -1;
    }

    private static Map<String, Object> getTeamsOnPage(int startIndex, int teamsPerPage, int totalPages) {
        // Execute a query to retrieve the teams for the specified page, limited by the teams per page and starting index
        ResultSet rs = DatabaseManager.queryDB("SELECT Name, Leader, Members FROM Teams LIMIT " + teamsPerPage + " OFFSET " + startIndex);

        List<String> teamInfoList = new ArrayList<>();
        try {
            while (rs.next()) {
                // Format the team information and add it to the list
                teamInfoList.add(formatTeamInfo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isValidPage", true); // Flag indicating the page is valid
        result.put("totalPages", totalPages); // Total number of pages
        result.put("teams", teamInfoList); // List of team information

        return result;
    }

    private static String formatTeamInfo(ResultSet rs) throws SQLException {
        StringBuilder teamInfo = new StringBuilder("&a" + rs.getString("Name"));
        UUID leaderUUID = UUID.fromString(rs.getString("Leader"));
        Player leader = Bukkit.getPlayer(leaderUUID);

        // Append the leader information to the team info string builder
        teamInfo.append("&7: &a").append(leader != null ? leader.getDisplayName() : "Unknown leader");

        // Retrieve the members string from the result set
        String memberString = rs.getString("Members");

        // Add member count to team info if any members exist
        if (memberString != null && !memberString.isEmpty()) {
            String[] members = memberString.split(",");
            teamInfo.append("&7, and &a").append(members.length).append("&7 other members");
        }

        return teamInfo.toString();
    }

    private static Map<String, Object> createEmptyResult(boolean isValidPage) {
        Map<String, Object> result = new HashMap<>();
        result.put("isValidPage", isValidPage);
        result.put("totalPages", 0);
        result.put("teams", Collections.emptyList());
        return result;
    }

    private static String wildcard(String parameter) {
        // Add wildcard characters to the provided parameter for SQL LIKE queries
        return String.format("%%%s%%", parameter);
    }


}
