package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class kit implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player) {
			Player playr = (Player) sender;

			if(!KitPVP.dedicatedGame){
				if(!KitPVP.getPlayer(playr.getUniqueId()).inGame){
					playr.sendMessage(ChatColor.BLUE + "Not in game!");
					return true;
				}
			}

			playr.setAllowFlight(true);
			playr.setHealth(playr.getMaxHealth());
			if (KitPVP.lobbyWorld != null) {
				playr.teleport(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));
			} else {
				playr.teleport(new Location(Bukkit.getWorld(KitPVP.worldName), KitPVP.x, KitPVP.y, KitPVP.z));
			}
			playr.getInventory().clear();
			playr.getInventory().setArmorContents(null);
			int invCount = ((int) Math.ceil(KitPVP.kitList.size() / 9)) * 9;
			if ((int) Math.ceil(KitPVP.kitList.size() / 9) == 0) {
				invCount = 9;
			}
			Inventory inventory = Bukkit.getServer().createInventory(playr, invCount, "Kits");
			KPVPPlayer player = KitPVP.getPlayer(playr.getUniqueId());
			for (Kit kit : KitPVP.kitList) {
				if (kit.permission != null) {
					if (KitPVP.useVault) {
						if (KitPVP.permission.playerHas(playr, kit.permission)) {
							ItemMeta itemMeta = kit.displayItem.getItemMeta();
							itemMeta.setDisplayName(kit.Name);
							ItemStack item = kit.displayItem;
							item.setItemMeta(itemMeta);
							inventory.addItem(item);
						} else {
							if (player.playerRank.equals(PlayerRank.VETERAN)) {
								ItemMeta itemMeta = kit.displayItem.getItemMeta();
								itemMeta.setDisplayName(kit.Name);
								ItemStack item = kit.displayItem;
								item.setItemMeta(itemMeta);
								inventory.addItem(item);
							} else if (player.playerRank.equals(PlayerRank.SOLDIER)) {
								if (kit.minRank.equals(PlayerRank.TRAINEE) || kit.minRank.equals(PlayerRank.SOLDIER)) {
									ItemMeta itemMeta = kit.displayItem.getItemMeta();
									itemMeta.setDisplayName(kit.Name);
									ItemStack item = kit.displayItem;
									item.setItemMeta(itemMeta);
									inventory.addItem(item);
								}
							} else if (player.playerRank.equals(PlayerRank.TRAINEE)) {
								if (kit.minRank.equals(PlayerRank.TRAINEE)) {
									ItemMeta itemMeta = kit.displayItem.getItemMeta();
									itemMeta.setDisplayName(kit.Name);
									ItemStack item = kit.displayItem;
									item.setItemMeta(itemMeta);
									inventory.addItem(item);
								}
							}
						}
					} else {
						if (playr.hasPermission(kit.permission)) {
							ItemMeta itemMeta = kit.displayItem.getItemMeta();
							itemMeta.setDisplayName(kit.Name);
							ItemStack item = kit.displayItem;
							item.setItemMeta(itemMeta);
							inventory.addItem(item);
						} else {
							if (player.playerRank.equals(PlayerRank.VETERAN)) {
								ItemMeta itemMeta = kit.displayItem.getItemMeta();
								itemMeta.setDisplayName(kit.Name);
								ItemStack item = kit.displayItem;
								item.setItemMeta(itemMeta);
								inventory.addItem(item);
							} else if (player.playerRank.equals(PlayerRank.SOLDIER)) {
								if (kit.minRank.equals(PlayerRank.TRAINEE) || kit.minRank.equals(PlayerRank.SOLDIER)) {
									ItemMeta itemMeta = kit.displayItem.getItemMeta();
									itemMeta.setDisplayName(kit.Name);
									ItemStack item = kit.displayItem;
									item.setItemMeta(itemMeta);
									inventory.addItem(item);
								}
							} else if (player.playerRank.equals(PlayerRank.TRAINEE)) {
								if (kit.minRank.equals(PlayerRank.TRAINEE)) {
									ItemMeta itemMeta = kit.displayItem.getItemMeta();
									itemMeta.setDisplayName(kit.Name);
									ItemStack item = kit.displayItem;
									item.setItemMeta(itemMeta);
									inventory.addItem(item);
								}
							}
						}
					}
				} else {
					if (player.playerRank.equals(PlayerRank.VETERAN)) {
						ItemMeta itemMeta = kit.displayItem.getItemMeta();
						itemMeta.setDisplayName(kit.Name);
						ItemStack item = kit.displayItem;
						item.setItemMeta(itemMeta);
						inventory.addItem(item);
					} else if (player.playerRank.equals(PlayerRank.SOLDIER)) {
						if (kit.minRank.equals(PlayerRank.TRAINEE) || kit.minRank.equals(PlayerRank.SOLDIER)) {
							ItemMeta itemMeta = kit.displayItem.getItemMeta();
							itemMeta.setDisplayName(kit.Name);
							ItemStack item = kit.displayItem;
							item.setItemMeta(itemMeta);
							inventory.addItem(item);
						}
					} else if (player.playerRank.equals(PlayerRank.TRAINEE)) {
						if (kit.minRank.equals(PlayerRank.TRAINEE)) {
							ItemMeta itemMeta = kit.displayItem.getItemMeta();
							itemMeta.setDisplayName(kit.Name);
							ItemStack item = kit.displayItem;
							item.setItemMeta(itemMeta);
							inventory.addItem(item);
						}
					}
				}
			}
				playr.openInventory(inventory);
				player.selectingKit = true;
				KitPVP.savePlayerData(player);
        }
        return true;
    }
}
