package com.jseb.teleport.storage;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class Storage {
	private static Map<Player, Location> back = new HashMap<Player, Location>(); ;
	private static Map<Player, Location> deaths = new HashMap<Player, Location>();;

	private File homeFile;
	private File areaFile;

	public Storage(JavaPlugin plugin) {
		this.homeFile = new File(plugin.getDataFolder(), "home-locations.bin");
		this.areaFile = new File(plugin.getDataFolder(), "area-locations.bin");

		if (homeFile.exists()) {
			LEGACYHOMES();
			homeFile.delete();
		}

		if (areaFile.exists()) {
			LEGACYAREAS();
			areaFile.delete();
		}
	}

	public void LEGACYHOMES() {
		Double x = 0.0, y = 0.0, z = 0.0;
		float pitch = 0, yaw = 0;
		String name = "", s, owner = "";
		World world = null;
		boolean isDefault = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(homeFile));

			s = br.readLine();
			if (s == null) return;

			while (s != null) {
				do {
					if (s.startsWith("home: ")) name = s.substring(s.indexOf(":") + 2, s.length());
					else if (s.startsWith("owner: ")) owner = s.substring(s.indexOf(":") + 2, s.length()).toLowerCase().trim();
					else if (s.startsWith("world: ")) world = Bukkit.getServer().getWorld(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("x: ")) x = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("y: ")) y = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("z: ")) z = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("yaw: ")) yaw = Float.parseFloat(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("pitch: ")) pitch = Float.parseFloat(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("isDefault: ")) isDefault = Boolean.parseBoolean(s.substring(s.indexOf(":") + 2, s.length()));
					
					s = br.readLine();
					if (s == null) break;
				} while (!(s.startsWith("home: ")));

				if ((world == null) || (owner == "") || (name == "")) System.out.println("[Teleport] something went wrong porting homes");
				else Home.newHome(owner, name, new Location(world, x, y, z, yaw, pitch), isDefault);
			}

			br.close();
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("[TH] something went wrong loading homes");
		} catch(IOException e) {
			System.out.println("[TH] something went wrong loading areas");
		}
	}

	public void LEGACYAREAS() {
		Double x = 0.0, y = 0.0, z = 0.0;
		float pitch = 0, yaw = 0;
		String name = "", list[], s, owner = "", alias = "";
		World world = null;
		boolean permission = false;

		try {
			BufferedReader br;
			br  = new BufferedReader(new FileReader(areaFile));

			s = br.readLine();
			if (s == null) return;

			while (s != null) {
				do {
					if (s.startsWith("area: ")) name = s.substring(s.indexOf(":") + 2, s.length());
					else if (s.startsWith("author: ")) owner = s.substring(s.indexOf(":") + 2, s.length()).toLowerCase().trim();
					else if (s.startsWith("world: ")) world = Bukkit.getServer().getWorld(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("x: ")) x = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("y: ")) y = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("z: ")) z = Double.parseDouble(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("yaw: ")) yaw = Float.parseFloat(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("pitch: ")) pitch = Float.parseFloat(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("permission: ")) permission = Boolean.parseBoolean(s.substring(s.indexOf(":") + 2, s.length()));
					else if (s.startsWith("alias: ")) alias = s.substring(s.indexOf(":") + 2, s.length());
					
					s = br.readLine();
					if (s == null) break;
				} while (!(s.startsWith("area: ")));

				if ((world == null) || (name == "")) System.out.println("[TH] something went wrong loading areas");
				else Area.newArea(owner, name, (alias == "") ? name : alias, new Location(world, x, y, z, yaw, pitch), permission);
			}

			br.close();
		} catch (IOException e) {
			System.out.println("[TH] something went wrong loading areas");
		}
	}

	public static void saveDeathLocation(Player player, Location location) {
		player.sendMessage(Language.getString("plugin.title") + Language.getString("general.deathlocsave"));
		deaths.put(player, location);
	}

	public static Location getDeathLocation(Player player) {
		return deaths.get(player);
	}

	public static boolean hasDeathLocation(Player player) {
		return deaths.containsKey(player);
	}

	public static void saveBackLocation(Player player, Location location) {
		player.sendMessage(Language.getString("plugin.title") + Language.getString("general.backlocsave"));
		back.put(player, location);
	}

	public static Location getBackLocation(Player player) {
		return back.get(player);
	}

	public static boolean hasBackLocation(Player player) {
		return back.containsKey(player);
	}
}