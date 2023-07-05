package mods.nate.teams.utils;

import mods.nate.teams.Teams;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class DatabaseManager {
    private static Connection connection;

    public static void createDatabase() {
        try {
            File dataFolder = Teams.plugin.getDataFolder();
            String databaseFileName = "teams.db";
            File databaseFile = new File(dataFolder, databaseFileName);
            dataFolder.mkdirs();
            if (!databaseFile.exists()) {
                Teams.LOGGER.info("[Teams] Database file not found. Creating a new one...");
                databaseFile.createNewFile();
            }

            String databaseURL = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            connection = DriverManager.getConnection(databaseURL);
            createTables();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public static ResultSet queryDB(String query, Object... args) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Integer) {
                    statement.setInt(i + 1, (Integer) arg);
                } else if (arg instanceof String) {
                    statement.setString(i + 1, (String) arg);
                }

            }

            if (statement.execute()) {
                return statement.getResultSet();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static void createTables() throws SQLException {
        Statement statement = connection.createStatement();

        String createTeamsTables =
                "CREATE TABLE IF NOT EXISTS Teams (" +
                "ID INTEGER NOT NULL UNIQUE, " +
                "Name TEXT NOT NULL UNIQUE, " +
                "Leader TEXT NOT NULL UNIQUE, " +
                "Moderators TEXT, " +
                "Members TEXT, " +
                "PRIMARY KEY(ID AUTOINCREMENT)" +
                ")";
        statement.executeUpdate(createTeamsTables);
        statement.close();
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
