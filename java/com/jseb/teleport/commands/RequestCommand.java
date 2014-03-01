package com.jseb.teleport.commands;

import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Teleport;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.storage.Request;
import com.jseb.teleport.Language;
import com.jseb.teleport.Config;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;

public class RequestCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("request")) {
			if (args.length == 0) helpSyntax(sender);
			else {
				if (args[0].equalsIgnoreCase("list")) {
				if (!(sender instanceof Player) && args.length == 1) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
			    		if (Request.numRequests(args.length == 2 ? args[1] : sender.getName()) > 0) {
							sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("requests.list.title"), args.length == 2 ? args[1] : "your"));
							int i = 1;
							for (Request mRequest : Request.getRequests(args.length == 2 ? args[1] : sender.getName())) sender.sendMessage(Language.getString("plugin.title") + "  " + i++  + ". " + ChatColor.WHITE + mRequest.requester + " (" + ((mRequest.destination instanceof Home) ? "home: " + ((Home) mRequest.destination).getName() : Language.getString("requests.player")) + ")");
						} else sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
			    	}
				} else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
					if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
					else {
						Player player = (Player) sender;
						Request request = Request.getRequest(player.getName(), args.length == 1 ? "" : args[1]);

						if (request == null && Request.numRequests(player.getName()) != 0) player.sendMessage(Language.getString("plugin.title") + (args.length == 1 ? Language.getString("error.request.multiplerequests") : Language.getString("error.request.requestnotfound")));
						else if (Request.numRequests(player.getName()) == 0) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
						else {
							if (args[0].equalsIgnoreCase("accept")) request.accept();
							else request.deny();
						}
					}
				} else helpSyntax(sender);
			}
		}

		return true;
	}

	public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/request] " + ChatColor.WHITE + Language.getString("general.commandhelp.title"));
    	player.sendMessage(Language.getString("plugin.title") + "[/request accept] or [/request deny] " + ChatColor.WHITE + Language.getString("general.teleport.help"));
    	player.sendMessage(Language.getString("plugin.title") + "[/request list <player>] " + ChatColor.WHITE + Language.getString("requests.info.list"));
    	player.sendMessage(Language.getString("plugin.title") + "[/teleport], [/home], and [/area] " + ChatColor.WHITE + Language.getString("teleport.help.general"));
    }
}