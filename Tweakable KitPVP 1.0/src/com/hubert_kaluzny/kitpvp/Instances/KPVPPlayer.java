package com.hubert_kaluzny.kitpvp.Instances;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KPVPPlayer {

    public KPVPPlayer(UUID uuid, int id) {
        this.uuid = uuid;
	    this.playerID = id;
        if(KitPVP.useSQL) {
            if (KitPVP.sqlManager.playerEntryExists(uuid)) {
                KPVPPlayer player = KitPVP.sqlManager.getPlayerStats(this);
                kills = player.kills;
                deaths = player.deaths;
                points = player.points;
            }else{
                points = KitPVP.basePoints;
            }
        }else{
            File playerFile = new File(KitPVP.plugin.getDataFolder() + "/players/", uuid.toString() + ".yml");
            if(!playerFile.exists()){
                try{
                    playerFile.createNewFile();
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                    config.set("kills", kills);
                    config.set("deaths", deaths);
                    config.set("points", KitPVP.basePoints);
                    config.set("rank", playerRank.toString());
                    config.save(playerFile);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            this.kills = config.getInt("kills");
            this.deaths = config.getInt("deaths");
            this.points = config.getInt("points");
            this.playerRank = PlayerRank.valueOf(config.getString("rank").toUpperCase());
        }
        KitPVP.updateScoreboard(this);
    }

    public UUID uuid;
    public int kills, deaths, points = 0, playerID, killstreak = 0;
    public PlayerRank playerRank = PlayerRank.TRAINEE;
    public boolean selectingKit, immuneToExplosion, shopOpen = false;
    public Kit kit;
    public Scoreboard scoreboard;
    public boolean dev = false;
    public boolean inGame = false;

    public ItemStack[] storedInventory;
    public ItemStack storedHelmet;
    public ItemStack storedChestplate;
    public ItemStack storedLeggings;
    public ItemStack storedBoots;

    public void wearKit(Kit kit){
	    Bukkit.getPlayer(uuid).getInventory().clear();
        this.kit = kit;
        Bukkit.getPlayer(uuid).getInventory().setHelmet(kit.helmet);
        Bukkit.getPlayer(uuid).getInventory().setChestplate(kit.chestplate);
        Bukkit.getPlayer(uuid).getInventory().setLeggings(kit.leggings);
        Bukkit.getPlayer(uuid).getInventory().setBoots(kit.boots);
        Bukkit.getPlayer(uuid).teleport(new Location(Bukkit.getWorld(KitPVP.worldName), KitPVP.x, KitPVP.y, KitPVP.z));

        for(PotionEffect potionEffect : kit.potionEffects){
            Bukkit.getPlayer(uuid).addPotionEffect(potionEffect);
        }

        for(Item item : kit.inventoryContents){
            Bukkit.getPlayer(uuid).getInventory().addItem(item.itemStack);
        }
	    if(KitPVP.soupEnable) {
		    for (int i = 0; i < Bukkit.getPlayer(uuid).getInventory().getSize(); i++) {
			    Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		    }
		    Bukkit.getPlayer(uuid).updateInventory();
	    }
    }
}