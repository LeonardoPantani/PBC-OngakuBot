package it.pantani.ongakubot;

import java.io.File;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**

 This class represents a DatabaseManager that handles the initialization and interactions with the database.
 The class provides methods for initializing the database using either MySQL or SQLite.
 */
public class DatabaseManager {
    // URL of the database
    private static String databaseURL = null;

    // ExecutorService for managing asynchronous database operations
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Connection object for the database
    private static Connection connection;

    /**
     * Initializes the MySQL database with the provided connection parameters.
     * @param host The host address of the MySQL server.
     * @param dbName The name of the database to be used.
     * @param username The username for the MySQL server.
     * @param password The password for the MySQL server.
     */
    public static boolean initializeDB(String host, int port, String dbName, String username, String password) {
        // Construct the MySQL database URL
        databaseURL = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC";

        try {
            // required for using mysql in jar files
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("[!] Internal error. Contact the bot author: " + e.getLocalizedMessage());
                return false;
            }

            // Establish connection to the database
            connection = DriverManager.getConnection(databaseURL, username, password);

            // Create the "servers" table if not exists
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS servers (serverID VARCHAR(255) PRIMARY KEY, logChannelID VARCHAR(255))");
            System.out.println("> MySQL DB loaded.");
            return true;
        } catch (SQLException e) {
            System.err.println("[!] MySQL error during initialization: " + e.getLocalizedMessage());
            System.err.println("[!] MySQL error code: " + e.getErrorCode());

            // tips
            if(e.getErrorCode() == 1045) System.err.println("[!] Tip: be sure that the specified user in the config file can: create tables, select, add, update and delete rows in the chosen database. Also remember that creating a user like this: create user 'test-user'@'127.0.0.1' IDENTIFIED BY 'test-password' IS DIFFERENT FROM create user");
            if(e.getErrorCode() == 1049) System.err.println("[!] Tip: you must create the database in advance via mysql and then start the bot. If you do not know what 'mysql' is or do not have a working 'mysql' server available, type 'sqlite' in the 'DB_TYPE' option of the configuration file.");
            if(e.getErrorCode() == 0) System.err.println("[!] Tip: be sure that mysql is installed and is working in the host machine.");
        }
        return false;
    }

    /**
     * Initializes the SQLite database using the specified file.
     * @param dbFile The path of the SQLite database file.
     */
    public static boolean initializeDB(String dbFile) {
        // Construct the SQLite database URL
        databaseURL = "jdbc:sqlite:" + dbFile;

        // Check if the database file exists
        if (!new File(dbFile).exists()) {
            try {
                // Establish connection to the database
                connection = DriverManager.getConnection(databaseURL);

                // Creating table in database
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS servers (serverID VARCHAR(255) PRIMARY KEY, logChannelID VARCHAR(255))").executeUpdate();
                System.out.println("> SQLite DB initialized.");
                return true;
            } catch (SQLException e) {
                System.err.println("[!] SQLite error during initialization: " + e.getLocalizedMessage());
                System.err.println("[!] SQLite error code: "+ e.getErrorCode());
            }
            return false;
        } else {
            System.out.println("> SQLite DB loaded.");
            return true;
        }
    }

    /**
     * Retrieves the logChannelID associated with the given serverID and invokes the callback asynchronously with the result.
     * @param serverID The ID of the server.
     * @param callback The callback function to be invoked with the retrieved logChannelID.
     */
    public static void getLogChannel(String serverID, Callback<String> callback) {
        executor.submit(() -> {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT logChannelID FROM servers WHERE serverID = ?")) {
                stmt.setString(1, serverID);

                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    String logChannelID = resultSet.getString("logChannelID");
                    callback.onResult(logChannelID);
                } else {
                    callback.onResult(null);
                }
            } catch (SQLException e) {
                callback.onResult(null);
            }
        });
    }

    /**
     * Inserts or updates a row in the "servers" table with the provided serverID and logChannelID and invokes the callback asynchronously with the result.
     * @param serverID The ID of the server.
     * @param logChannelID The ID of the log channel associated with the server.
     * @param callback The callback function to be invoked with the result of the operation.
     */
    public static void setLogChannel(String serverID, String logChannelID, Callback<String> callback) {
        executor.submit(() -> {
            try (PreparedStatement stmt = connection.prepareStatement("REPLACE INTO servers (serverID, logChannelID) VALUES (?, ?)")) {
                stmt.setString(1, serverID);
                stmt.setString(2, logChannelID);

                if (stmt.executeUpdate() > 0) {
                    callback.onResult("true");
                } else {
                    callback.onResult("false");
                }
            } catch (SQLException e) {
                System.err.println("[!] DB error while setting log channel ID: " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * Deletes the row with the given serverID from the "servers" table and invokes the callback asynchronously with the result.
     * @param serverID The ID of the server whose logChannelID is to be deleted.
     * @param callback The callback function to be invoked with the result of the operation.
     */
    public static void deleteLogChannel(String serverID, Callback<String> callback) {
        executor.submit(() -> {
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM servers WHERE serverID = ?")) {
                stmt.setString(1, serverID);

                if (stmt.executeUpdate() > 0) {
                    callback.onResult("true");
                } else {
                    callback.onResult("false");
                }
            } catch (SQLException e) {
                System.err.println("[!] DB error while setting log channel ID: " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * Interface for asynchronous callback.
     * @param <T> The type of result passed to the callback.
     */
    public interface Callback<T> {
        void onResult(T result);
    }

}