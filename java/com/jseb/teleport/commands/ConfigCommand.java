package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ConfigCommand implements CommandExecutor {
	Teleport plugin;

	public ConfigCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("teleport.config")) {
			sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			return true;
    	}

    	if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
    		plugin.getSettings().refreshConfig();
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("general.config.reload"));
    	} else {
    		sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/config reload]"));
    	}

		return true;
    }
}