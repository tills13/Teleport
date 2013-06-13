package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class BackCommand implements CommandExecutor {
	Teleport plugin;

	public BackCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().backEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

    	if (!(sender instanceof Player)) {
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
    		return true;
    	}

    	Player player = (Player) sender;

    	if (!player.hasPermission("teleport.back")) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			return true;
    	}

		if (plugin.getStorage().back.containsKey(player)) {
			Location location = plugin.getStorage().back.get(player);
			TeleportHelper.loadChunkAt(location);
			player.teleport(location);
			player.sendMessage(Language.getString("plugin.title") + Language.getString("back.teleport"));
		} else {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.back.nolocation"));
		}

		return true;
    }
}