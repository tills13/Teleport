package com.jseb.teleport.storage;

import com.jseb.teleport.TeleportHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Area {
	private String name;
	private String owner;
	private boolean permissions;
	private Location location;
	private String alias;

	protected Area(String name, Location location, String owner, boolean permissions, String alias) {
		this.name = name;
		this.location = location;
		this.owner = owner;
		this.permissions = false;
		this.alias = alias;
		this.permissions = permissions;
	}

	// ---------

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location newLocation) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		this.location = newLocation;

		areas.set(this.name + ".location.x", newLocation.getX());
		areas.set(this.name + ".location.y", newLocation.getY());
		areas.set(this.name + ".location.z", newLocation.getZ());
		areas.set(this.name + ".location.pitch", newLocation.getPitch());
		areas.set(this.name + ".location.yaw", newLocation.getYaw());
		areas.set(this.name + ".location.world", newLocation.getWorld().getName());
		TeleportHelper.saveConfig("areas.yml", areas);
	}

	public boolean getPermission() {
		return this.permissions;
	}

	public void setPermissions(boolean newPermissions) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		this.permissions = newPermissions;
		areas.set(this.name + ".permissions", newPermissions);
		TeleportHelper.saveConfig("areas.yml", areas);	
	}

	public String getAlias() {
		return this.alias;
	}

	public boolean setAlias(String newAlias) {
		if (getAreaByAlias(newAlias) == null) {
			YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
			this.alias = newAlias;
			areas.set(this.name + ".alias", newAlias);
			TeleportHelper.saveConfig("areas.yml", areas);
			return true;
		}

		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		areas.set(newName, areas.getConfigurationSection(this.name));
		areas.set(this.name, null);
		TeleportHelper.saveConfig("areas.yml", areas);
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String newOwner) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		this.owner = newOwner;
		areas.set(this.name + ".owner", owner);
		TeleportHelper.saveConfig("areas.yml", areas);
	}

	public void delete() {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		areas.set(this.name, null);
		TeleportHelper.saveConfig("areas.yml", areas);
	}

	public String getPermissionString() {
		return permissions ? "teleport.area.teleport." + this.name : "none";
	}

	public String getLocationString() {
		return "(" + (int)this.location.getX() + ", " + (int)this.location.getY() + ", " + (int)this.location.getZ() + ")";
	}

	public boolean canTeleportTo(Player player) {
		if (this.permissions) {
			if (!(player.hasPermission("teleport.area.teleport." + this.name) || (player.hasPermission("teleport.area.teleport"))) && !player.getName().equalsIgnoreCase(this.owner)) {
				return false;
			}
		}

		return true;
	}

	public void teleportTo(Player player) {
		if (canTeleportTo(player)) {
			Storage.saveBackLocation(player, player.getLocation());
			TeleportHelper.loadChunkAt(this.getLocation());
			player.teleport(this.getLocation());
		} else return;
	}

	//STATIC MEMBER FUNCTIONS
	public static Area newArea(String owner, String name, String alias, Location location, boolean permissions) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		String path = name + ".";

		areas.set(path + "owner", owner);
		areas.set(path + "alias", alias);
		areas.set(path + "location.x", location.getX());
		areas.set(path + "location.y", location.getY());
		areas.set(path + "location.z", location.getZ());
		areas.set(path + "location.pitch", location.getPitch());
		areas.set(path + "location.yaw", location.getYaw());
		areas.set(path + "location.world", location.getWorld().getName());
		areas.set(path + "permissions", permissions);

		TeleportHelper.saveConfig("areas.yml", areas);
		return getArea(name);
	}

	public static Area getArea(String name) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");

		if (areas.contains(name)) {
			String path = name + ".";
			World world = Bukkit.getWorld(areas.getString(path + "location.world"));
			int x = areas.getInt(path + "location.x");
			int y = areas.getInt(path + "location.y");
			int z = areas.getInt(path + "location.z");
			int yaw = areas.getInt(path + "location.yaw");
			int pitch = areas.getInt(path + "location.pitch");
			Location location = new Location(world, x, y, z, yaw, pitch);

			String owner = areas.getString(path + "owner");
			String alias = areas.getString(path + "alias");
			boolean permissions = areas.getBoolean(path + "permissions");
			return new Area(name, location, owner, permissions, alias);
		} else return null;
	}

	public static boolean areaExists(String name) {
		return TeleportHelper.getConfig("areas.yml").contains(name);
	}

	public static Area getAreaByAlias(String alias) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		Map<String, Object> areaList = areas.getValues(false);

		for (String area : areaList.keySet()) if (areas.getString(area + ".alias").equals(alias)) return getArea(area);
		return null;
	}

	public static int numAreas() {
		return TeleportHelper.getConfig("areas.yml").getValues(false).size();
	}

	public static ArrayList<Area> getAreaList() {
		Map<String, Object> areas = TeleportHelper.getConfig("areas.yml").getValues(false);
		ArrayList<Area> areaList = new ArrayList<Area>();

		for (String area : areas.keySet()) areaList.add(getArea(area));
		return areaList;
	}
}