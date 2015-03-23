package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class points implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length > 0) {
            if (args[0].equalsIgnoreCase("set")) {
                if (isPlayerOnline(args[1])) {
                    KPVPPlayer player = KitPVP.getPlayer(getUserUUID(args[1]));
                    player.points = Integer.parseInt(args[2]);
                    if (KitPVP.useVault) {
                        KitPVP.economy.withdrawPlayer(Bukkit.getPlayer(player.uuid), KitPVP.economy.getBalance(Bukkit.getPlayer(player.uuid)));
                        KitPVP.economy.depositPlayer(Bukkit.getPlayer(player.uuid), player.points);
                        sender.sendMessage(KitPVP.pointName + " updated for " + args[1]);
                        KitPVP.updateScoreboard(player);
                    }
                    KitPVP.savePlayerData(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "Player must be online!");
                }
            } else if (args[0].equalsIgnoreCase("deposit")) {
                if (isPlayerOnline(args[1])) {
                    KPVPPlayer player = KitPVP.getPlayer(getUserUUID(args[1]));
                    player.points += Integer.parseInt(args[2]);
                    if (KitPVP.useVault) {
                        KitPVP.economy.depositPlayer(Bukkit.getPlayer(player.uuid), Integer.parseInt(args[2]));
                    }
                    KitPVP.savePlayerData(player);
                    sender.sendMessage(KitPVP.pointName + " updated for " + args[1]);
                    KitPVP.updateScoreboard(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "Player must be online!");
                }
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                if (isPlayerOnline(args[1])) {
                    KPVPPlayer player = KitPVP.getPlayer(getUserUUID(args[1]));
                    player.points -= Integer.parseInt(args[2]);
                    if (KitPVP.useVault) {
                        KitPVP.economy.withdrawPlayer(Bukkit.getPlayer(player.uuid), Integer.parseInt(args[2]));
                    }
                    KitPVP.savePlayerData(player);
                    sender.sendMessage(KitPVP.pointName + " updated for " + args[1]);
                    KitPVP.updateScoreboard(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "Player must be online!");
                }
            } else if (args[0].equalsIgnoreCase("balance")) {
                if (isPlayerOnline(args[1])) {
                    KPVPPlayer player = KitPVP.getPlayer(getUserUUID(args[1]));
                    sender.sendMessage("Balance : " + player.points);
                } else {
                    sender.sendMessage(ChatColor.RED + "Player must be online!");
                }
            }else{
                sender.sendMessage(ChatColor.RED + "/points <set/withdraw/deposit/balance> <playerName> <amount>");
            }
        }else{
            sender.sendMessage(ChatColor.RED + "/points <set/withdraw/deposit/balance> <playerName> <amount>");
        }
        return true;
    }

    public UUID getUserUUID(String username){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getName().equals(username)){
                return player.getUniqueId();
            }
        }
        return null;
    }

    public boolean isPlayerOnline(String username){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getName().equals(username)){
                return true;
            }
        }
        return false;
    }
}
