package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Language;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BedCommand implements CommandExecutor {
	Teleport plugin;

	public BedCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().bedEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

    	if (!(sender instanceof Player)) {
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
    		return true;
    	}

    	Player player = (Player) sender;

		if (!player.hasPermission("teleport.bed")) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			return true;
    	}

		Location bedLocation = player.getBedSpawnLocation();
		if (bedLocation == null) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.bed.bednotfound"));
		} else {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("bed.teleport"));
			TeleportHelper.loadChunkAt(bedLocation);
			player.teleport(bedLocation);
		}

		return true;
    }
}