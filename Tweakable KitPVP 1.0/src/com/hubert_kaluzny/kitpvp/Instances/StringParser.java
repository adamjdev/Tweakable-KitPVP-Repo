package com.hubert_kaluzny.kitpvp.Instances;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StringParser {

    public String parse(String string){
        return string.replaceAll("&f","" + ChatColor.WHITE)
                .replaceAll("&0","" + ChatColor.BLACK)
                .replaceAll("&1","" + ChatColor.DARK_BLUE)
                .replaceAll("&2","" + ChatColor.DARK_GREEN)
                .replaceAll("&3","" + ChatColor.DARK_AQUA)
                .replaceAll("&4","" + ChatColor.DARK_RED)
                .replaceAll("&5","" + ChatColor.DARK_PURPLE)
                .replaceAll("&6","" + ChatColor.GOLD)
                .replaceAll("&7","" + ChatColor.GRAY)
                .replaceAll("&8","" + ChatColor.DARK_GRAY)
                .replaceAll("&9","" + ChatColor.BLUE)
                .replaceAll("&a","" + ChatColor.GREEN)
                .replaceAll("&b","" + ChatColor.AQUA)
                .replaceAll("&c","" + ChatColor.RED)
                .replaceAll("&d","" + ChatColor.LIGHT_PURPLE)
                .replaceAll("&e","" + ChatColor.YELLOW)
                .replaceAll("&l","" + ChatColor.BOLD)
                .replaceAll("&m","" + ChatColor.STRIKETHROUGH)
                .replaceAll("&n","" + ChatColor.UNDERLINE)
                .replaceAll("&o","" + ChatColor.ITALIC)
                .replaceAll("&r","" + ChatColor.RESET);
    }

	public List<String> parse(List<String> strings){
		List<String> parses = new ArrayList<String>();
		for(String string : strings){
			parses.add(parse(string));
		}
		return parses;
	}
}
