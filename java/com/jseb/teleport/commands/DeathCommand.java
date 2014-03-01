package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.Config;

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
    	if (!Config.getBoolean("components.deathenabled")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
        else if (!(sender instanceof Player) || ((Player) sender).hasPermission("teleport.death")) {
            if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
            else sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
        } else {
            Player player = (Player) sender;
            if (Storage.hasDeathLocation(player)) {
                player.teleport(Storage.getDeathLocation(player));
                player.sendMessage(Language.getString("plugin.title") + Language.getString("death.teleport"));
            } else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.death.nolocation"));
        }

        return true;
    }
}