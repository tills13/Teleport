package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;

public class DeathCommand implements CommandExecutor {
	Teleport plugin;

	public DeathCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().deathEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

    	if (!(sender instanceof Player)) {
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
    		return true;
    	}

    	Player player = (Player) sender;

		if (!player.hasPermission("teleport.death")) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			return true;
    	}

		if (plugin.getStorage().deathLocations.containsKey(player)) player.teleport(plugin.getStorage().deathLocations.remove(player));
		else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.death.nolocation"));

		return true;
    }
}