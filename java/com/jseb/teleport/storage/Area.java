package com.jseb.teleport.storage;

import com.jseb.teleport.TeleportHelper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.ArrayList;

public class Area {
	private String name;
	private String author;
	private boolean requirePermission;
	private Location location;
	private String alias;

	public static List<Area> areaList = new ArrayList<Area>();

	public Area(String name, Location location) {
		this.name = name;
		this.location = location;
		this.author = "";
		this.requirePermission = false;
		this.alias = "";

		areaList.add(this);
	}

	public Area(String name, Location location, String author) {
		this(name, location);
		this.author = author;
	}

	public Area(String name, Location location, String author, boolean requirePermission) {
		this(name, location, author);
		this.requirePermission = requirePermission;
	}

	public Area(String name, Location location, String author, boolean requirePermission, String alias) {
		this(name, location, author, requirePermission);
		this.alias = alias;
	}

	public Area(String name, Location location, boolean requirePermission) {
		this(name, location);
		this.requirePermission = requirePermission;
	}

	// ---------

	public Location getLocation() {
		return this.location;
	}

	public String getLocationString() {
		return "(" + (int)this.location.getX() + ", " + (int)this.location.getY() + ", " + (int)this.location.getZ() + ")";
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setPermissions(boolean requirePermission) {
		this.requirePermission = requirePermission;
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

	public String getAuthor() {
		return this.author;
	}

	public boolean getPermission() {
		return this.requirePermission;
	}

	public String getPermissionString() {
		return requirePermission ? "teleport.area.teleport." + this.name : "none";
	}

	public String getAlias() {
		return this.alias;
	}

	public void rename(String newname) {
		this.name = newname;
	}

	public boolean delete() {
		return areaList.remove(this);
	}

	public boolean canTeleportTo(CommandSender player) {
		if (this.requirePermission) {
			if (!(player.hasPermission("teleport.area.teleport." + this.name) || (player.hasPermission("teleport.area.teleport"))) && !player.getName().equalsIgnoreCase(this.author)) {
				return false;
			}
		}

		return true;
	}

	public boolean teleportTo(Player player) {
		if (this.requirePermission) {
			if (!player.hasPermission("teleport.area.teleport." + this.name) && !player.getName().equalsIgnoreCase(this.author)) {
				return false;
			}
		}

		TeleportHelper.loadChunkAt(this.getLocation());
		player.teleport(this.getLocation());
		return true;
	}


	//STATIC MEMBER FUNCTIONS

	public static Area getArea(String name) {
		for (Area area : areaList) {
			if (area.getName().equals(name)) {
				return area;
			}
		}

		return null;
	}

	public static Area getAreaByAlias(String alias) {
		for (Area area : areaList) {
			if (area.getAlias().equals(alias)) {
				return area;
			}
		}

		return null;
	}

	public static int numAreas() {
		return areaList.size();
	}

	public static Object[] toArray() {
		return areaList.toArray();
	}
}