package com.jseb.teleport;

import com.jseb.teleport.Language;
import com.jseb.teleport.commands.*;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.storage.Request;
import com.jseb.teleport.listeners.PlayerListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public class Teleport extends JavaPlugin {
	private Updater updater;
    private Config settings;
    private Storage storage;
    
	public void onEnable() {
		saveResource("en.lang", true);
		storage = new Storage();
		settings = new Config(getConfig(), this);
		updater = new Updater(this);
		Language.plugin = this;
		Request.plugin = this;
		TeleportHelper.plugin = this;
		Language.reload();
		
		init();
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this); // register event listener
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.runTaskLaterAsynchronously(this, updater, 20);
	}

	public void onDisable() {
		//settings.saveConfig();
	}

	public Config getSettings() {
		return this.settings;
	}

	public Storage getStorage() {
		return this.storage;
	}

	public Updater getUpdater() {
		return this.updater;
	}

	public void init() {
		getCommand("home").setExecutor(new HomeCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("request").setExecutor(new RequestCommand());
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("teleport").setExecutor(new TeleportCommand(this));
        getCommand("area").setExecutor(new AreaCommand(this)); 
        getCommand("config").setExecutor(new ConfigCommand(this));
        getCommand("death").setExecutor(new DeathCommand(this));
        getCommand("bed").setExecutor(new BedCommand(this));
	}
}