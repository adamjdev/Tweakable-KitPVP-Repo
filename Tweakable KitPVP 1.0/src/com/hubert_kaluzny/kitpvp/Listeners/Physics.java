package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Physics implements Listener {

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event){
        if(KitPVP.dedicatedGame) {
            if (event.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
                if (KitPVP.soupEnable) {
                    if (!KitPVP.instaSoup) {
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        if (event.getPlayer().getHealth() + KitPVP.soupHealth > event.getPlayer().getMaxHealth()) {
                            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                        } else {
                            event.getPlayer().setHealth(event.getPlayer().getHealth() + KitPVP.soupHealth);
                        }
                        event.setCancelled(true);
                        event.getPlayer().updateInventory();
                        return;
                    } else {
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        if (event.getPlayer().getHealth() + KitPVP.soupHealth > event.getPlayer().getMaxHealth()) {
                            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                        } else {
                            event.getPlayer().setHealth(event.getPlayer().getHealth() + KitPVP.soupHealth);
                        }
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                if (event.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
                    if (KitPVP.soupEnable) {
                        if (!KitPVP.instaSoup) {
                            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                            if (event.getPlayer().getHealth() + KitPVP.soupHealth > event.getPlayer().getMaxHealth()) {
                                event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                            } else {
                                event.getPlayer().setHealth(event.getPlayer().getHealth() + KitPVP.soupHealth);
                            }
                            event.setCancelled(true);
                            event.getPlayer().updateInventory();
                            return;
                        } else {
                            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                            if (event.getPlayer().getHealth() + KitPVP.soupHealth > event.getPlayer().getMaxHealth()) {
                                event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                            } else {
                                event.getPlayer().setHealth(event.getPlayer().getHealth() + KitPVP.soupHealth);
                            }
                            event.setCancelled(true);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
	}

    /* cop
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event){
        if(KitPVP.dedicatedGame) {
            if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                    event.setCancelled(true);
                }
            }
        }
    }*/

    @EventHandler
    public void onPlayerThrow(PlayerDropItemEvent event){
        if(KitPVP.dedicatedGame) {
            if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InvDrag(InventoryDragEvent event){
        if(KitPVP.dedicatedGame) {
            if ((KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).selectingKit || KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).shopOpen) && !KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).dev) {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).inGame){
                if ((KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).selectingKit || KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).shopOpen) && !KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).dev) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void InvClick(InventoryClickEvent event){
        if(KitPVP.dedicatedGame) {
            if ((KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).selectingKit || KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).shopOpen) && !KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).dev) {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).inGame){
                if ((KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).selectingKit || KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).shopOpen) && !KitPVP.getPlayer(event.getWhoClicked().getUniqueId()).dev) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(KitPVP.dedicatedGame) {
            if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                event.setCancelled(true);
            } else {
                if (event.getBlock().getType().equals(Material.SIGN)) {
                    Sign sign = (Sign) event.getBlock().getState();
                    if (sign.getLine(0).equalsIgnoreCase("[kits]")) {
                        KitPVP.kitSignList.remove(event.getBlock().getLocation());
                    } else if (sign.getLine(0).equalsIgnoreCase("[shops]")) {
                        KitPVP.shopSignList.remove(event.getBlock().getLocation());
                    }
                }
            }
        }else{
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                event.setCancelled(true);
                if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                    event.setCancelled(true);
                } else {
                    if (event.getBlock().getType().equals(Material.SIGN)) {
                        Sign sign = (Sign) event.getBlock().getState();
                        if (sign.getLine(0).equalsIgnoreCase("[kits]")) {
                            KitPVP.kitSignList.remove(event.getBlock().getLocation());
                        } else if (sign.getLine(0).equalsIgnoreCase("[shops]")) {
                            KitPVP.shopSignList.remove(event.getBlock().getLocation());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(KitPVP.dedicatedGame) {
            if (!KitPVP.getPlayer(event.getPlayer().getUniqueId()).dev) {
                event.setCancelled(true);
            }
        }else{
            if(KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHungerLost(FoodLevelChangeEvent event){
        if(KitPVP.dedicatedGame){
            event.setCancelled(true);
            ((Player)event.getEntity()).setFoodLevel(19);
        }else{
            if(KitPVP.getPlayer(event.getEntity().getUniqueId()).inGame){
                event.setCancelled(true);
                ((Player)event.getEntity()).setFoodLevel(19);
            }
        }
    }

    @EventHandler
    public void onExplosion(BlockDamageEvent event){
        if(KitPVP.dedicatedGame) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!KitPVP.dedicatedGame){
            if(event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (!KitPVP.getPlayer(player.getUniqueId()).inGame) {
                    return;
                }
            }
        }
        if (event.getEntity() instanceof Player) {
            if(KitPVP.bloodEffectenabled) {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0, 1, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            }
            KPVPPlayer playr = KitPVP.getPlayer(event.getEntity().getUniqueId());
            if(playr.kit != null && playr.kit.immuneTo != null && playr.kit.immuneTo.contains(event.getCause())){
                event.setCancelled(true);
                return;
            }
            if (!KitPVP.getPlayer(event.getEntity().getUniqueId()).selectingKit) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
                if(event.getEntity().getLocation().distance(new Location(Bukkit.getWorld(KitPVP.worldName), KitPVP.x, KitPVP.y, KitPVP.z)) <= config.getInt("SafeDistance")){
                    event.setCancelled(true);
                }else {
                    if (event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                        if (KitPVP.getPlayer(event.getEntity().getUniqueId()).immuneToExplosion) {
                            event.setCancelled(true);
                            KPVPPlayer player = KitPVP.getPlayer(event.getEntity().getUniqueId());
                            player.immuneToExplosion = false;
                            KitPVP.savePlayerData(player);
                        }
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
