package me.ifamasssxd.statsapi;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsAPI extends JavaPlugin {
    private static ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
    private static Database db = new Database();
    public static String url, username, host, password;
    static StatsAPI plugin;
    File file = new File(getDataFolder(), "config.yml");

    public void onEnable() {
        plugin = this;
        if (!file.exists()) {
            saveDefaultConfig();
        }
        if (!retrieveInfo()) {
            Bukkit.getLogger().info("Invalid SQL info!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (db.connect()) {
            createDB();
            Bukkit.getLogger().info("Database connected!");
        }

    }

    public static Database getDB() {
        return db;
    }

    public void createDB() {
        try (PreparedStatement pst = getDB()
                .getConnection()
                .prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `stats` (`username` varchar(16) NOT NULL,`kills` int(11) NOT NULL DEFAULT '0', `deaths` int(11) NOT NULL DEFAULT '0', `wins` int(11) NOT NULL DEFAULT '0', `losses` int(11) NOT NULL DEFAULT '0', PRIMARY KEY (`username`), UNIQUE KEY`stats_player_username` (`username`)) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;")) {
            pst.executeUpdate();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

    }

    public static User getUser(Player p) {
        if (users.containsKey(p.getName()))
            return users.get(p.getName());
        return new User(p);
    }

    public boolean retrieveInfo() {
        if (getConfig().getString("MYSQL.Host") == "" || getConfig().getString("MYSQL.Username") == "") {
            Bukkit.getLogger().info(ChatColor.RED + "Please enter valid SQL data and restart the server...");
            return false;
        }
        host = getConfig().getString("MYSQL.Host");
        username = getConfig().getString("MYSQL.Username");
        password = getConfig().getString("MYSQL.Password");
        url = "jdbc:mysql://" + getConfig().getString("MYSQL.Host") + ":" + getConfig().getString("MYSQL.Port") + "/" + getConfig().getString("MYSQL.Database");
        return true;
    }

    public static StatsAPI getPlugin() {
        return plugin;
    }

    public static void clearUser(Player p) {
        if (users.containsKey(p.getName())) {
            users.remove(p.getName());
        }
    }
}
