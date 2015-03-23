package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.RewardType;
import com.hubert_kaluzny.kitpvp.Instances.DeathSign;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.KillStreaks.KillStreak;
import com.hubert_kaluzny.kitpvp.Instances.StringParser;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class PlayerDeath implements Listener {

    StringParser stringParser = new StringParser();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!KitPVP.dedicatedGame){
            if(!KitPVP.getPlayer(event.getEntity().getUniqueId()).inGame){
                return;
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
        if(!config.getBoolean("PublicDeathMessages")) {
            event.setDeathMessage(null);
            if(event.getEntity().isDead()) {
                if(event.getEntity().getKiller() != null) {
                    if(config.getString("DeathMessage") != null) {
                        if (!event.getEntity().getKiller().getItemInHand().getType().equals(Material.AIR)) {
                            String message = stringParser.parse(config.getString("DeathMessage").replaceAll("%Victim%", event.getEntity().getDisplayName())
                                    .replaceAll("%Killer%", event.getEntity().getKiller().getDisplayName())
                                    .replaceAll("%Tool%", event.getEntity().getKiller().getItemInHand().getType().name().toLowerCase().replaceAll("_", " ")));
                            event.getEntity().sendMessage(message);
                            event.getEntity().getKiller().sendMessage(message + ChatColor.RED + " (+" + KitPVP.killRewardPoints + " Points)");
                        } else {
                            String message = ChatColor.RED + event.getEntity().getKiller().getDisplayName() + ChatColor.WHITE + " has killed " + ChatColor.WHITE + event.getEntity().getDisplayName();
                            event.getEntity().sendMessage(message);
                            event.getEntity().getKiller().sendMessage(message + ChatColor.RED + " (+" + KitPVP.killRewardPoints + " Points)");
                        }
                    }
                    KPVPPlayer player = KitPVP.getPlayer(event.getEntity().getUniqueId());
                    player.kit = null;
                    KitPVP.savePlayerData(player);
                }
            }else{
                event.setDeathMessage(null);
            }
        }else{
            if(event.getEntity().getKiller() != null) {
                if(config.getString("DeathMessage") != null) {
                    if (!event.getEntity().getKiller().getItemInHand().getType().equals(Material.AIR)) {
                        String message = stringParser.parse(config.getString("DeathMessage").replaceAll("%Victim%", event.getEntity().getDisplayName())
                                .replaceAll("%Killer%", event.getEntity().getKiller().getDisplayName())
                                .replaceAll("%Tool%", event.getEntity().getKiller().getItemInHand().getType().name().toLowerCase().replaceAll("_", " ")));
                        event.setDeathMessage(message);
                    } else {
                        String message = ChatColor.RED + event.getEntity().getKiller().getDisplayName() + ChatColor.WHITE + " has killed " + ChatColor.WHITE + event.getEntity().getDisplayName();
                        event.setDeathMessage(message);
                    }
                }
            }else{
                event.setDeathMessage(null);
            }
        }
        if(event.getDrops() != null){
            event.getDrops().clear();
        }

        if(KitPVP.deathSigns) {
            if (event.getEntity() instanceof Player) {
                Location topBlock = event.getEntity().getLocation().clone();
	            if(!topBlock.getBlock().isLiquid() && !topBlock.getBlock().getType().equals(Material.SIGN) && !topBlock.getBlock().getType().equals(Material.SIGN_POST) && !topBlock.getBlock().getType().equals(Material.WALL_SIGN)) {
		            DeathSign deathSign = new DeathSign();
		            deathSign.location = topBlock;
		            deathSign.disapearsIn = KitPVP.deathSignDelay;
		            deathSign.originalMaterial = topBlock.getBlock().getType();
		            topBlock.getBlock().setType(Material.SIGN_POST);
		            if (topBlock.getBlock().getState() instanceof Sign) {
			            Sign sign = (Sign) topBlock.getBlock().getState();
			            sign.setLine(0, KitPVP.deathSignLines.get(0).replaceAll("%PlayerName%", event.getEntity().getName()));
			            sign.setLine(1, KitPVP.deathSignLines.get(1).replaceAll("%PlayerName%", event.getEntity().getName()));
			            sign.setLine(2, KitPVP.deathSignLines.get(2).replaceAll("%PlayerName%", event.getEntity().getName()));
			            sign.setLine(3, KitPVP.deathSignLines.get(3).replaceAll("%PlayerName%", event.getEntity().getName()));
			            sign.update();
			            topBlock.getBlock().getState().setData(sign.getData());
			            KitPVP.deathSignList.add(deathSign);
		            }
	            }
            }
        }
    }
}
