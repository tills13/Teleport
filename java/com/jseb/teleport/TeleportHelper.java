package com.jseb.teleport;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Chunk;

public class TeleportHelper {
	public static void loadChunkAt(Location location) {
		if (!location.getWorld().getChunkAt(location).isLoaded()) location.getWorld().getChunkAt(location).load();
	}

	public static boolean unloadChunkAt(Location location) {
		return false;
	}
}