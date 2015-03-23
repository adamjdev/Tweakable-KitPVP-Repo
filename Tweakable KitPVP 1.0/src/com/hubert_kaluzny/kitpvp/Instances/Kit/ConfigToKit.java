package com.hubert_kaluzny.kitpvp.Instances.Kit;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.Abilities.AbilityMaker;
import com.hubert_kaluzny.kitpvp.Instances.Item;
import com.hubert_kaluzny.kitpvp.Instances.StringParser;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigToKit {

    AbilityMaker abilityMaker = new AbilityMaker();

    public Kit loadKit(String fileName){
        StringParser stringParser = new StringParser();

        YamlConfiguration armourConfig = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "kits/" + fileName));
        Kit kit = new Kit(stringParser.parse(armourConfig.getString("KitName")));
        kit.helmet = new ItemStack(Material.valueOf(armourConfig.getString("helmet").toUpperCase()));
        kit.boots = new ItemStack(Material.valueOf(armourConfig.getString("boots").toUpperCase()));
        kit.leggings = new ItemStack(Material.valueOf(armourConfig.getString("leggings").toUpperCase()));
        kit.chestplate = new ItemStack(Material.valueOf(armourConfig.getString("chestplate").toUpperCase()));
        kit.displayItem = new ItemStack(Material.valueOf(armourConfig.getString("displayitem").toUpperCase()));

        int invID = 1;
        while(armourConfig.get("inventorycontents." + invID) != null){
            Item actItem = new Item();
            ItemStack item = new ItemStack(Material.valueOf(armourConfig.getString("inventorycontents." + invID + ".Item").toUpperCase()));
            ItemMeta itemMeta = item.getItemMeta();
            int enchantmentID = 1;
            while(armourConfig.getString("inventorycontents." + invID + ".Enchantment." + enchantmentID) != null){
                itemMeta.addEnchant(Enchantment.getByName(armourConfig.getString("inventorycontents." + invID + ".Enchantment." + enchantmentID)), armourConfig.getInt("inventorycontents." + invID + ".EnchantmentLevel." + enchantmentID), true);
                enchantmentID++;
        }
            if(armourConfig.getString("inventorycontents." + invID + ".displayname") != null) {
                itemMeta.setDisplayName(stringParser.parse(armourConfig.getString("inventorycontents." + invID + ".displayname")));
            }
            if(armourConfig.getStringList("inventorycontents." + invID + ".lore") != null) {
                List<String> stringList = armourConfig.getStringList("inventorycontents." + invID + ".lore");
                List<String> loreList = new ArrayList<String>();
                for(String string : stringList){
                    loreList.add(stringParser.parse(string));
                }
                itemMeta.setLore(loreList);
            }
            item.setItemMeta(itemMeta);
            item.setAmount(1);
            actItem.itemStack = item;

            int abilityID = 1;
            while(armourConfig.getString("inventorycontents." + invID + ".ability." + abilityID) != null){
                actItem.abilityList.add(abilityMaker.loadAbility(armourConfig.getString("inventorycontents." + invID + ".ability." + abilityID)));
                abilityID++;
            }

            int effectID = 1;
            while(armourConfig.getString("effect." + effectID + ".effect") != null){
                PotionEffectType potionEffectType = PotionEffectType.ABSORPTION;
                for(PotionEffectType potionEffectType1 : PotionEffectType.values()){
                    if(potionEffectType1.getName().equalsIgnoreCase(armourConfig.getString("effect." + effectID + ".effect"))){
                        potionEffectType = potionEffectType1;
                    }
                }
                kit.potionEffects.add(new PotionEffect(potionEffectType, Integer.MAX_VALUE, armourConfig.getInt("effect." + effectID + ".amplifier")));
            }

            kit.inventoryContents.add(actItem);
            invID++;
        }

        if(armourConfig.getStringList("ImmuneToDamage") != null && armourConfig.getStringList("ImmuneToDamage").size() > 0) {
            for (String immuneToDamage : armourConfig.getStringList("ImmuneToDamage")) {
                if(EntityDamageEvent.DamageCause.valueOf(immuneToDamage.toUpperCase()) != null) {
                    kit.immuneTo.add(EntityDamageEvent.DamageCause.valueOf(immuneToDamage.toUpperCase()));
                }
            }
        }
        if(armourConfig.getString("Permission") != null){
            kit.permission = armourConfig.getString("Permission");
        }

        kit.minRank = PlayerRank.valueOf(armourConfig.getString("MinimumRank").toUpperCase());
        Bukkit.getLogger().log(Level.INFO, "Loading kit " + kit.Name);
        return kit;
    }
}
