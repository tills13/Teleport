package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;

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
            sender.sendMessage(plugin.title + "this feature has been disabled");
            return true;
        }

    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Only players can execute this command.");
    		return true;
    	}

    	Player player = (Player) sender;

    	if (!player.hasPermission("teleport.spawn")) {
			player.sendMessage(plugin.title + "you do not have the required permission.");
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
						player.sendMessage(plugin.title + "something went wrong :(");
						return true;
					}
					block = block.getRelative(BlockFace.UP);
				}
			} else if (block.getType().equals(Material.AIR)) {
				count = 0; 
				while(block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
					if (count++ > 100) { 
						player.sendMessage(plugin.title + "something went wrong :(");
						return true;
					}
					block = block.getRelative(BlockFace.DOWN);
				}
			}

			location = block.getLocation();
			plugin.getStorage().back.put(player, player.getLocation());

			player.sendMessage(plugin.title + "teleporting to spawn");
			TeleportHelper.loadChunkAt(location);
	        player.teleport(location);
    	} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("set")) {
    			if (!player.hasPermission("teleport.spawn.set")) {
					player.sendMessage(plugin.title + "you do not have the required permission.");
					return true;
    			}

    			Location location = player.getLocation();
    			int x = (int)location.getX();
    			int y = (int)location.getY();
    			int z = (int)location.getZ();
    			player.sendMessage(plugin.title + "setting spawn location (" + x + ", " + y + ", " + z + ")");
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
    	player.sendMessage(plugin.title + "[/spawn] " + ChatColor.WHITE + "commands syntax: ");
    	if (player.hasPermission("teleport.spawn")) player.sendMessage(plugin.title + "[/spawn] " + ChatColor.WHITE + "teleports to spawn");
    	if (player.hasPermission("teleport.spawn.set")) player.sendMessage(plugin.title + "[/spawn set] " + ChatColor.WHITE + "sets the spawn location for the current world");
    }
}