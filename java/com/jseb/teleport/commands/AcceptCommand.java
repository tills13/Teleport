package com.jseb.teleport.commands;

import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Teleport;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;

public class AcceptCommand implements CommandExecutor {
	Teleport plugin;

	public AcceptCommand(Teleport plugin) {
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
			player.sendMessage(plugin.title + "you do not have the required permission.");
			return true;
    	}

		if (plugin.getStorage().homeAccept.containsKey(player)) {
			Map<Player, Home> target = plugin.getStorage().homeAccept.remove(player);
			Player receiver = (Player)target.keySet().toArray()[0];
			Home home = target.get(receiver);

			plugin.getStorage().back.put(receiver, receiver.getLocation());
			receiver.sendMessage(plugin.title + "teleporting to " + player.getName() + "'s home <" + home.getName() + ">");
			player.sendMessage(plugin.title + "teleporting " + receiver.getName() + " to your home <" + home.getName() + ">");

			Location location = home.getLocation();
			TeleportHelper.loadChunkAt(location);
			receiver.teleport(location);
		} else if (plugin.getStorage().playerAccept.containsKey(player)) {
			Player requester = plugin.getStorage().playerAccept.remove(player);

			plugin.getStorage().back.put(requester, requester.getLocation());
			requester.sendMessage(plugin.title + "teleporting to " + player.getName());
			player.sendMessage(plugin.title + "teleporting " + requester.getName() + " to you");

			requester.teleport(player.getLocation());
		} else {
			player.sendMessage(plugin.title + "no pending teleports");
		}

		return true;
    }
}