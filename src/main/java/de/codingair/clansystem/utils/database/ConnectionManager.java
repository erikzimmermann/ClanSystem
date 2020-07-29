package de.codingair.clansystem.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {

    private static String driver;
    private static String fqdn;
    private static String database;
    private static String username;
    private static String password;

    public ConnectionManager(String fqdn, String database, String username, String password) throws SQLException {
        if (ConnectionManager.driver == null) {
            ConnectionManager.driver = "mysql";
        }
        if (ConnectionManager.fqdn == null) {
            ConnectionManager.fqdn = "//" + fqdn + "/";
        }
        if (ConnectionManager.database == null) {
            ConnectionManager.database = database;
        }
        if (ConnectionManager.username == null) {
            ConnectionManager.username = username;
        }
        if (ConnectionManager.password == null) {
            ConnectionManager.password = password;
        }

        testconnection();
    }

    public static void testconnection() throws SQLException {
        stopConnection(startConnection());
    }

    public static Connection startConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:" + ConnectionManager.driver + ":" + ConnectionManager.fqdn + ConnectionManager.database, ConnectionManager.username, ConnectionManager.password);
    }

    public static void stopConnection(Connection connect) throws SQLException {
        if (connect != null) {
            connect.close();
        }
    }
}

