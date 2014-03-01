package com.jseb.teleport;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.Material;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class TeleportHelper {
	private final static Logger LOGGER = Logger.getLogger("TeleportHelper");
	public static Teleport plugin;

	public static void loadChunkAt(Location location) {
		if (!location.getWorld().getChunkAt(location).isLoaded()) location.getWorld().getChunkAt(location).load();
	}

	public static boolean unloadChunkAt(Location location) {
		return false;
	}

	public static YamlConfiguration getConfig(String name) {
		try {
			InputStream defConfigStream = plugin.getResource(name);

			if (defConfigStream != null) return YamlConfiguration.loadConfiguration(defConfigStream);
			else {		
				File saveFile = new File(plugin.getDataFolder(), name);
				saveFile.createNewFile();

				return YamlConfiguration.loadConfiguration(saveFile);	
			}
		} catch (IOException | NullPointerException e) {
			LOGGER.warning("[TH] error: " + e.getMessage());
			return null;
		}
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

	public static Location getSafeTeleportLocation(Location location) {
		int count = 0; 
		Block candidate = location.getWorld().getBlockAt(location);

		if (!candidate.getType().equals(Material.AIR)) {
			while(!candidate.getRelative(BlockFace.DOWN).getType().equals(Material.AIR) && count++ > 100) candidate = candidate.getRelative(BlockFace.UP);
		} else if (candidate.getType().equals(Material.AIR)) {
			while (candidate.getRelative(BlockFace.DOWN).getType().equals(Material.AIR) && count++ > 100) candidate = candidate.getRelative(BlockFace.DOWN);
		}

		return candidate.getLocation();
	}

	public static String listToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (String string : list) sb.append(((index == 0) ? "[" : "") + string + (++index == list.size() ? "]" : ", "));

		return sb.toString();
	}
}