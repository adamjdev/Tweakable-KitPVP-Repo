package com.hubert_kaluzny.kitpvp;

import com.hubert_kaluzny.kitpvp.Commands.*;
import com.hubert_kaluzny.kitpvp.Instances.*;
import com.hubert_kaluzny.kitpvp.Instances.Abilities.Ability;
import com.hubert_kaluzny.kitpvp.Instances.KillStreaks.KillStreak;
import com.hubert_kaluzny.kitpvp.Instances.KillStreaks.KillStreakMaker;
import com.hubert_kaluzny.kitpvp.Instances.Kit.ConfigToKit;
import com.hubert_kaluzny.kitpvp.Instances.Kit.Kit;
import com.hubert_kaluzny.kitpvp.Listeners.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class KitPVP extends JavaPlugin {
    public static List<KPVPPlayer> playerList = new CopyOnWriteArrayList<KPVPPlayer>();
    public static List<KPVPPlayer> READplayerList = new CopyOnWriteArrayList<KPVPPlayer>();
    public static Plugin plugin;
    public static SQLManager sqlManager = new SQLManager();
    public static ConfigToKit configToKit = new ConfigToKit();
    public static List<Kit> kitList = new ArrayList<Kit>();
    public static int x, y, z, killRewardPoints, basePoints, deathSignDelay, lobbyX, lobbyY, lobbyZ;
    public static String worldName, pointName, lobbyWorld;
    public static ScoreboardManager manager;
    public static Economy economy = null;
	public static Permission permission = null;
    public static boolean useSQL, useVault = false, deathSigns = false, soupEnable = true, showGUIOnLogin = true, instaSoup = true, bloodEffectenabled = true;
    static StringParser stringParser = new StringParser();
    static String ScoreboardName;
    public static List<DeathSign> deathSignList = new ArrayList<DeathSign>(), deathSignsToRemove = new ArrayList<DeathSign>();
	public static List<String> deathSignLines = new ArrayList<String>(), scoreboardLines = new ArrayList<String>();
	public static List<Location> shopSignList = new ArrayList<Location>(), kitSignList = new ArrayList<Location>();
	public static double soupHealth = 3;
	public static List<KillStreak> killStreaks = new ArrayList<KillStreak>();

	public static boolean dedicatedGame = false;

    @Override
    public void onEnable(){
        plugin = this;
        File playerDir = new File(plugin.getDataFolder(), "players");
        if(!playerDir.exists()){
            playerDir.mkdir();
        }
        File file = new File(plugin.getDataFolder(), "config.yml");
        if(!file.exists()){
            plugin.saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		if(config.get("dedicated") != null){
			dedicatedGame = config.getBoolean("dedicated");
			System.out.println("Dedicated : " + dedicatedGame);
		}

		if(config.get("BloodEnabled") != null){
			bloodEffectenabled = config.getBoolean("BloodEnabled");
		}

        x = config.getInt("arena.x");
        y = config.getInt("arena.y");
        z = config.getInt("arena.z");
	    if(config.get("ShowGUIOnLogin") != null) {
		    showGUIOnLogin = config.getBoolean("ShowGUIOnLogin");
	    }
	    soupEnable = config.getBoolean("EnableSoup");
	    soupHealth = config.getDouble("SoupHealth");
	    instaSoup = config.getBoolean("InstaSoup");
        worldName = config.getString("arena.worldName");
		if(worldName != null) {
			if (Bukkit.getWorld(worldName) == null) {
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Arena world not setup!");
			}
		}else{
			Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Arena world not setup!");
		}
	    if((lobbyWorld = config.getString("lobby.worldName")) != null){
		    lobbyX = config.getInt("lobby.x");
		    lobbyY = config.getInt("lobby.y");
		    lobbyZ = config.getInt("lobby.z");
	    }
		if(lobbyWorld != null) {
			if (Bukkit.getWorld(lobbyWorld) == null) {
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Lobby world not setup!");
			}
		}
        useSQL = config.getBoolean("UseMySQL");
        killRewardPoints = config.getInt("KillReward");
        basePoints = config.getInt("BasePoints");
        deathSigns = config.getBoolean("DeathSigns");
        deathSignDelay = config.getInt("DeathSignDelay");
        manager = Bukkit.getScoreboardManager();
        if(useSQL) {
            sqlManager.connect();
        }
	    ScoreboardName = stringParser.parse(config.getString("ScoreboardName"));
        pointName = stringParser.parse(config.getString("CurrencyName"));
	    if(config.getStringList("ScoreboardLines") != null) {
		    for (String string : config.getStringList("ScoreboardLines")) {
				scoreboardLines.add(string);
		    }
	    }

        registerListeners();
        registerCommands();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().length > 0)
                    updateAllPlayers();
            }
        }, 0L, 6000L);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().length > 0){
                    for(KPVPPlayer player : playerList){
                        if (player.kit != null) {
                            for (Item item : player.kit.inventoryContents) {
                                for (Ability ability : item.abilityList){
                                    if(ability.cooldown > 0){
                                        ability.cooldown--;
                                    }
                                }
                            }
                        }
                    }
                }
	            if(deathSigns) {
		            deathSignList.removeAll(deathSignsToRemove);
		            deathSignsToRemove.clear();
		               for (DeathSign deathSign : deathSignList) {
			            if (deathSign.disapearsIn == 0) {
				            deathSign.location.getBlock().setType(deathSign.originalMaterial);
				            deathSignsToRemove.add(deathSign);
			            } else {
				            deathSign.disapearsIn--;
			            }
		            }
	            }
            }
        }, 0L, 20L);

        if(config.getBoolean("UseVault")){
            useVault = true;
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
            if(economy == null){
                useVault = false;
                System.out.println("[Tweakable KitPVP] Could not implement Vault Economy!");
            }else{
                System.out.println("[Tweakable KitPVP] Implemented Vault Economy!");
            }
	        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
		        permission = permissionProvider.getProvider();
                System.out.println("[Tweakable KitPVP] Implemented Vault Permissions!");
            }else{
		        useVault = false;
		        System.out.println("[Tweakable KitPVP] Could not implement Vault Permissions!");
	        }
        }
        loadKits();
	    if(deathSigns){
		    deathSignLines.add(stringParser.parse(config.getString("DeathSignLines.1")));
		    deathSignLines.add(stringParser.parse(config.getString("DeathSignLines.2")));
		    deathSignLines.add(stringParser.parse(config.getString("DeathSignLines.3")));
		    deathSignLines.add(stringParser.parse(config.getString("DeathSignLines.4")));
	    }
	    killStreaks = new KillStreakMaker().loadKillStreaks();
    }

    public void registerCommands(){
        getCommand("setarena").setExecutor(new setarena());
        getCommand("kit").setExecutor(new kit());
        getCommand("shop").setExecutor(new shop());
        getCommand("dev").setExecutor(new dev());
        getCommand("points").setExecutor(new points());
		getCommand("kitpvp").setExecutor(new kitpvp());
    }

    public void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new JQEvents(), this);
        Bukkit.getPluginManager().registerEvents(new PVPEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
        Bukkit.getPluginManager().registerEvents(new InventorySelect(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new Interact(), this);
        Bukkit.getPluginManager().registerEvents(new Physics(), this);
    }

    public static void updatePlayer(KPVPPlayer player){
        if (useSQL) {
            sqlManager.UpdatePlayerData(player);
        } else {
            File playerFile = new File(plugin.getDataFolder() + "/players/", player.uuid.toString() + ".yml");
            if(!playerFile.exists()){
                try{
                    playerFile.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set("kills", player.kills);
            config.set("deaths", player.deaths);
            config.set("points", player.points);
            config.set("rank", player.playerRank.toString());
            try {
                config.save(playerFile);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void updateAllPlayers() {
        for (KPVPPlayer player : playerList) {
           updatePlayer(player);
        }
    }

    public static KPVPPlayer getPlayer(UUID uuid){
        for(KPVPPlayer player : READplayerList){
            if(player.uuid.equals(uuid)){
                if(useVault){
                    if(!economy.hasAccount(Bukkit.getPlayer(player.uuid))){
                        economy.createPlayerAccount(Bukkit.getPlayer(player.uuid));
                        economy.depositPlayer(Bukkit.getPlayer(player.uuid), basePoints);
                    }
                }
                return player;
            }
        }
        return null;
    }

	public static int generatePlayerID(){
		Random random = new Random();
		int id = 0;
		boolean validID = false;
		while(!validID) {
			id = random.nextInt(Bukkit.getMaxPlayers() + 100);
			boolean idExists = false;
			for(KPVPPlayer player : playerList){
				if(player.playerID == id){
					idExists = true;
				}
			}
			validID = !idExists;
		}
		return id;
	}

    public static void savePlayerData(KPVPPlayer player){
        for(KPVPPlayer nPlayer : playerList){
            if(nPlayer.uuid.equals(player.uuid)){
                playerList.remove(nPlayer);
                playerList.add(player);
            }
        }
        KitPVP.READplayerList = KitPVP.playerList;
        updatePlayer(player);
    }

    public void loadKits(){
        File dir = new File(plugin.getDataFolder(), "kits");
        if(!dir.exists()){
            try{
                dir.createNewFile();
                System.out.println("No kits present!");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                kitList.add(configToKit.loadKit(file.getName()));
            }
        }
    }

    public static void updateScoreboard(KPVPPlayer player) {
        if(!dedicatedGame){
            if(!player.inGame){
                return;
            }
        }
        if (player.scoreboard == null) {
            player.scoreboard = manager.getNewScoreboard();
        }

        Objective objective = player.scoreboard.getObjective(String.valueOf(player.playerID));
        if (objective != null) {
            objective.unregister();
            objective = player.scoreboard.registerNewObjective(String.valueOf(player.playerID), "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ScoreboardName);
        } else {
            objective = player.scoreboard.registerNewObjective(String.valueOf(player.playerID), "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ScoreboardName);
        }

        if(useVault) {
            if (player.uuid != null) {
                if (!economy.hasAccount(Bukkit.getOfflinePlayer(player.uuid))) {
                    economy.createPlayerAccount(Bukkit.getOfflinePlayer(player.uuid));
                }
                economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.uuid), economy.getBalance(Bukkit.getOfflinePlayer(player.uuid)));
                economy.depositPlayer(Bukkit.getOfflinePlayer(player.uuid), player.points);
            }
        }

        Score line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12;
        if (scoreboardLines.size() > 0) {
            line1 = objective.getScore(stringParser.parse(scoreboardLines.get(0))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));


            line1.setScore(-1);
        }
        if (scoreboardLines.size() > 1) {
            line2 = objective.getScore(stringParser.parse(scoreboardLines.get(1))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line2.setScore(-2);
        }
        if (scoreboardLines.size() > 2) {
            line3 = objective.getScore(stringParser.parse(scoreboardLines.get(2))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));

            line3.setScore(-3);
        }
        if (scoreboardLines.size() > 3) {
            line4 = objective.getScore(stringParser.parse(scoreboardLines.get(3))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line4.setScore(-4);
        }
        if (scoreboardLines.size() > 4) {
            line5 = objective.getScore(stringParser.parse(scoreboardLines.get(4))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line5.setScore(-5);
        }
        if (scoreboardLines.size() > 5) {
            line6 = objective.getScore(stringParser.parse(scoreboardLines.get(5))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line6.setScore(-6);
        }
        if (scoreboardLines.size() > 6) {
            line7 = objective.getScore(stringParser.parse(scoreboardLines.get(6))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line7.setScore(-7);
        }
        if (scoreboardLines.size() > 7) {
            line8 = objective.getScore(stringParser.parse(scoreboardLines.get(7))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line8.setScore(-8);
        }
        if (scoreboardLines.size() > 8) {
            line9 = objective.getScore(stringParser.parse(scoreboardLines.get(8))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line9.setScore(-9);
        }
        if (scoreboardLines.size() > 9) {
            line10 = objective.getScore(stringParser.parse(scoreboardLines.get(9))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line10.setScore(-10);
        }
        if (scoreboardLines.size() > 10) {
            line11 = objective.getScore(stringParser.parse(scoreboardLines.get(10))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line11.setScore(-11);
        }
        if (scoreboardLines.size() == 12) {
            line12 = objective.getScore(stringParser.parse(scoreboardLines.get(11))
                    .replaceAll("%PlayerName%", Bukkit.getPlayer(player.uuid).getName())
                    .replaceAll("%PlayerPoints%", "" + player.points)
                    .replaceAll("%PlayerDeaths%", "" + player.deaths)
                    .replaceAll("%PlayerKills%", "" + player.kills)
                    .replaceAll("%PlayerRank%", player.playerRank.toString().toLowerCase())
                    .replaceAll("%PlayerKillStreak%", "" + player.killstreak));
            line12.setScore(-12);
        }

        Bukkit.getPlayer(player.uuid).setScoreboard(player.scoreboard);
    }
}
