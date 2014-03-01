package com.jseb.teleport.listeners;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.Config;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {
	public Teleport plugin;

	public PlayerListener(Teleport plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("teleport.update.notify") && plugin.getUpdater().notify && Config.getBoolean("general.updatenotify")) player.sendMessage(Language.getString("plugin.title") + Language.getString("general.updateavail"));
	}

	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.hasPermission("teleport.death")) {
			Storage.saveDeathLocation(player, player.getLocation());
			player.sendMessage(Language.getString("plugin.title") + Language.getString("general.deathlocsave"));
		}
	}
}