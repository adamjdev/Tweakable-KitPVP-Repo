package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class InventorySelect implements Listener {

    @EventHandler
    public void InventoryInteract(InventoryInteractEvent event){
        if(!KitPVP.dedicatedGame){
            if(!KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).inGame){
                return;
            }
        }
	    if(KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).selectingKit || KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).shopOpen) {
		    event.setCancelled(true);
	    }
    }

    @EventHandler
    public void onInventorySelect(InventoryClickEvent event){
        if(!KitPVP.dedicatedGame){
            if(!KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).inGame){
                return;
            }
        }
        if (event.getInventory().getType().equals(InventoryType.CHEST)) {
            final KPVPPlayer player = KitPVP.getPlayer(event.getWhoClicked().getUniqueId());
            for (Kit kit : KitPVP.kitList) {
                if(event.getCurrentItem() != null && event.getCurrentItem().getType() != null) {
                    if (event.getCurrentItem().getType().equals(kit.displayItem.getType())) {
                        if (!player.shopOpen && player.selectingKit) {
                            ((Player) event.getWhoClicked()).setAllowFlight(false);
                            player.wearKit(kit);
                            player.selectingKit = false;
                            KitPVP.savePlayerData(player);
                            KitPVP.updateScoreboard(player);
                            event.getCurrentItem().setType(Material.AIR);
                            event.getWhoClicked().closeInventory();
                            event.setCancelled(true);
                        }
                    }
                }
            }
            if (player.shopOpen) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                if(event.getCurrentItem() != null) {
                    if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                        if (event.getCurrentItem().getData().getData() == DyeColor.LIME.getData()) {
                            if (player.playerRank == PlayerRank.SOLDIER) {
                                if (player.points >= config.getInt("SoldierToVeteranCost")) {
                                    player.points -= config.getInt("SoldierToVeteranCost");
                                    player.playerRank = PlayerRank.VETERAN;
                                    Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.GREEN + "You have been promoted to veteran rank!");
                                    event.setCancelled(true);
                                    KitPVP.updateScoreboard(player);
                                    event.getCurrentItem().setType(Material.AIR);
                                    event.getWhoClicked().closeInventory();
                                    KitPVP.updatePlayer(player);
                                } else {
                                    Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.RED + "You do not have enough " + KitPVP.pointName + " to be promoted.");
                                }
                            } else if (player.playerRank == PlayerRank.TRAINEE) {
                                if (player.points >= config.getInt("TraineeToSoldierCost")) {
                                    player.points -= config.getInt("TraineeToSoldierCost");
                                    player.playerRank = PlayerRank.SOLDIER;
                                    Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.GREEN + "You have been promoted to soldier rank!");
                                    event.setCancelled(true);
                                    KitPVP.updateScoreboard(player);
                                    event.getCurrentItem().setType(Material.AIR);
                                    event.getWhoClicked().closeInventory();
                                    KitPVP.updatePlayer(player);
                                } else {
                                    Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.RED + "You do not have enough " + KitPVP.pointName + " to be promoted.");
                                }
                            }
                            KitPVP.savePlayerData(player);
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.IRON_SWORD)) {
                        if (player.kit != null) {
                            for (ItemStack item : event.getWhoClicked().getInventory().getContents()) {
                                if (player.points >= config.getInt("SharpnessEnchantCost")) {
                                    if (item != null && item.getType() != null)
                                        if (item.getType().equals(Material.WOOD_SWORD) || item.getType().equals(Material.STONE_SWORD) || item.getType().equals(Material.IRON_SWORD) || item.getType().equals(Material.DIAMOND_SWORD)) {
                                            ItemMeta swordMeta = item.getItemMeta();
                                            if (swordMeta.getEnchantLevel(Enchantment.DAMAGE_ALL) < 10) {
                                                swordMeta.addEnchant(Enchantment.DAMAGE_ALL, swordMeta.getEnchantLevel(Enchantment.DAMAGE_ALL) + 1, true);
                                                item.setItemMeta(swordMeta);
                                                player.points -= config.getInt("SharpnessEnchantCost");
                                                KitPVP.savePlayerData(player);
                                                event.setCancelled(true);
                                                KitPVP.updateScoreboard(player);
                                                event.getCurrentItem().setType(Material.AIR);
                                                event.getWhoClicked().closeInventory();
                                                KitPVP.updatePlayer(player);
                                                return;
                                            } else {
                                                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + item.getItemMeta().getDisplayName() + " can not be enchanted with sharpness!!");
                                            }
                                        }
                                } else {
                                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to enchant " + item.getItemMeta().getDisplayName());
                                }
                            }
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.FLINT_AND_STEEL)) {
                        if (player.kit != null) {
                            for (ItemStack item : event.getWhoClicked().getInventory().getContents()) {
                                if (item != null && item.getType() != null) {
                                    if (item.getType().equals(Material.WOOD_SWORD) || item.getType().equals(Material.STONE_SWORD) || item.getType().equals(Material.IRON_SWORD) || item.getType().equals(Material.DIAMOND_SWORD)) {
                                        ItemMeta itemMeta = item.getItemMeta();
                                        if (player.points >= config.getInt("FireAspectEnchantCost")) {
                                            if (!itemMeta.hasEnchant(Enchantment.FIRE_ASPECT)) {
                                                itemMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
	                                            item.setItemMeta(itemMeta);
                                                player.points -= config.getInt("FireAspectEnchantCost");
                                                event.setCancelled(true);
                                                KitPVP.updateScoreboard(player);
                                                event.getCurrentItem().setType(Material.AIR);
                                                event.getWhoClicked().closeInventory();
                                                KitPVP.updatePlayer(player);
                                                return;
                                            } else {
                                                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + itemMeta.getDisplayName() + " already has a fire aspect enchant!");
                                            }
                                        } else {
                                            ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to enchant " + itemMeta.getDisplayName());
                                        }
                                    }
                                }
                            }
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.IRON_CHESTPLATE)) {
                        if (player.kit != null) {
                            if (player.points >= config.getInt("ProtectionEnchantCost")) {
                                player.points -= config.getInt("ProtectionEnchantCost");
                                for (ItemStack item : event.getWhoClicked().getInventory().getArmorContents()) {
                                    ItemMeta swordMeta = item.getItemMeta();
                                    if (swordMeta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) < 4) {
                                        swordMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, swordMeta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) + 1, true);
                                        item.setItemMeta(swordMeta);
                                        KitPVP.savePlayerData(player);
                                        event.setCancelled(true);
                                        KitPVP.updateScoreboard(player);
                                        event.getCurrentItem().setType(Material.AIR);
                                        event.getWhoClicked().closeInventory();
                                        KitPVP.updatePlayer(player);
                                    } else {
                                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + item.getType().toString().replaceAll("_", " ").toLowerCase() + " can not be enchanted with protection!");
                                    }
                                }
                            } else {
                                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to enchant your armour!");
                            }
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.MUSHROOM_SOUP)) {
                        if (player.kit != null) {
                            if (player.points >= config.getInt("LowHealthRegenCost")) {
                                player.points -= config.getInt("LowHealthRegenCost");
                                Bukkit.getPlayer(player.uuid).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
                                KitPVP.savePlayerData(player);
                                event.setCancelled(true);
                                KitPVP.updateScoreboard(player);
                                event.getCurrentItem().setType(Material.AIR);
                                event.getWhoClicked().closeInventory();
                                KitPVP.updatePlayer(player);

                            } else {
                                Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to buy low health regeneration.");
                            }
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.APPLE)) {
                        if (player.kit != null) {
                            if (player.points >= config.getInt("MediumHealthRegenCost")) {
                                player.points -= config.getInt("MediumHealthRegenCost");
                                Bukkit.getPlayer(player.uuid).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
                                KitPVP.savePlayerData(player);
                                event.setCancelled(true);
                                KitPVP.updateScoreboard(player);
                                event.getCurrentItem().setType(Material.AIR);
                                event.getWhoClicked().closeInventory();
                                KitPVP.updatePlayer(player);
                            } else {
                                Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to buy medium health regeneration.");
                            }
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {
                        if (player.kit != null) {
                            if (player.points >= config.getInt("HighHealthRegenCost")) {
                                player.points -= config.getInt("HighHealthRegenCost");
                                Bukkit.getPlayer(player.uuid).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 2));
                                KitPVP.savePlayerData(player);
                                event.setCancelled(true);
                                KitPVP.updateScoreboard(player);
                                event.getCurrentItem().setType(Material.AIR);
                                event.getWhoClicked().closeInventory();
                                KitPVP.updatePlayer(player);
                            } else {
                                Bukkit.getPlayer(player.uuid).sendMessage(ChatColor.RED + "Not enough " + KitPVP.pointName + " to buy high health regeneration.");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getType().equals(InventoryType.CHEST)){
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).shopOpen){
                KPVPPlayer player = KitPVP.getPlayer(event.getPlayer().getUniqueId());
                player.shopOpen = false;
                KitPVP.savePlayerData(player);
            }
        }
    }
}

