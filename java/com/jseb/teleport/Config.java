package com.jseb.teleport;

import org.bukkit.configuration.file.YamlConfiguration;
import java.util.TreeMap;
import java.util.SortedMap;

public class Config {
	public static void load() {
		YamlConfiguration config = TeleportHelper.getConfig("config.yml");
		for (String entry : defaults().keySet()) if (!config.contains(entry)) config.set(entry, defaults().get(entry));
		TeleportHelper.saveConfig("config.yml", config);
	}

	public static SortedMap<String, Object> defaults() {
		SortedMap<String, Object> defaults = new TreeMap<String, Object>();

		defaults.put("general.maxhomes", 4);
		defaults.put("general.updatenotify", true);
		defaults.put("general.updateenabled", false);
		defaults.put("donttouch.projectname", "teleport-home");
		defaults.put("language.language", "en");
		defaults.put("language.cache", false);
		defaults.put("components.areaenabled", true);
		defaults.put("components.homeenabled", true);
		defaults.put("components.deathenabled", true);
		defaults.put("components.spawnenabled", true);
		defaults.put("components.playerteleenabled", true);
		defaults.put("components.backenabled", true);
		defaults.put("components.bedenabled", true);
		return defaults;
	}

	public static int getInt(String name) {
		return TeleportHelper.getConfig("config.yml").getInt(name);
	}

	public static String getString(String name) {
		return TeleportHelper.getConfig("config.yml").getString(name);
	}

	public static boolean getBoolean(String name) {
		return TeleportHelper.getConfig("config.yml").getBoolean(name);
	}
}