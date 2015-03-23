package com.hubert_kaluzny.kitpvp.Listeners;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.Instances.Abilities.Ability;
import com.hubert_kaluzny.kitpvp.Instances.Abilities.AbilityActivator;
import com.hubert_kaluzny.kitpvp.Instances.Item;
import com.hubert_kaluzny.kitpvp.Instances.KPVPPlayer;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Interact implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
		if(!KitPVP.dedicatedGame){
			if(!KitPVP.getPlayer(event.getPlayer().getUniqueId()).inGame){
				return;
			}
		}
		if (event.getItem() != null) {
			if (event.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
				if (KitPVP.soupEnable && KitPVP.instaSoup) {
					event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					if (event.getPlayer().getHealth() + KitPVP.soupHealth > event.getPlayer().getMaxHealth()) {
						event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
					} else {
						event.getPlayer().setHealth(event.getPlayer().getHealth() + KitPVP.soupHealth);
					}
				}
			}
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR)) {
				for (final KPVPPlayer player : KitPVP.playerList) {
					if (player.uuid.equals(event.getPlayer().getUniqueId())) {
						if (player.kit != null) {
							for (Item item : player.kit.inventoryContents) {
								for (Ability ability : item.abilityList) {
									if (ability.abilityActivator.equals(AbilityActivator.RIGHT_CLICK)) {
										if (ability.cooldown < 1) {
											if (event.getPlayer().getItemInHand().getType().equals(item.itemStack.getType())) {
												ability.cooldown = ability.maxCooldown;
												player.immuneToExplosion = true;
												ability.abilityExecutor.onAbilityExecute(event.getPlayer().getUniqueId());
												Bukkit.getScheduler().scheduleSyncDelayedTask(KitPVP.plugin, new Runnable() {
													@Override
													public void run() {
														player.immuneToExplosion = false;
														KitPVP.savePlayerData(player);
													}
												}, 5L);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(0) != null) {
					Player playr = event.getPlayer();
					if (sign.getLine(0).replaceAll(ChatColor.WHITE + "", "")
							.replaceAll(ChatColor.BLACK + "", "")
							.replaceAll(ChatColor.DARK_BLUE + "", "")
							.replaceAll(ChatColor.DARK_GREEN + "", "")
							.replaceAll(ChatColor.DARK_AQUA + "", "")
							.replaceAll(ChatColor.DARK_RED + "", "")
							.replaceAll(ChatColor.DARK_PURPLE + "", "")
							.replaceAll(ChatColor.GOLD + "", "")
							.replaceAll(ChatColor.GRAY + "", "")
							.replaceAll(ChatColor.DARK_GRAY + "", "")
							.replaceAll(ChatColor.BLUE + "", "")
							.replaceAll(ChatColor.GREEN + "", "")
							.replaceAll(ChatColor.AQUA + "", "")
							.replaceAll(ChatColor.RED + "", "")
							.replaceAll(ChatColor.LIGHT_PURPLE + "", "")
							.replaceAll(ChatColor.YELLOW + "", "")
							.replaceAll(ChatColor.BOLD + "", "")
							.replaceAll(ChatColor.STRIKETHROUGH + "", "")
							.replaceAll(ChatColor.UNDERLINE + "", "")
							.replaceAll(ChatColor.ITALIC + "", "")
							.replaceAll(ChatColor.RESET + "", "")
							.equalsIgnoreCase("[shop]")) {
						Inventory inventory = Bukkit.getServer().createInventory(playr, 9, "Shop");

						KPVPPlayer kpvpPlayer = KitPVP.getPlayer(playr.getUniqueId());
						kpvpPlayer.shopOpen = true;
						KitPVP.savePlayerData(kpvpPlayer);

						YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));

						if (kpvpPlayer.playerRank != PlayerRank.VETERAN) {
							ItemStack upgrade = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getData());
							ItemMeta upgradeMeta = upgrade.getItemMeta();
							upgradeMeta.setDisplayName(ChatColor.GREEN + "Rank Promotion");
							List<String> upgradeLore = new ArrayList<String>();
							if (kpvpPlayer.playerRank.equals(PlayerRank.SOLDIER)) {
								upgradeLore.add(ChatColor.RED + "" + config.getInt("SoldierToVeteranCost") + KitPVP.pointName);
							} else if (kpvpPlayer.playerRank.equals(PlayerRank.TRAINEE)) {
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
						playr.openInventory(inventory);
						return;
					} else if (sign.getLine(0).replaceAll(ChatColor.BLACK + "", "")
							.replaceAll(ChatColor.DARK_BLUE + "", "")
							.replaceAll(ChatColor.DARK_GREEN + "", "")
							.replaceAll(ChatColor.DARK_AQUA + "", "")
							.replaceAll(ChatColor.DARK_RED + "", "")
							.replaceAll(ChatColor.DARK_PURPLE + "", "")
							.replaceAll(ChatColor.GOLD + "", "")
							.replaceAll(ChatColor.GRAY + "", "")
							.replaceAll(ChatColor.DARK_GRAY + "", "")
							.replaceAll(ChatColor.BLUE + "", "")
							.replaceAll(ChatColor.GREEN + "", "")
							.replaceAll(ChatColor.AQUA + "", "")
							.replaceAll(ChatColor.RED + "", "")
							.replaceAll(ChatColor.LIGHT_PURPLE + "", "")
							.replaceAll(ChatColor.YELLOW + "", "")
							.replaceAll(ChatColor.BOLD + "", "")
							.replaceAll(ChatColor.STRIKETHROUGH + "", "")
							.replaceAll(ChatColor.UNDERLINE + "", "")
							.replaceAll(ChatColor.ITALIC + "", "")
							.replaceAll(ChatColor.RESET + "", "")
							.equalsIgnoreCase("[kits]")) {
						int invCount = ((int) Math.ceil(KitPVP.kitList.size() / 9)) * 9;
						if ((int) Math.ceil(KitPVP.kitList.size() / 9) == 0) {
							invCount = 9;
						}
						Inventory inventory = Bukkit.getServer().createInventory(playr, invCount, "Kits");
						KPVPPlayer player = KitPVP.getPlayer(playr.getUniqueId());
						for (Kit kit : KitPVP.kitList) {
							if (kit.permission != null) {
								if (KitPVP.useVault) {
									if (KitPVP.permission.playerHas(playr, kit.permission)) {
										ItemMeta itemMeta = kit.displayItem.getItemMeta();
										itemMeta.setDisplayName(kit.Name);
										ItemStack item = kit.displayItem;
										item.setItemMeta(itemMeta);
										inventory.addItem(item);
									} else {
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
								} else {
									if (playr.hasPermission(kit.permission)) {
										ItemMeta itemMeta = kit.displayItem.getItemMeta();
										itemMeta.setDisplayName(kit.Name);
										ItemStack item = kit.displayItem;
										item.setItemMeta(itemMeta);
										inventory.addItem(item);
									} else {
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
								}
							} else {
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
						}
						playr.openInventory(inventory);
						player.selectingKit = true;
						KitPVP.savePlayerData(player);
						return;
					}
				}
			}
			if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR)) {
					for (final KPVPPlayer player : KitPVP.playerList) {
						if (player.uuid.equals(event.getPlayer().getUniqueId())) {
							if (player.kit != null) {
								for (Item item : player.kit.inventoryContents) {
									for (Ability ability : item.abilityList) {
										if (ability.abilityActivator.equals(AbilityActivator.LEFT_CLICK)) {
											if (ability.cooldown < 1) {
												if (event.getPlayer().getItemInHand().getType().equals(item.itemStack.getType())) {
													ability.cooldown = ability.maxCooldown;
													player.immuneToExplosion = true;
													ability.abilityExecutor.onAbilityExecute(event.getPlayer().getUniqueId());
													Bukkit.getScheduler().scheduleSyncDelayedTask(KitPVP.plugin, new Runnable() {
														@Override
														public void run() {
															player.immuneToExplosion = false;
															KitPVP.savePlayerData(player);
														}
													}, 5L);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
