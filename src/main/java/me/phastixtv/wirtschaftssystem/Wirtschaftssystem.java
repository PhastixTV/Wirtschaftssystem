package me.phastixtv.wirtschaftssystem;

import me.phastixtv.wirtschaftssystem.commands.MoneyCommand;
import me.phastixtv.wirtschaftssystem.managers.MoneyManager;
import me.phastixtv.wirtschaftssystem.mysql.MySQLConnection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Wirtschaftssystem extends JavaPlugin {

    MySQLConnection connection;
    MoneyManager moneyManager;

    @Override
    public void onEnable() {
        connection = new MySQLConnection("localhost", 3306, "minecraft", "minecraft", "minecraft");
        moneyManager = new MoneyManager(this);

        getCommand("money").setExecutor(new MoneyCommand(this));
    }

    @Override
    public void onDisable() {
        connection.disconnect();
    }

    public MySQLConnection getConnection() {
        return connection;
    }
}
