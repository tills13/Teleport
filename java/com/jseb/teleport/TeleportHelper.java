package com.jseb.teleport;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class TeleportHelper {
	public static Teleport plugin;

	public static void loadChunkAt(Location location) {
		if (!location.getWorld().getChunkAt(location).isLoaded()) location.getWorld().getChunkAt(location).load();
	}

	public static boolean unloadChunkAt(Location location) {
		return false;
	}

	public static YamlConfiguration getConfig(String name) {
		InputStream defConfigStream = plugin.getResource(name);
		if (defConfigStream != null) return YamlConfiguration.loadConfiguration(defConfigStream);
		else return null;
	}

	public static void saveConfig(String name, YamlConfiguration config) {
		File saveFile = new File(plugin.getDataFolder(), name);

		try {
			if (!saveFile.exists()) {
				// something went wrong
			} else config.save(saveFile);
		} catch (IOException e) {

		}
	}
}