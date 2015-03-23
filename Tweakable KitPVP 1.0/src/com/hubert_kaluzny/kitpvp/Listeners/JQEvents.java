package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

public class JQEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event){
        if(KitPVP.dedicatedGame) {
            event.getPlayer().setFoodLevel(19);
            if (KitPVP.lobbyWorld != null) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));
            } else {
                if (Bukkit.getWorld(KitPVP.worldName) != null) {
                    event.getPlayer().teleport(new Location(Bukkit.getWorld(KitPVP.worldName), KitPVP.x, KitPVP.y, KitPVP.z));
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED + "You need to set up an arena and a lobby!");
                    event.getPlayer().sendMessage(ChatColor.RED + "Do that with " + ChatColor.BLUE + "/setarena arena");
                    event.getPlayer().sendMessage(ChatColor.RED + "and" + ChatColor.BLUE + "/setarena lobby");
                    event.getPlayer().sendMessage(ChatColor.RED + "Then restart the server! (Don't reload)");
                }
            }
        }
        if(KitPVP.dedicatedGame) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().getInventory().setArmorContents(null);
            event.getPlayer().setAllowFlight(true);
        }

        KPVPPlayer player = new KPVPPlayer(event.getPlayer().getUniqueId(), KitPVP.generatePlayerID());
        KitPVP.playerList.add(player);
        KitPVP.READplayerList = KitPVP.playerList;

        if(KitPVP.dedicatedGame) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(KitPVP.plugin, new Runnable() {
                @Override
                public void run() {
                    KPVPPlayer player = new KPVPPlayer(event.getPlayer().getUniqueId(), KitPVP.generatePlayerID());
                    player.selectingKit = true;
                    KitPVP.savePlayerData(player);
                    KitPVP.updateScoreboard(player);
                    if (KitPVP.showGUIOnLogin) {
                        int invCount = ((int) Math.ceil(KitPVP.kitList.size() / 9)) * 9;
                        if ((int) Math.ceil(KitPVP.kitList.size() / 9) == 0) {
                            invCount = 9;
                        }
                        Inventory inventory = Bukkit.getServer().createInventory(event.getPlayer(), invCount, "Kits");
                        for (Kit kit : KitPVP.kitList) {
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
                        event.getPlayer().openInventory(inventory);
                    }
                }
            }, 20L);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        for (KPVPPlayer player : KitPVP.playerList) {
            if(player.inGame){
                event.getPlayer().getInventory().setContents(player.storedInventory);
                event.getPlayer().getInventory().setHelmet(player.storedHelmet);
                event.getPlayer().getInventory().setChestplate(player.storedChestplate);
                event.getPlayer().getInventory().setLeggings(player.storedLeggings);
                event.getPlayer().getInventory().setBoots(player.storedBoots);
                event.getPlayer().teleport(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));
            }
            if (player.uuid.equals(event.getPlayer().getUniqueId())) {
                if(KitPVP.useSQL) {
                    KitPVP.sqlManager.UpdatePlayerData(player);
                }else{
                    File playerFile = new File(KitPVP.plugin.getDataFolder() + "/players/", player.uuid.toString() + ".yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                    config.set("kills", player.kills);
                    config.set("deaths", player.deaths);
                    config.set("points", player.points);
                    config.set("rank", player.playerRank.toString());
                    try {
                        config.save(playerFile);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                KitPVP.playerList.remove(player);
            }
        }
        KitPVP.READplayerList = KitPVP.playerList;
    }
}
