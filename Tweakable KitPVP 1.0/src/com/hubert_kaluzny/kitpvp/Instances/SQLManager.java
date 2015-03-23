package com.hubert_kaluzny.kitpvp.Instances;

import com.hubert_kaluzny.kitpvp.Enums.PlayerRank;
import com.hubert_kaluzny.kitpvp.KitPVP;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLManager {
    private String tableName;
    private Connection connection;

    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
            connection = DriverManager.getConnection(getConnectionURL(), config.getString("user"), config.getString("password"));
            checkAndMaketable(tableName);
        }catch(Exception e){
            e.printStackTrace();
            close();
        }

    }

    public void close(){
        try {
            connection.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getConnectionURL(){
        String url =  "jdbc:mysql://";
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(KitPVP.plugin.getDataFolder(), "config.yml"));
        url += config.getString("hostname") + ":3306/" + config.getString("database");
        tableName = config.getString("table");
        return url;
    }

    public void checkAndMaketable(String table){
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet res = meta.getTables(null, null, tableName, null);
            if(!res.next()){
                if(connection.isClosed()){
                    connect();
                }
                PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE " + table + " (uuid VARCHAR(50), kills INT(255), deaths INT(255), points INT(255), rank VARCHAR(50));");
                preparedStatement.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
            close();
        }
    }

    public boolean playerEntryExists(UUID uuid){
        try {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement preparedStatement = connection.prepareStatement("Select kills FROM " + tableName + " WHERE uuid='" + uuid.toString() + "';");
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch(SQLException e){
            e.printStackTrace();
            close();
        }
        return false;
    }

    public void UpdatePlayerData(KPVPPlayer player){
        try {
            if (connection.isClosed()) {
                connect();
            }
            if(!playerEntryExists(player.uuid)){
                makeEntry(player);
            }else {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + tableName + " SET kills=" + player.kills + ", deaths=" + player.deaths + ", points=" + player.points + ", rank='" + player.playerRank.toString() + "' WHERE uuid='" + player.uuid.toString() + "';");
                preparedStatement.execute();
            }
        }catch(SQLException e){
            e.printStackTrace();
            close();
        }
    }

    public void makeEntry(KPVPPlayer player){
        try {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (uuid, kills, deaths, points, rank) VALUES ('" + player.uuid.toString() + "'," + player.kills + "," + player.deaths + "," + player.points + ",'" + player.playerRank.toString() + "');");
            statement.execute();
        }catch(SQLException e){
            e.printStackTrace();
            close();
        }
    }

    public KPVPPlayer getPlayerStats(KPVPPlayer kpvpPlayer){
        try {
            if (connection.isClosed()) {
                connect();
            }
            if(playerEntryExists(kpvpPlayer.uuid)) {
                PreparedStatement killsGet = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid='" + kpvpPlayer.uuid.toString() + "';");
                ResultSet killsSet = killsGet.executeQuery();
                killsSet.first();
                kpvpPlayer.kills = killsSet.getInt("kills");
                kpvpPlayer.deaths = killsSet.getInt("deaths");
                kpvpPlayer.points = killsSet.getInt("points");
                kpvpPlayer.playerRank = PlayerRank.valueOf(killsSet.getString("rank"));
                killsSet.close();
            }else{
                makeEntry(kpvpPlayer);
            }
        }catch(SQLException e){
            e.printStackTrace();
            close();
        }
        return kpvpPlayer;
    }

}
