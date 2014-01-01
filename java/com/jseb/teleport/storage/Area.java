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

	public void setLocation(Location location) { 
		this.location = location;
	}

	public boolean getPermission() {
		return this.permissions;
	}

	public void setPermissions(boolean permissions) {
		this.permissions = permissions;
	}

	public boolean setAlias(String alias) {
		if (getAreaByAlias(alias) == null) {
			this.alias = alias;
			return true;
		}

		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		TeleportHelper.getConfig("areas.yml").set(this.name, newName);
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		TeleportHelper.getConfig("areas.yml").set(this.name + ".owner", owner);
	}

	public void delete() {
		TeleportHelper.getConfig("areas.yml").set(this.name + ".", null);
	}

	public String getPermissionString() {
		return permissions ? "teleport.area.teleport." + this.name : "none";
	}

	public String getAlias() {
		return this.alias;
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

	public boolean teleportTo(Player player) {
		if (canTeleportTo(player)) {
			TeleportHelper.loadChunkAt(this.getLocation());
			player.teleport(this.getLocation());
			return true;
		} else return false;
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
		areas.set(path + "location.world", location.getWorld());
		areas.set(path + "permissions", permissions);
		return getArea(name);
	}

	public static Area getArea(String name) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		Map areaList = areas.getValues(true);

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
		return getArea(name) != null;
	}

	public static Area getAreaByAlias(String alias) {
		YamlConfiguration areas = TeleportHelper.getConfig("areas.yml");
		Map<String, Object> areaList = areas.getValues(true);

		for (String area : areaList.keySet()) if (areas.contains(area + "." + alias)) return getArea(area);
		return null;
	}

	public static int numAreas() {
		return TeleportHelper.getConfig("areas.yml").getValues(true).size();
	}

	public static ArrayList<Area> getAreaList() {
		Map<String, Object> areas = TeleportHelper.getConfig("areas.yml").getValues(true);
		ArrayList<Area> areaList = new ArrayList<Area>();

		for (String area : areas.keySet()) areaList.add(getArea(area));
		return areaList;
	}
}