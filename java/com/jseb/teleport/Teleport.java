package com.jseb.teleport;

import com.jseb.teleport.commands.*;
import com.jseb.teleport.storage.Storage;
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

    public String title = "[" + ChatColor.RED + "TH" + ChatColor.WHITE + "] " + ChatColor.GREEN;

	public void onEnable() {

		File baseDirectory = this.getDataFolder();
		String filePath = baseDirectory.getAbsolutePath();

		if (!baseDirectory.exists()){
			baseDirectory.mkdirs();
		}

		storage = new Storage(this, filePath);
		settings = new Config(filePath, getConfig(), this);
		updater = new Updater(this);

		init();
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this); // register event listener
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.runTaskLater(this, updater, 20);
	}

	public void onDisable() {
		storage.saveAreas();
		storage.saveHomes();
		settings.saveConfig();
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
        getCommand("accept").setExecutor(new AcceptCommand(this));
        getCommand("deny").setExecutor(new DenyCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("teleport").setExecutor(new TeleportCommand(this));
        getCommand("area").setExecutor(new AreaCommand(this)); 
        getCommand("config").setExecutor(new ConfigCommand(this));
        getCommand("death").setExecutor(new DeathCommand(this));
	}
}