package me.phastixtv.wirtschaftssystem.managers;

import me.phastixtv.wirtschaftssystem.Wirtschaftssystem;
import me.phastixtv.wirtschaftssystem.mysql.MySQLDataType;
import me.phastixtv.wirtschaftssystem.mysql.MySQLTable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class MoneyManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MySQLTable.Condition condition = new MySQLTable.Condition("uuid", event.getPlayer().getUniqueId().toString());
        if (!tabel.exits(condition)) {
            set(event.getPlayer().getUniqueId(), 1000);
        }
    }

    private static MySQLTable tabel;

    public MoneyManager(Wirtschaftssystem pl) {
        HashMap<String, MySQLDataType> colums = new HashMap<>();
        colums.put("uuid", MySQLDataType.CHAR);
        colums.put("value", MySQLDataType.INT);

        tabel = new MySQLTable(pl.getConnection(), "money", colums);

        pl.getServer().getPluginManager().registerEvents(this, pl);

    }


    public static void set(UUID uuid, int value) {
        MySQLTable.Condition condition = new MySQLTable.Condition("uuid", uuid.toString());
        if(tabel.exits(condition)) {
            tabel.set("value", value, condition);
        } else {
            tabel.set("uuid", uuid.toString(), condition);
            tabel.set("value", value, condition);
        }
    }

    public static int get(UUID uuid) {
        MySQLTable.Condition condition = new MySQLTable.Condition("uuid", uuid.toString());
        if(tabel.exits(condition)) {
            return tabel.getInt("value", condition);
        }
        set(uuid, 0);
        return 0;
    }

}
