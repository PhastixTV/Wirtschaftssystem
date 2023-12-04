package me.phastixtv.wirtschaftssystem.commands;

import me.phastixtv.wirtschaftssystem.Wirtschaftssystem;
import me.phastixtv.wirtschaftssystem.managers.MoneyManager;
import me.phastixtv.wirtschaftssystem.utilitys.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyCommand implements CommandExecutor {

    private final String adminPermission = "admin";
    final Wirtschaftssystem plugin;

    public MoneyCommand(Wirtschaftssystem plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Das darf nur ein Spieler tun!");
                    return;
                }

                if(args.length == 0) {
                    sendMoney(sender, ((Player) sender));
                    return;
                }
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("help")) {
                        sendHelp(sender);
                        return;
                    }
                }
                if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("pay")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        Player player = (Player) sender;
                        if(target != null) {
                            if (!Utility.isInt(args[2])) {
                                sender.sendMessage(ChatColor.RED + "Der eingegebene Wert " + args[2] + "ist keine gültife Nummer!");
                                return;
                            }
                            if (!(MoneyManager.get(player.getUniqueId())-Integer.parseInt(args[2]) >= 0)) {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht so viel Geld");
                                return;
                            }
                            payMoney(sender, ((Player) sender), target, Integer.parseInt(args[2]));
                            return;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Der Spieler " + args[1] + " konnte nicht gefunden werden!");
                            return;
                        }
                    }
                }

                if (sender.hasPermission(adminPermission)) {
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("get")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            Player player = (Player) sender;
                            if(target != null) {
                                sendMoney(sender, target);
                                return;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Der Spieler " + args[1] + " konnte nicht gefunden werden!");
                                return;
                            }
                        }
                    }
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("set")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            Player player = (Player) sender;
                            if(target != null) {
                                if (!Utility.isInt(args[2])) {
                                    sender.sendMessage(ChatColor.RED + "Der eingegebene Wert " + args[2] + "ist keine gültife Nummer!");
                                    return;
                                }
                                setMoney(sender, target, Integer.parseInt(args[2]));
                                return;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Der Spieler " + args[1] + " konnte nicht gefunden werden!");
                                return;
                            }
                        }
                    }
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            Player player = (Player) sender;
                            if(target != null) {
                                if (!Utility.isInt(args[2])) {
                                    sender.sendMessage(ChatColor.RED + "Der eingegebene Wert " + args[2] + "ist keine gültife Nummer!");
                                    return;
                                }
                                setMoney(sender, target, Integer.parseInt(args[2]) + MoneyManager.get(target.getUniqueId()));
                                return;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Der Spieler " + args[1] + " konnte nicht gefunden werden!");
                                return;
                            }
                        }
                    }
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("remove")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            Player player = (Player) sender;
                            if(target != null) {
                                if (!Utility.isInt(args[2])) {
                                    sender.sendMessage(ChatColor.RED + "Der eingegebene Wert " + args[2] + "ist keine gültife Nummer!");
                                    return;
                                }
                                setMoney(sender, target,MoneyManager.get(target.getUniqueId()) - Integer.parseInt(args[2]));
                                return;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Der Spieler " + args[1] + " konnte nicht gefunden werden!");
                                return;
                            }
                        }
                    }
                }

                sender.sendMessage(ChatColor.RED + "Fehler! Benutze /money help für eine Hilfestellung!");
            }
        });
        return true;
    }

    private void sendMoney(CommandSender sender, Player player) {
        if(sender.getName().equalsIgnoreCase(player.getName()))
            sender.sendMessage(ChatColor.GREEN + "Du hast " + MoneyManager.get(player.getUniqueId()) + " Geld!");
        else
            sender.sendMessage(ChatColor.GREEN + "Der Spieler " + player.getName() + " hat " + MoneyManager.get(player.getUniqueId()) + " Geld!");
    }

    private void setMoney(CommandSender sender, Player player, int value) {
        MoneyManager.set(player.getUniqueId(), value);
        player.sendMessage(ChatColor.GREEN + "Du hast nun " + value);
        sender.sendMessage(ChatColor.GREEN + "Der Spieler " + player.getName() + "hat nun " + value + " Geld.");
    }

    private void payMoney(CommandSender sender, Player from, Player target, int value) {
        if(sender.getName().equalsIgnoreCase(from.getName())) {
            sender.sendMessage(ChatColor.GREEN + "Du hast " + target.getName() + " " + value + " gegeben!");
            target.sendMessage(ChatColor.GREEN + "Der Spieler " + from.getName() + " hat dir " + value + " gegeben!");
            MoneyManager.set(from.getUniqueId(), MoneyManager.get(from.getUniqueId()) - value);
            MoneyManager.set(target.getUniqueId(), MoneyManager.get(target.getUniqueId()) + value);
        } else {
            sender.sendMessage(ChatColor.GREEN + "Der Spieler " + from.getName() + " hat " + target.getName() + " " + value + " gegeben!");
            from.sendMessage(ChatColor.GREEN + "Du hast " + target.getName() + " " + value + " gegeben!");
            target.sendMessage(ChatColor.GREEN + "Der Spieler " + from.getName() + " hat dir " + value + " gegeben!");
            MoneyManager.set(from.getUniqueId(), MoneyManager.get(from.getUniqueId()) - value);
            MoneyManager.set(target.getUniqueId(), MoneyManager.get(target.getUniqueId()) + value);
        }
    }


    private void sendHelp(CommandSender sender) {
        String color = ChatColor.BLUE.toString();
        String commandColor = ChatColor.GRAY.toString();
        String arrow = ChatColor.WHITE.toString() + "→";

        sender.sendMessage(color + ChatColor.BOLD + "------- Help: Money -------");
        sender.sendMessage(commandColor + "/money" +  arrow + color +" Zeigt dir dein Geld an.");
        sender.sendMessage(commandColor + "/money pay <player> <value>" +  arrow + color +" Überweise dem Spieler Geld.");
        if (sender.hasPermission(adminPermission)) {
            sender.sendMessage(commandColor + "/money get <player>" +  arrow + color +" Zeigt dir das Geld von anderen Spieler an.");
            sender.sendMessage(commandColor + "/money set <player> <value>" +  arrow + color +" Zeigt dir das Geld von anderen Spieler an.");
            sender.sendMessage(commandColor + "/money add <player> <value>" +  arrow + color +" Zeigt dir das Geld von anderen Spieler an.");
            sender.sendMessage(commandColor + "/money remove <player> <value>" +  arrow + color +" Zeigt dir das Geld von anderen Spieler an.");
        }
    }
}
