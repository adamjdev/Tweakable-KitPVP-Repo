package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class dev implements CommandExecutor {

        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            if(sender instanceof Player){
                Player player = (Player)sender;
                KPVPPlayer playr = KitPVP.getPlayer(player.getUniqueId());
                if(playr.dev){
                    playr.dev = false;
                    player.sendMessage(ChatColor.BLUE + "Dev mode disabled");
                }else {
                    playr.dev = true;
                    player.sendMessage(ChatColor.BLUE + "Dev mode enabled");
                }
                KitPVP.savePlayerData(playr);
            }
            return true;
        }
}
