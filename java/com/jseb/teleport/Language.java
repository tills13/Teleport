package com.jseb.teleport;

import com.jseb.teleport.Teleport;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class Language {
	public static File langFile;
	public static Teleport plugin;
	public static Map<String, String> stringList;

	public static String getString(String title) {
		if (stringList == null) {
			BufferedReader reader;
			String string = null, value = "";
			try {
				reader = new BufferedReader(new FileReader(langFile));
				do { 
					try {
						string = reader.readLine();
						if (string == null) break;
						if (string.indexOf(":") > 0) value = string.substring(0, string.indexOf(":"));
						else continue;
					} catch (StringIndexOutOfBoundsException e) {
						continue;
					}
				} while (!value.equalsIgnoreCase(title));
			} catch (IOException e) {
				reload();
				return getString(title);
			} 

			return string == null ? null : ChatColor.translateAlternateColorCodes('&', string.substring(string.indexOf(":") + 2, string.length()));
		} else {
			return ChatColor.translateAlternateColorCodes('&', stringList.get(title.toLowerCase()));
		}
	}

	public static String getFormattedString(String title, String ... args) {
		//return String.format(getString(title), args);
		return "";
	}

	public static void load() {
		BufferedReader reader;
		String string;
		if (stringList == null) stringList = new HashMap<String, String>();

		try {
			reader = new BufferedReader(new FileReader(langFile));
			string = reader.readLine().toLowerCase();
			
			while (string != null) {
				if (string.indexOf(":") > 0) stringList.put(string.substring(0, string.indexOf(":")), string.substring(string.indexOf(":") + 2, string.length()));
				string = reader.readLine().toLowerCase();
			} 
		} catch (IOException e) {
			reload();
		}
	}

	public static void reload() {
		langFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + Config.getString("language.language") + ".lang");

		if (Config.getBoolean("language.cache")) load();
	}
}