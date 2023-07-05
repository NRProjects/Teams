package mods.nate.teams.teams;

import mods.nate.teams.utils.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TeamsManager {
    public static void createTeam(String name, UUID leader) {
        DatabaseManager.queryDB("INSERT INTO Teams (Name, Leader) VALUES (?, ?);", name, leader.toString());
    }

    public static void disbandTeam(UUID leader) {
        DatabaseManager.queryDB("DELETE FROM Teams WHERE Leader=?;", leader.toString());
    }

    public static void joinTeam() {

    }

    public static void leaveTeam() {

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
        ResultSet rs = DatabaseManager.queryDB("SELECT COUNT(1) FROM Teams WHERE Leader=? OR Members LIKE ? LIMIT 1;", playerUUID.toString(), wildcard(playerUUID.toString()));
        try {
            return rs.getInt(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasInvite() {
        return false;
    }

    public static boolean isTeamLeader(UUID playerUUID) {
        ResultSet rs = DatabaseManager.queryDB("SELECT COUNT(1) FROM Teams WHERE Leader=? LIMIT 1;", playerUUID.toString());
        try {
            if (rs.next()) {
                return rs.getInt(1) != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTeamName(UUID playerUUID) {
        ResultSet rs = DatabaseManager.queryDB("SELECT Name FROM Teams WHERE Leader=? OR Members LIKE ? LIMIT 1;", playerUUID.toString(), wildcard(playerUUID.toString()));
        try {
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String wildcard(String parameter) {
        return String.format("%%%s%%", parameter);
    }

}
