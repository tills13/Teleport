package com.jseb.teleport;

import com.jseb.teleport.Language;
import com.jseb.teleport.commands.*;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.listeners.PlayerListener;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Teleport extends JavaPlugin {
	private Updater updater;
	private Config settings;
	private Storage storage;
	
	public void onEnable() {
		saveResource("en.lang", true);
		TeleportHelper.plugin = this;
		Language.plugin = this;

		storage = new Storage(this);
		Language.reload();
		Config.load();
		
		init();
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this); // register event listener
	}

	public void onDisable() {
		//settings.saveConfig();
	}

	public void init() {
		getCommand("home").setExecutor(new HomeCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("request").setExecutor(new RequestCommand());
		getCommand("back").setExecutor(new BackCommand(this));
		getCommand("teleport").setExecutor(new TeleportCommand(this));
		getCommand("area").setExecutor(new AreaCommand(this)); 
		getCommand("death").setExecutor(new DeathCommand(this));
		getCommand("bed").setExecutor(new BedCommand(this));

		//aliases
		getCommand("hm").setExecutor(new HomeCommand(this));
		getCommand("sp").setExecutor(new SpawnCommand(this));
		getCommand("r").setExecutor(new RequestCommand());
		getCommand("tp").setExecutor(new TeleportCommand(this));
		getCommand("ar").setExecutor(new AreaCommand(this)); 
	}
}