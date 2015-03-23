package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.KitPVP;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Hubert Kaluzny
 * http://hubert-kaluzny.com
 * on 18/01/2015.
 */
public class kitpvp implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("join")) {
                    for (KPVPPlayer p : KitPVP.playerList) {
                        if (p.uuid.equals(player.getUniqueId())) {
                            if (p.inGame) {
                                player.sendMessage(ChatColor.BLUE + "You're already in game!");
                            } else {
                                p.inGame = true;
                                player.sendMessage(ChatColor.BLUE + "You have joined the game!");
                                player.teleport(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));

                                p.storedInventory = player.getInventory().getContents();
                                p.storedHelmet = player.getInventory().getHelmet();
                                p.storedChestplate = player.getInventory().getChestplate();
                                p.storedLeggings = player.getInventory().getLeggings();
                                p.storedBoots = player.getInventory().getBoots();

                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);
                                player.updateInventory();
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    for (KPVPPlayer p : KitPVP.playerList) {
                        if (p.uuid.equals(player.getUniqueId())) {
                            if (p.inGame) {
                                p.inGame = false;
                                player.sendMessage(ChatColor.BLUE + "You have left the game!");
                                player.teleport(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));
                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);

                                player.setScoreboard(KitPVP.manager.getNewScoreboard());

                                player.getInventory().setContents(p.storedInventory);
                                player.getInventory().setHelmet(p.storedHelmet);
                                player.getInventory().setChestplate(p.storedChestplate);
                                player.getInventory().setLeggings(p.storedLeggings);
                                player.getInventory().setBoots(p.storedBoots);

                                player.updateInventory();
                            } else {
                                player.sendMessage(ChatColor.BLUE + "You are not in a game!");
                            }
                        }
                    }
                }
            }else{
                player.sendMessage(ChatColor.RED + "[Tweakable KitPVP]");
                player.sendMessage(ChatColor.RED + "/KitPVP join - Joins the server's kitpvp arena");
                player.sendMessage(ChatColor.RED + "/KitPVP leave - Leaves the server's kitpvp arena");
            }
        }
        return true;
    }
}
