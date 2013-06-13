package com.jseb.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

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
				} while (!value.equals(title));
			} catch (IOException e) {
				reload();
				return getString(title);
			} 

			return string == null ? null : ChatColor.translateAlternateColorCodes('&', string.substring(string.indexOf(":") + 2, string.length()));
		} else {
			return stringList.get(title);
		}
	}

	public static void load() {
		BufferedReader reader;
		String string;
		if (stringList == null) stringList = new HashMap<String, String>();

		try {
			reader = new BufferedReader(new FileReader(langFile));
			string = reader.readLine();
			while (string != null) {
				if (string.startsWith("#")) continue;
				stringList.put(string.substring(0, string.indexOf(":")), string.substring(string.indexOf(":") + 2, string.length()));
			} 
		} catch (IOException e) {
			reload();
		}
	}

	public static void setPlugin(Teleport teleport) {
		plugin = teleport;
	}

	public static void reload() {
		langFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + plugin.getSettings().lang + ".lang");

		if (!langFile.exists()) {
			System.out.println("[Teleport] language file not found, aborting");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}