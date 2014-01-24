package com.jseb.teleport.storage;

import com.jseb.teleport.TeleportHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.World;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Home {
	private String owner;
	private String name;
	private Location location;
	private List<String> residents;
	private boolean isDefault;

	protected Home(String owner, String name, Location location, List<String> residents, boolean isDefault) {
		this.owner = owner.toLowerCase();
		this.name = name.toLowerCase();
		this.location = location;
		this.residents = residents;
		this.isDefault = isDefault;
	}

	// ---------

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location newLocation) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		this.location = newLocation;

		String path = this.owner + "." + this.name + ".";
		homes.set(path + ".location.x", newLocation.getX());
		homes.set(path + ".location.y", newLocation.getY());
		homes.set(path + ".location.z", newLocation.getZ());
		homes.set(path + ".location.pitch", newLocation.getPitch());
		homes.set(path + ".location.yaw", newLocation.getYaw());
		homes.set(path + ".location.world", newLocation.getWorld().getName());
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String newOwner) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		newHome(newOwner, this.name, this.location, false);
		this.delete();
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		homes.set(this.owner + "." + newName, homes.getConfigurationSection(this.owner + "." + this.name));
		homes.set(this.owner + "." + this.name, null);
		this.name = newName;
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public void delete() {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		if ((homes.getConfigurationSection(owner)).getValues(false).size() == 1) homes.set(owner, null);
		else homes.set(this.owner + "." + this.name, null);
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public void setIsDefault(boolean isDefault) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");

		if (isDefault) {
			Home defaultHome = getDefaultHome(this.owner);
			if (defaultHome != null) {
				homes.set(defaultHome.getOwner() + "." + defaultHome.getName() + ".isDefault", false);
				defaultHome.isDefault = false;
			}
		}

		homes.set(this.owner + "." + this.name + ".isDefault", isDefault);
		this.isDefault = isDefault;
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public boolean getIsDefault() {
		return this.isDefault;
	}

	public void addResident(String player) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		List<String> mResidents = homes.getStringList(this.owner + "." + this.name + ".residents");

		if (!mResidents.contains(player)) mResidents.add(player);
		this.residents = mResidents;

		homes.set(this.owner + "." + this.name + ".residents", residents);
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public void removeResident(String player) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		List<String> mResidents = homes.getStringList(this.owner + "." + this.name + ".residents");

		if (mResidents.contains(player)) mResidents.remove(player);
		this.residents = mResidents;

		homes.set(this.owner + "." + this.name + ".residents", residents);
		TeleportHelper.saveConfig("homes.yml", homes);
	}

	public List<String> getResidents() {
		return this.residents;
	}

	public boolean isResident(String player) {
		return getResidents().contains(player);
	}

	public String getLocationString() {
		return "(" + (int)this.location.getX() + ", " + (int)this.location.getY() + ", " + (int)this.location.getZ() + ")";
	}

	public boolean canTeleportTo(String player) {
		if (isResident(player) || player.equals(this.owner)) return true;
		return false;
	}

	public void teleportTo(Player player) {
		if (canTeleportTo(player.getName().toLowerCase())) {
			Storage.saveBackLocation(player, player.getLocation());
			TeleportHelper.loadChunkAt(this.getLocation());
			player.teleport(this.getLocation());
		} else return;
	}

	//STATIC MEMBER METHODS
	public static Home newHome(String owner, String name, Location location, boolean isDefault) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		String path = owner + "." + name + ".";

		homes.set(path + "location.x", location.getX());
		homes.set(path + "location.y", location.getY());
		homes.set(path + "location.z", location.getZ());
		homes.set(path + "location.pitch", location.getPitch());
		homes.set(path + "location.yaw", location.getYaw());
		homes.set(path + "location.world", location.getWorld().getName());
		homes.set(path + "residents", new ArrayList<String>());
		
		Home home = new Home(owner, name, location, new ArrayList<String>(), isDefault);

		if (isDefault) {
			Home defaultHome = getDefaultHome(owner);
			if (defaultHome != null) homes.set(defaultHome.getOwner() + "." + defaultHome.getName() + ".isDefault", false);
		}

		homes.set(path + "isDefault", isDefault);
		TeleportHelper.saveConfig("homes.yml", homes);
		return home;
	}

	public static Home getHome(String owner, String name) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		if (homes.contains(owner + "." + name)) {
			String path = owner + "." + name + ".";
			World world = Bukkit.getWorld(homes.getString(path + "location.world"));
			int x = homes.getInt(path + "location.x");
			int y = homes.getInt(path + "location.y");
			int z = homes.getInt(path + "location.z");
			int yaw = homes.getInt(path + "location.yaw");
			int pitch = homes.getInt(path + "location.pitch");
			Location location = new Location(world, x, y, z, yaw, pitch);
			List<String> residents = homes.getStringList(path + "residents");
			boolean isDefault = homes.getBoolean(path + "isDefault");

			return new Home(owner, name, location, residents, isDefault);
		} else return null;
	}

	public static Home getDefaultHome(String owner) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		if (homes.contains(owner)) {
			Map<String, Object> homeList = homes.getConfigurationSection(owner).getValues(false);

			for (String home : homeList.keySet()) if (homes.getBoolean(owner + "." + home + ".isDefault")) return getHome(owner, home);
		}
		
		return null;
	}

	public static int numHomes(String owner) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		return homes.contains(owner) ? homes.getConfigurationSection(owner).getValues(false).size() : 0;
	}

	public static List<Home> getHomeList(String owner) {
		YamlConfiguration homes = TeleportHelper.getConfig("homes.yml");
		ArrayList<Home> homeList = new ArrayList<Home>();

		if (homes.contains(owner)) {
			Map<String, Object> homeMap = homes.getConfigurationSection(owner).getValues(false);
			for (String home : homeMap.keySet()) homeList.add(getHome(owner, home));
		}

		return homeList;
	}
}