package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
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
			sender.sendMessage(plugin.title + "you do not have the required permission.");
			return true;
    	}

    	if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
    		plugin.getSettings().refreshConfig();
    		sender.sendMessage(plugin.title + "config reloaded");
    	} else {
    		sender.sendMessage(plugin.title + "syntax: [/config reload]");
    	}

		return true;
    }
}