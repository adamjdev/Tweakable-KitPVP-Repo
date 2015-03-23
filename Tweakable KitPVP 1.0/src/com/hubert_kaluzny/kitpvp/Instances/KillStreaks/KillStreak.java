package com.hubert_kaluzny.kitpvp.Instances.KillStreaks;

import com.hubert_kaluzny.kitpvp.Enums.RewardType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KillStreak {
	public int kills, reward_currency, amount;
	public RewardType rewardType;
	public String Message, displayName;
	public Material reward_item;
	public List<String> lores = new ArrayList<String>();
}
