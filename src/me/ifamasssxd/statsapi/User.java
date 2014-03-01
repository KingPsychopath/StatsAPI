package me.ifamasssxd.statsapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class User {
    private int kills = 0, deaths = 0, wins = 0, losses = 0;
    private Player p;
    private boolean loaded = false;

    public User(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public User loadPlayerStats() {
        Bukkit.getScheduler().runTaskAsynchronously(StatsAPI.getPlugin(), new Runnable() {
            public void run() {
                try (PreparedStatement pst = StatsAPI.getDB().getConnection().prepareStatement("SELECT * FROM stats WHERE username = ?")) {
                    pst.setString(1, p.getName());
                    ResultSet rs = pst.executeQuery();
                    if (!rs.first()) {
                        /* There is nothing there so dont really do anything.. */
                        return;
                    }
                    kills = rs.getInt("kills");
                    deaths = rs.getInt("deaths");
                    wins = rs.getInt("wins");
                    losses = rs.getInt("losses");
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
                loaded = true;

            }
        });
        return this;
    }

    public void savePlayerStats(final boolean remove_player) {
        /* If nothing was loaded then there is no point saving anything. */
        if (!isLoaded())
            return;
        Bukkit.getScheduler().runTask(StatsAPI.getPlugin(), new Runnable() {
            public void run() {
                try (PreparedStatement pst = StatsAPI.getDB().getConnection()
                        .prepareStatement("UPDATE stats SET kills = ?, deaths = ?, wins = ?, losses = ?")) {
                    pst.setInt(1, getKills());
                    pst.setInt(2, getDeaths());
                    pst.setInt(3, getWins());
                    pst.setInt(4, getLosses());
                    pst.executeUpdate();
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }
                if (remove_player)
                    StatsAPI.clearUser(p);
            }
        });

    }

    public ItemStack createBook(BookMeta bm) {
        BookMeta book_meta;
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        if (bm == null) {
            book_meta = (BookMeta) book.getItemMeta();
        } else {
            book_meta = bm;
        }
        /* Sets all the data here. */
        book_meta.addPage(ChatColor.BLACK.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "     Character     \n" + ChatColor.BOLD
                + "All of your stats are listed below.\n" + ChatColor.BLACK + "   " + getKills() + ChatColor.BOLD + " Kills\n" + ChatColor.BLACK + "   "
                + getDeaths() + ChatColor.BOLD + " Deaths\n" + ChatColor.BLACK + "   " + getWins() + ChatColor.BOLD + " Wins\n" + ChatColor.BLACK + "   "
                + getLosses() + ChatColor.BOLD + " Losses\n");
        book.setItemMeta(book_meta);
        return book;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public User setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public User setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }


    public User setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public User setLosses(int losses) {
        this.losses = losses;
        return this;
    }
}
