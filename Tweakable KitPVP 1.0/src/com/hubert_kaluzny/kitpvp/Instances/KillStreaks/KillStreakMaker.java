package com.hubert_kaluzny.kitpvp.Instances.KillStreaks;

import com.hubert_kaluzny.kitpvp.Enums.RewardType;
import com.hubert_kaluzny.kitpvp.Instances.StringParser;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KillStreakMaker {

	public List<KillStreak> loadKillStreaks(){
		List<KillStreak> killStreaks = new ArrayList<KillStreak>();

		if(!new File(KitPVP.plugin.getDataFolder(), "killstreaks.yml").exists()){
			try{
				new File(KitPVP.plugin.getDataFolder(), "killstreaks.yml").createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			YamlConfiguration ks = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "killstreaks.yml"));
			int i = 0;
			while(ks.get(i + ".kills") != null){
				KillStreak killStreak = new KillStreak();
				killStreak.kills = ks.getInt(i + ".kills");
				killStreak.Message = new StringParser().parse(ks.getString(i + ".message"));
				if(ks.getString(i + ".rewardType").equalsIgnoreCase("currency")){
					killStreak.rewardType = RewardType.CURRENCY;
					killStreak.reward_currency = ks.getInt(i + ".rewardCurrency");
				}else if(ks.getString(i + ".rewardType").equalsIgnoreCase("item")){
					killStreak.rewardType = RewardType.ITEM;
					killStreak.reward_item = Material.valueOf(ks.getString(i + ".material").toUpperCase());
					killStreak.amount = ks.getInt(i + ".amount");
					killStreak.displayName = new StringParser().parse(ks.getString(i + ".displayname"));
					killStreak.lores = new StringParser().parse(ks.getStringList(i + ".lore"));
				}else{
					killStreak.rewardType = RewardType.NULL;
				}
				killStreaks.add(killStreak);
				i++;
			}
		}
		return killStreaks;
	}
}
