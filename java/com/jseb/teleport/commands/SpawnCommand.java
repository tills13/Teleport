package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Language;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SpawnCommand implements CommandExecutor {
	Teleport plugin;

	public SpawnCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().spawnEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

    	if (!(sender instanceof Player)) {
    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
    		return true;
    	}

    	Player player = (Player) sender;

    	if (!player.hasPermission("teleport.spawn")) {
			player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			return true;
    	}

    	if (args.length == 0) {
    		World world = player.getWorld();
			Location location = world.getSpawnLocation();

			if (!location.getWorld().getEnvironment().equals(Environment.NORMAL)) {
				for (World w : plugin.getServer().getWorlds()) {
					if (w.getEnvironment().equals(Environment.NORMAL)) {
						location = w.getSpawnLocation();
						world = w;
						break;
					}
				}
			}

			Block block = world.getBlockAt(location);

			int count = 0; 
			if (!block.getType().equals(Material.AIR)) {
				while(!block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
					if (count++ > 100) { 
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.general"));
						return true;
					}
					block = block.getRelative(BlockFace.UP);
				}
			} else if (block.getType().equals(Material.AIR)) {
				count = 0; 
				while(block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
					if (count++ > 100) { 
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.general"));
						return true;
					}
					block = block.getRelative(BlockFace.DOWN);
				}
			}

			location = block.getLocation();
			plugin.getStorage().back.put(player, player.getLocation());

			player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.teleport"), "spawn"));
			TeleportHelper.loadChunkAt(location);
	        player.teleport(location);
    	} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("set")) {
    			if (!player.hasPermission("teleport.spawn.set")) {
					player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					return true;
    			}

    			Location location = player.getLocation();
    			int x = (int)location.getX();
    			int y = (int)location.getY();
    			int z = (int)location.getZ();
    			player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("spawn.setspawn"),  ("(" + x + ", " + y + ", " + z + ")")));
    			location.getWorld().setSpawnLocation(x, y, z); 
    		} else {
    			helpSyntax(player);
    		}
    	} else {
    		helpSyntax(player);
    	}

		return true;
    }

    public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/spawn] " + Language.getString("general.commandhelp.title"));
    	if (player.hasPermission("teleport.spawn")) player.sendMessage(Language.getString("plugin.title") + "[/spawn] " + ChatColor.WHITE + Language.getString("spawn.help.spawn"));
    	if (player.hasPermission("teleport.spawn.set")) player.sendMessage(Language.getString("plugin.title") + "[/spawn set] " + ChatColor.WHITE + Language.getString("spawn.help.setspawn"));
    }
}