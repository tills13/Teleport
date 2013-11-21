package com.jseb.teleport.commands;

import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Teleport;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.storage.Request;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;

public class RequestReplyCommand implements CommandExecutor {
	Teleport plugin;

	public RequestReplyCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;

		if (cmd.getName().equalsIgnoreCase("request")) {
			if (args.length == 0) {
				helpSyntax(sender);
				return true;
			}

			if (args[0].equalsIgnoreCase("list")) {
				if (!(sender instanceof Player) && args.length == 1) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	} 

				if (args.length == 2) player = plugin.getServer().getPlayer(args[1]);
				else player = (Player) sender;

				if (player == null) {
					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.playernotfound"), args[1]));
					return true;
				}

				if (Request.numRequests(player) > 0) {
					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("requests.list.title"), player.getName()));
					int i = 1;
					for (Request mrequest : Request.getRequests(player)) sender.sendMessage(Language.getString("plugin.title") + "  " + i++  + ". " + ChatColor.WHITE + mrequest.requester.getName() + " (" + ((mrequest.destination instanceof Home) ? ((Home)mrequest.destination).getName() : Language.getString("requests.player")) + ")");
				} else sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
			} else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
					return true;
				}

				player = (Player) sender;
				Request request = Request.getRequest(player, args.length == 1 ? "" : args[1]);
				System.out.println("test");

				if (request == null && Request.numRequests(player) != 0) player.sendMessage(Language.getString("plugin.title") + (args.length == 1 ? Language.getString("error.request.multiplerequests") : Language.getString("error.request.requestnotfound")));
				else if (Request.numRequests(player) == 0) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
				else {
					if (args[0].equalsIgnoreCase("accept")) request.accept();
					else request.deny();
				}
			} else helpSyntax(sender);
			
			return true;
		}

		return true;
	}

	public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/request] " + ChatColor.WHITE + Language.getString("general.commandhelp.title"));
    	player.sendMessage(Language.getString("plugin.title") + "[/request accept] or [/request deny]" + ChatColor.WHITE + Language.getString("general.teleport.help"));
    	player.sendMessage(Language.getString("plugin.title") + "[/request list <player>]" + ChatColor.WHITE + Language.getString("general.teleport.help"));
    	player.sendMessage(Language.getString("plugin.title") + "[/teleport], [/home], and [/area] " + ChatColor.WHITE + Language.getString("teleport.help.general"));
    }
}