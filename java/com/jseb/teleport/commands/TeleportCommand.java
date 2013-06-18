package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.storage.Request;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand implements CommandExecutor {
	Teleport plugin;
	Storage storage;

	public TeleportCommand(Teleport plugin) {
		this.plugin = plugin;
		this.storage = plugin.getStorage();
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().playerTeleEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

		if (args.length < 1) {
			helpSyntax(sender);
		} else {
			if (!(sender instanceof Player)) {
	    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
	    		return true;
	    	}

	    	Player player = (Player) sender;

			if (!player.hasPermission("teleport.player")) {
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
				return true;
	    	}

			Player target = plugin.getServer().getPlayer(args[0]);
			if (target == null) {
				player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.playernotfound"), args[0]));
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.playermustbeonline"));
				return true;
			} else {
				new Request(player, target);
			}
		}

		return true;
    }

    public void helpSyntax(CommandSender player) {
        player.sendMessage(Language.getString("plugin.title") + Language.getString("general.commandhelp.title"));
        if (player.hasPermission("teleport.player")) player.sendMessage(Language.getString("plugin.title") + "[/teleport <player>] " + ChatColor.WHITE + Language.getString("teleport.help.player"));
        if (player.hasPermission("teleport.death")) player.sendMessage(Language.getString("plugin.title") + "[/death] " + ChatColor.WHITE + Language.getString("teleport.help.death"));
        if (player.hasPermission("teleport.spawn")) player.sendMessage(Language.getString("plugin.title") + "[/spawn] " + ChatColor.WHITE + Language.getString("teleport.help.spawn"));
        if (player.hasPermission("teleport.back")) player.sendMessage(Language.getString("plugin.title") + "[/back] " + ChatColor.WHITE + Language.getString("teleport.help.back"));
        if (player.hasPermission("teleport.config")) player.sendMessage(Language.getString("plugin.title") + "[/config reload] " + ChatColor.WHITE + Language.getString("teleport.help.config"));
        if (player.hasPermission("teleport.accept")) player.sendMessage(Language.getString("plugin.title") + "[/accept] and [/deny] " + ChatColor.WHITE + Language.getString("teleport.help.accept"));
        player.sendMessage(Language.getString("plugin.title") + "[/home] and [/area] " + ChatColor.WHITE + Language.getString("teleport.help.general"));
    }
}