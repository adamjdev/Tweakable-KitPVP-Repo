package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class setarena implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player) {
            if(args.length > 0) {
                if (args[0] != null && args[0].equalsIgnoreCase("lobby")) {
                    Player player = (Player) sender;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    config.set("lobby.worldName", player.getLocation().getWorld().getName());
                    config.set("lobby.x", player.getLocation().getX());
                    config.set("lobby.y", player.getLocation().getY());
                    config.set("lobby.z", player.getLocation().getZ());
                    try {
                        config.save(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    KitPVP.lobbyWorld = player.getLocation().getWorld().getName();
                    KitPVP.lobbyX = (int) player.getLocation().getX();
                    KitPVP.lobbyY = (int) player.getLocation().getY();
                    KitPVP.lobbyZ = (int) player.getLocation().getZ();
                    sender.sendMessage(ChatColor.BLUE + "Lobby spawn set.");
                } else if (args[0] != null && args[0].equalsIgnoreCase("arena")) {
                    Player player = (Player) sender;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    config.set("arena.worldName", player.getLocation().getWorld().getName());
                    config.set("arena.x", player.getLocation().getX());
                    config.set("arena.y", player.getLocation().getY());
                    config.set("arena.z", player.getLocation().getZ());
                    try {
                        config.save(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    KitPVP.worldName = player.getLocation().getWorld().getName();
                    KitPVP.x = (int) player.getLocation().getX();
                    KitPVP.y = (int) player.getLocation().getY();
                    KitPVP.z = (int) player.getLocation().getZ();
                    sender.sendMessage(ChatColor.BLUE + "Arena spawn set.");
                } else {
                    Player player = (Player) sender;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    config.set("arena.worldName", player.getLocation().getWorld().getName());
                    config.set("arena.x", player.getLocation().getX());
                    config.set("arena.y", player.getLocation().getY());
                    config.set("arena.z", player.getLocation().getZ());
                    try {
                        config.save(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    KitPVP.worldName = player.getLocation().getWorld().getName();
                    KitPVP.x = (int) player.getLocation().getX();
                    KitPVP.y = (int) player.getLocation().getY();
                    KitPVP.z = (int) player.getLocation().getZ();
                    sender.sendMessage(ChatColor.BLUE + "Arena spawn set.");
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Must be a player.");
        }
        return true;
    }
}
