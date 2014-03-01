package me.ifamasssxd.statsapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;


public class Database {
    protected static Connection connection;
    public int lastUpdate;
    public HashSet<String> players = new HashSet<String>();

    public boolean connect() {
        try {
            connection = DriverManager.getConnection(StatsAPI.url, StatsAPI.username, StatsAPI.password);
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
            return false;
        }
    }
    public synchronized Connection getConnection() {
        try {
            if (null != connection) {
                if (connection.isValid(1)) {
                    return connection;
                } else {
                    connection.close();
                }
            }
            connection = DriverManager.getConnection(StatsAPI.url, StatsAPI.username, StatsAPI.password);
        } catch (Exception e) {

        }
        return connection;
    }

}