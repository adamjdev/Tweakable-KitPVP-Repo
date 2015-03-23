package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.RewardType;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.KillStreaks.KillStreak;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PVPEvent implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if(!KitPVP.dedicatedGame){
                if(!KitPVP.getPlayer(victim.getUniqueId()).inGame){
                    return;
                }
            }
            if(victim.getAllowFlight() || (event.getDamager() instanceof Player && ((Player) event.getDamager()).getAllowFlight())){
                event.setCancelled(true);
                return;
            }
            if (!KitPVP.getPlayer(victim.getUniqueId()).selectingKit) {
                if (victim.getHealth() - event.getDamage() <= 0) {
                    if(!victim.isDead() && !event.getDamager().isDead()) {
                        if (event.getDamager() instanceof Player) {
                            Player damager = (Player) event.getDamager();
                            if (!KitPVP.getPlayer(damager.getUniqueId()).selectingKit) {
                                KPVPPlayer newVictim = KitPVP.getPlayer(victim.getUniqueId());
                                newVictim.deaths++;
                                KitPVP.savePlayerData(newVictim);
                                KPVPPlayer newDamager = KitPVP.getPlayer(damager.getUniqueId());
                                newDamager.kills++;
                                newDamager.points += KitPVP.killRewardPoints;
                                KitPVP.savePlayerData(newDamager);
                                KitPVP.updateScoreboard(newDamager);
                                KitPVP.updateScoreboard(newVictim);
                                KitPVP.updatePlayer(newDamager);
                                KitPVP.updatePlayer(newVictim);
                                victim.setHealth(0);
                            } else {
                                event.setCancelled(true);
                            }
                        } else if (event.getDamager() instanceof Arrow) {
                            Arrow arrow = (Arrow) event.getDamager();
                            if (arrow.getShooter() != null) {
                                if (arrow.getShooter() instanceof Player) {
                                    Player damager = (Player) arrow.getShooter();
                                    KPVPPlayer newVictim = KitPVP.getPlayer(victim.getUniqueId());
                                    newVictim.deaths++;
                                    KitPVP.savePlayerData(newVictim);
                                    KPVPPlayer newDamager = KitPVP.getPlayer(damager.getUniqueId());
                                    newDamager.kills++;
                                    newDamager.points += KitPVP.killRewardPoints;
                                    KitPVP.savePlayerData(newDamager);
                                    KitPVP.updateScoreboard(newDamager);
                                    KitPVP.updateScoreboard(newVictim);
                                    KitPVP.updatePlayer(newDamager);
                                    KitPVP.updatePlayer(newVictim);
                                    victim.setHealth(0);
                                } else {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }else{
                event.setCancelled(true);
            }
        }

        if(event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).getHealth() - event.getDamage() <= 0) {
                if (event.getDamager() instanceof Player) {
                    if (event.getEntity() instanceof Player) {
                        KPVPPlayer victim = KitPVP.getPlayer(event.getEntity().getUniqueId());
                        victim.killstreak = 0;
                        KitPVP.savePlayerData(victim);
                        KPVPPlayer killer = KitPVP.getPlayer(event.getDamager().getUniqueId());
                        killer.killstreak++;
                        for (KillStreak killStreak : KitPVP.killStreaks) {
                            if (killStreak.kills == killer.killstreak) {
                                Bukkit.getPlayer(killer.uuid).sendMessage(killStreak.Message);
                                if (killStreak.rewardType.equals(RewardType.CURRENCY)) {
                                    killer.points += killStreak.reward_currency;
                                } else if (killStreak.rewardType.equals(RewardType.ITEM)) {
                                    for (int i = 0; i < Bukkit.getPlayer(killer.uuid).getInventory().getContents().length; i++) {
                                        if (Bukkit.getPlayer(killer.uuid).getInventory().getContents()[i].getType().equals(Material.MUSHROOM_SOUP) || Bukkit.getPlayer(killer.uuid).getInventory().getContents()[i].getType().equals(Material.AIR)) {
                                            ItemStack item = new ItemStack(killStreak.reward_item);
                                            item.setAmount(killStreak.amount);
                                            ItemMeta meta = item.getItemMeta();
                                            meta.setDisplayName(killStreak.displayName);
                                            meta.setLore(killStreak.lores);
                                            item.setItemMeta(meta);
                                            Bukkit.getPlayer(killer.uuid).getInventory().setItem(i, item);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        KitPVP.savePlayerData(killer);
                        KitPVP.updateScoreboard(killer);
                        KitPVP.updateScoreboard(victim);
                    }
                }
            }
        }
    }
}
