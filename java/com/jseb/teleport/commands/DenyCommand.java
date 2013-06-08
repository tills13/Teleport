package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class DenyCommand implements CommandExecutor {
	Teleport plugin;

	public DenyCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Only players can execute this command.");
    		return true;
    	}

    	Player player = (Player) sender;

		if (!player.hasPermission("teleport.accept")) {
			player.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
			return true;
    	}

		if (plugin.getStorage().homeAccept.containsKey(player)) {
			Map<Player, Home> target = plugin.getStorage().homeAccept.remove(player);
			Player receiver = (Player)target.keySet().toArray()[0];

			receiver.sendMessage(Language.getString("plugin.title") + "teleport request denied");
			player.sendMessage(Language.getString("plugin.title") + "denied " + receiver.getName()  + "'s teleport request");
		} else if (plugin.getStorage().playerAccept.containsKey(player)) {
			Player requester = plugin.getStorage().playerAccept.remove(player);

			requester.sendMessage(Language.getString("plugin.title") + "teleport request denied");
			player.sendMessage(Language.getString("plugin.title") + "denied " + requester.getName()  + "'s teleport request");
		} else {
			player.sendMessage(Language.getString("plugin.title") + "no pending teleports");
		}

		return true;
    }
}