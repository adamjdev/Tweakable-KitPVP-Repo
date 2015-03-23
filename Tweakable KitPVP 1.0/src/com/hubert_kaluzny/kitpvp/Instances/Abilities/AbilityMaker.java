package com.hubert_kaluzny.kitpvp.Instances.Abilities;

import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.UUID;

public class AbilityMaker {

    public Ability loadAbility(String Name){
        YamlConfiguration aConf = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "abilities/" + Name + ".yml"));
        Ability ability = new Ability();
        if(aConf.getString("abilitytype").equalsIgnoreCase("multiplyvelocity")){
            final int velX = aConf.getInt("x");
            final int velY = aConf.getInt("y");
            final int velZ = aConf.getInt("z");
            ability.abilityExecutor = new AbilityExecutor() {
                @Override
                public void onAbilityExecute(UUID uuid) {
                    Bukkit.getPlayer(uuid).setVelocity(Bukkit.getPlayer(uuid).getVelocity().multiply(new Vector(velX, velY, velZ)));
                }
            };
        }else if(aConf.getString("abilitytype").equalsIgnoreCase("setvelocity")) {
            final int velX = aConf.getInt("x");
            final int velY = aConf.getInt("y");
            final int velZ = aConf.getInt("z");
            ability.abilityExecutor = new AbilityExecutor() {
                @Override
                public void onAbilityExecute(UUID uuid) {
                    Bukkit.getPlayer(uuid).setVelocity(new Vector(velX, velY, velZ));
                }
            };
        }else if(aConf.getString("abilitytype").equalsIgnoreCase("effect")){
            final String effect = aConf.getString("effect");
            if(effect.equalsIgnoreCase("explosion")){
                ability.abilityExecutor = new AbilityExecutor() {
                    @Override
                    public void onAbilityExecute(UUID uuid) {
                        Player player = Bukkit.getPlayer(uuid);
                        player.getWorld().createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 3, false, false);
                    }
                };
            }else {
                ability.abilityExecutor = new AbilityExecutor() {
                    @Override
                    public void onAbilityExecute(UUID uuid) {
                        Player player = Bukkit.getPlayer(uuid);
                        player.playEffect(player.getLocation(), Effect.valueOf(effect), 1);
                    }
                };
            }
        }else if(aConf.getString("abilitytype").equalsIgnoreCase("potioneffect")){
            final PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(aConf.getString("Potion").toUpperCase()), aConf.getInt("Duration"), aConf.getInt("Amplifier"));
            ability.abilityExecutor = new AbilityExecutor() {
                @Override
                public void onAbilityExecute(UUID uuid){
                    Bukkit.getPlayer(uuid).addPotionEffect(potionEffect);
                }
            };
        }

        ability.abilityActivator = AbilityActivator.valueOf(aConf.getString("activator").toUpperCase());
        ability.maxCooldown = aConf.getInt("cooldown");
        return ability;
    }
}
