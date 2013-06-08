package com.jseb.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Language {
	public static File langFile;
	public static Teleport plugin; 

	public static String getString(String title) {
		BufferedReader reader;
		String string = null, value = "";
		try {
			reader = new BufferedReader(new FileReader(langFile));
			do { 
				try {
					string = reader.readLine();
					System.out.println(string);
					if (string == null) break;
					if (string.indexOf(":") > 0) value = string.substring(0, string.indexOf(":"));
					System.out.println(value);
					else continue;
				} catch (StringIndexOutOfBoundsException e) {
					continue;
				}
			} while (!string.equals(title));
		} catch (IOException e) {
			reload();
			return getString(title);
		} 

		return string == null ? null : ChatColor.translateAlternateColorCodes('&', string.substring(string.indexOf(":") + 2, string.length()));
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