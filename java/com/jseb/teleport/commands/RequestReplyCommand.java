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
    		if (args[0].equalsIgnoreCase("list")) {
	    		if (args.length == 1) {
	    			if (!(sender instanceof Player)) {
			    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    		return true;
			    	} 

	    			if (args[0].equalsIgnoreCase("list")) {
	    				if (Request.numRequests((Player)sender) > 0) {
	    					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("requests.list.title"), "My"));
	    					int i = 1;
	    					for (Request mrequest : Request.getRequests((Player)sender)) {
	    						sender.sendMessage(Language.getString("plugin.title") + "  " + i++  + ". " + ChatColor.WHITE + mrequest.requester.getName() + " (" + ((mrequest.destination instanceof Home) ? ((Home)mrequest.destination).getName() : "player") + ")");
	    					}
	    				} else {
							sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
	    				}
	    			} else {
	    				return true;
	    			}
	    		} else if (args.length == 2) {
	    				player = plugin.getServer().getPlayer(args[1]);

	    				if (player == null) {
	    					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.playernotfound"), args[1]));
	    					return true;
	    				}

	    				if (Request.numRequests(player) > 0) {
	    					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("requests.list.title"), (player.getName() + "'s")));
	    					int i = 1;
	    					for (Request mrequest : Request.getRequests(player)) {
	    						sender.sendMessage(Language.getString("plugin.title") + "  " + i++  + ". " + ChatColor.WHITE + mrequest.requester.getName() + " (" + ((mrequest.destination instanceof Home) ? ((Home)mrequest.destination).getName() : "player") + ")");
	    					}
	    				} else {
							sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
	    				}
    			} else {
    				return true;
    			}
    		}
    		return true;
    	}

    	if (!(sender instanceof Player)) {
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
    		return true;
    	}

 		player = (Player) sender;
    	Request request = null;

    	if (args.length == 0) {
			request = Request.getRequest(player);

			if (request == null && Request.numRequests(player) != 0) {
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.multiplerequests"));
				return true;
			} else if (Request.numRequests(player) == 0) {
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
				return true;
			} 
		} else if (args.length == 1) {
			request = Request.getRequest(player, args[0]);

			if (request == null && Request.numRequests(player) != 0) {
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.requestnotfound"));
				return true;
			} else if (Request.numRequests(player) == 0) {
				player.sendMessage(Language.getString("plugin.title") + Language.getString("error.request.norequests"));
				return true;
			}
		} else {
			return true;
		}

    	if (cmd.getName().equalsIgnoreCase("accept")) {
    		request.accept();
    	} else if (cmd.getName().equalsIgnoreCase("deny")) {
    		request.deny();
    	} 

		return true;
    }
}