package com.hubert_kaluzny.kitpvp.Instances.Kit;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.Item;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    public Kit(String KitName){
        Name = KitName;
    }

    public String Name, permission;
    public PlayerRank minRank;
    public ItemStack helmet, chestplate, leggings, boots, displayItem;
    public List<Item> inventoryContents = new ArrayList<Item>();
    public List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
    public List<EntityDamageEvent.DamageCause> immuneTo = new ArrayList<EntityDamageEvent.DamageCause>();
}
