package com.jseb.teleport.storage;

import com.jseb.teleport.TeleportHelper;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;

public class Home {
	private String owner;
	private String name;
	private Location location;
	private List<String> residents;
	private boolean isDefault;

	public static Map<String, List<Home>> homeList = new HashMap<String, List<Home>>();

	public Home(String owner, String name, Location location) {
		this.owner = owner.toLowerCase();
		this.name = name;
		this.isDefault = false;
		this.location = location;
		

		List<Home> ownersHomes = homeList.get(owner);
		if (ownersHomes == null) ownersHomes = new ArrayList<Home>();

		ownersHomes.add(this);
		homeList.put(owner, ownersHomes);
	}

	public Home(String owner, String name, Location location, boolean isDefault) {
		this(owner, name, location);
		if (isDefault) this.makeDefault();
	}

	public void addResident(String player) {
		if (residents == null) residents = new ArrayList<String>();
		residents.add(player);
	}

	public void makeDefault() {
		for (Home home : homeList.get(owner)) {
			if (home.isDefault) {
				home.isDefault = false;
				break;
			}
		}

		this.isDefault = true;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void removeResident(String player) {
		if (residents == null) return;
		residents.remove(player);
	}

	public void rename(String newName) {
		this.name = newName;
	}

	public boolean delete() {
		List<Home> list = Home.homeList.get(owner);
		return list.remove(this);
	}


	public String getOwner() {
		return this.owner;
	}

	public String getName() {
		return this.name;
	}

	public Location getLocation() {
		return this.location;
	}

	public List<String> getResidents() {
		return this.residents;
	}

	public boolean getIsDefault() {
		return this.isDefault;
	}

	public boolean isResident(Player player) {
		return residents.contains(player.getName());
	}

	public void teleportTo(Player player) {
		TeleportHelper.loadChunkAt(this.getLocation());
		player.teleport(this.getLocation());
	}

	//STATIC MEMBER METHODS

	public static Home getHome(Player player, String name) {
		for (String owners : homeList.keySet()) {
			if (player.getName().equalsIgnoreCase(owners)) {
				for (Home home : homeList.get(owners)) {
					if (home.getName().equalsIgnoreCase(name)) {
						return home;
					}
				}
			}
		}
		return null;
	}

	public static Home getDefault(Player owner) {
		for (String player : homeList.keySet()) {
			if (owner.getName().equalsIgnoreCase(player)) {
				for (Home home : homeList.get(player)) {
					if (home.isDefault) {
						return home;
					}
				}
			}
		}
		return null;
	}

	public static List<Home> getHomes(String owner) {
		return homeList.get(owner.toLowerCase());
	}

	public static int numHomes(Player owner) {
		List<Home> list = Home.homeList.get(owner.getName().toLowerCase());
		return list == null ? 0 : list.size();
	}
}