package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Language;
import com.jseb.teleport.Config;
import com.jseb.teleport.storage.Storage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SpawnCommand implements CommandExecutor {
	Teleport plugin;

	public SpawnCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!Config.getBoolean("components.spawnenabled")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
        else {
        	if (!(sender instanceof Player) || !((Player) sender).hasPermission("teleport.spawn")) {
				if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
				else sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
	    	} else {
				Player player = (Player) sender;

				if (args.length == 0) {
					Location spawn = TeleportHelper.getSafeTeleportLocation(player.getWorld().getSpawnLocation());
					Storage.saveBackLocation(player, player.getLocation());

					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.teleport"), "spawn"));
					TeleportHelper.loadChunkAt(spawn);
			        player.teleport(spawn);
		    	} else if (args.length == 1) {
		    		if (args[0].equalsIgnoreCase("set")) {
		    			if (!player.hasPermission("teleport.spawn.set")) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
		    			else {
		    				Location location = player.getLocation();
			    			int x = (int) location.getX();
			    			int y = (int) location.getY();
			    			int z = (int) location.getZ();
			    			player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("spawn.setspawn"),  ("(" + x + ", " + y + ", " + z + ")")));
			    			location.getWorld().setSpawnLocation(x, y, z); 
		    			}
		    		} else helpSyntax(player);
		    	} else helpSyntax(player);
	    	}
	    }

		return true;
    }

    public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/spawn] " + Language.getString("general.commandhelp.title"));
    	if (player.hasPermission("teleport.spawn")) player.sendMessage(Language.getString("plugin.title") + "[/spawn] " + ChatColor.WHITE + Language.getString("spawn.help.spawn"));
    	if (player.hasPermission("teleport.spawn.set")) player.sendMessage(Language.getString("plugin.title") + "[/spawn set] " + ChatColor.WHITE + Language.getString("spawn.help.setspawn"));
    }
}