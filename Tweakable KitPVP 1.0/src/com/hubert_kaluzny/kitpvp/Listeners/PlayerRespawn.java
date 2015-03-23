package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event){
        if(!KitPVP.dedicatedGame){
            if(!KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
                return;
            }
        }
        event.getPlayer().setAllowFlight(true);
	    if(KitPVP.lobbyWorld != null){
		    event.setRespawnLocation(new Location(Bukkit.getWorld(KitPVP.lobbyWorld), KitPVP.lobbyX, KitPVP.lobbyY, KitPVP.lobbyZ));
	    }else{
		    event.setRespawnLocation(new Location(Bukkit.getWorld(KitPVP.worldName), KitPVP.x, KitPVP.y, KitPVP.z));
	    }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(KitPVP.plugin, new Runnable() {
            @Override
            public void run() {
                KPVPPlayer player = new KPVPPlayer(event.getPlayer().getUniqueId(), KitPVP.generatePlayerID());
                int invCount = ((int) Math.ceil(KitPVP.kitList.size() / 9)) * 9;
                if((int)Math.ceil(KitPVP.kitList.size() / 9) == 0){
                    invCount = 9;
                }
                Inventory inventory = Bukkit.getServer().createInventory(event.getPlayer(), invCount, "Kits");
                for(Kit kit : KitPVP.kitList){
                    if(player.playerRank.equals(PlayerRank.VETERAN)){
                        ItemMeta itemMeta = kit.displayItem.getItemMeta();
                        itemMeta.setDisplayName(kit.Name);
                        ItemStack item = kit.displayItem;
                        item.setItemMeta(itemMeta);
                        inventory.addItem(item);
                    }else if(player.playerRank.equals(PlayerRank.SOLDIER)){
                        if(kit.minRank.equals(PlayerRank.TRAINEE) || kit.minRank.equals(PlayerRank.SOLDIER)){
                            ItemMeta itemMeta = kit.displayItem.getItemMeta();
                            itemMeta.setDisplayName(kit.Name);
                            ItemStack item = kit.displayItem;
                            item.setItemMeta(itemMeta);
                            inventory.addItem(item);
                        }
                    }else if(player.playerRank.equals(PlayerRank.TRAINEE)){
                        if(kit.minRank.equals(PlayerRank.TRAINEE)){
                            ItemMeta itemMeta = kit.displayItem.getItemMeta();
                            itemMeta.setDisplayName(kit.Name);
                            ItemStack item = kit.displayItem;
                            item.setItemMeta(itemMeta);
                            inventory.addItem(item);
                        }
                    }
                }
                event.getPlayer().openInventory(inventory);
                player.selectingKit = true;
                KitPVP.savePlayerData(player);
	            event.getPlayer().setFoodLevel(19);
            }
        }, 20L);
    }
}
