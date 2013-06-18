package com.jseb.teleport.listeners;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;

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
		if ((player.hasPermission("teleport.update.notify")) && (plugin.getUpdater().notify) && (plugin.getSettings().notifyUpdate)) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("general.updateavail"));
		}
	}

	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.hasPermission("teleport.death")) {
			this.plugin.getStorage().deathLocations.put(player, player.getLocation());
			player.sendMessage(Language.getString("plugin.title") + Language.getString("general.deathlocsave"));
		}
	}
}