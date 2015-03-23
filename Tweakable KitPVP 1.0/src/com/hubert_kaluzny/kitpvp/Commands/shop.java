package com.hubert_kaluzny.kitpvp.Commands;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class shop implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player)sender;

        if(!KitPVP.dedicatedGame){
            if(!KitPVP.getPlayer(player.getUniqueId()).inGame){
                player.sendMessage(ChatColor.BLUE + "Not in game!");
                return true;
            }
        }

        Inventory inventory = Bukkit.getServer().createInventory(player, 9, "Shop");

        KPVPPlayer kpvpPlayer = KitPVP.getPlayer(player.getUniqueId());
        kpvpPlayer.shopOpen = true;
        KitPVP.savePlayerData(kpvpPlayer);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));

        if(kpvpPlayer.playerRank != PlayerRank.VETERAN) {
            ItemStack upgrade = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getData());
            ItemMeta upgradeMeta = upgrade.getItemMeta();
            upgradeMeta.setDisplayName(ChatColor.GREEN + "Rank Promotion");
            List<String> upgradeLore = new ArrayList<String>();
            if(kpvpPlayer.playerRank.equals(PlayerRank.SOLDIER)){
                upgradeLore.add(ChatColor.RED + "" + config.getInt("SoldierToVeteranCost") + KitPVP.pointName);
            }else if(kpvpPlayer.playerRank.equals(PlayerRank.TRAINEE)) {
                upgradeLore.add(ChatColor.RED + "" + config.getInt("TraineeToSoldierCost") + KitPVP.pointName);
            }
            upgradeMeta.setLore(upgradeLore);
            upgrade.setItemMeta(upgradeMeta);
            inventory.addItem(upgrade);
        }

        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        ItemMeta soupMeta = soup.getItemMeta();
        soupMeta.setDisplayName(ChatColor.GREEN + "Low Health Regen");
        List<String> soupLore = new ArrayList<String>();
        soupLore.add(ChatColor.RED + "" + config.getInt("LowHealthRegenCost") + KitPVP.pointName);
        soupMeta.setLore(soupLore);
        soup.setItemMeta(soupMeta);
        inventory.setItem(2, soup);

        ItemStack apple = new ItemStack(Material.APPLE);
        ItemMeta appleMeta = apple.getItemMeta();
        appleMeta.setDisplayName(ChatColor.GREEN + "Medium Health Regen");
        List<String> appleLore = new ArrayList<String>();
        appleLore.add(ChatColor.RED + "" + config.getInt("MediumHealthRegenCost") + KitPVP.pointName);
        appleMeta.setLore(appleLore);
        apple.setItemMeta(appleMeta);
        inventory.setItem(3, apple);

        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta goldMeta = goldenApple.getItemMeta();
        goldMeta.setDisplayName(ChatColor.GREEN + "High Health Regen");
        List<String> goldenAppleLore = new ArrayList<String>();
        goldenAppleLore.add(ChatColor.RED + "" + config.getInt("HighHealthRegenCost") + KitPVP.pointName);
        goldMeta.setLore(goldenAppleLore);
        goldenApple.setItemMeta(goldMeta);
        inventory.setItem(4, goldenApple);

        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = ironSword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.BLUE + "Add Sharpness Level");
        List<String> swordLore = new ArrayList<String>();
        swordLore.add(ChatColor.RED + "" + config.getInt("SharpnessEnchantCost") + KitPVP.pointName);
        swordMeta.setLore(swordLore);
        ironSword.setItemMeta(swordMeta);
        inventory.setItem(6, ironSword);

        ItemStack ironChestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta chestplateMeta = ironChestplate.getItemMeta();
        chestplateMeta.setDisplayName(ChatColor.BLUE + "Add Protection Level");
        List<String> chestLore = new ArrayList<String>();
        chestLore.add(ChatColor.RED + "" + config.getInt("ProtectionEnchantCost") + KitPVP.pointName);
        chestplateMeta.setLore(chestLore);
        ironChestplate.setItemMeta(chestplateMeta);
        inventory.setItem(7, ironChestplate);

        ItemStack flintandsteel = new ItemStack(Material.FLINT_AND_STEEL);
        ItemMeta flintMeta = flintandsteel.getItemMeta();
        flintMeta.setDisplayName(ChatColor.BLUE + "Add Fire Aspect Enchant");
        List<String> flintLore = new ArrayList<String>();
        flintLore.add(ChatColor.RED + "" + config.getInt("FireAspectEnchantCost") + KitPVP.pointName);
        flintMeta.setLore(flintLore);
        flintandsteel.setItemMeta(flintMeta);
        inventory.setItem(8, flintandsteel);

        player.openInventory(inventory);
        return true;
    }
}
