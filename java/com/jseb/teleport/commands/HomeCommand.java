package com.jseb.teleport.commands;

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
import java.util.HashMap;
import java.util.List;

public class HomeCommand implements CommandExecutor {
	Teleport plugin;

	public HomeCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!plugin.getSettings().homeEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + "this feature has been disabled");
            return true;
        }

    	Player player;

    	if (args.length == 0) {
			//send to syntax help
			helpSyntax(sender);
		} else {
			if (args[0].equalsIgnoreCase("set")) {
				if (!sender.hasPermission("teleport.sethome")) {
					sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length < 2) {
					player.sendMessage(Language.getString("plugin.title") + "syntax: [/home set <name> <default>]");
					return true;
				} else {
					if (Home.numHomes(player) < plugin.getSettings().maxHomes) {
						if (args.length == 3) {
							if (args[2].equalsIgnoreCase("default")) {
								new Home(player.getName(), args[1], player.getLocation(), true);
								player.sendMessage(Language.getString("plugin.title") + "default home location saved");
							} else {
								player.sendMessage(Language.getString("plugin.title") + "unknown argument: " + args[2]);
							}
						} else {
							Home home = Home.getHome(player, args[1]);
							if (home != null) {
								home.setLocation(player.getLocation());
								player.sendMessage(Language.getString("plugin.title") + args[1] + "'s location updated");
							} else {
								new Home(player.getName(), args[1], player.getLocation());
								player.sendMessage(Language.getString("plugin.title") + "home location saved");
							}
						}
					} else {
						player.sendMessage(Language.getString("plugin.title") + "maximum of " + plugin.getSettings().maxHomes + " homes on this server");
					}
				}
			} else if (args[0].equalsIgnoreCase("setdefault")) {
				if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length != 2) {
					player.sendMessage(Language.getString("plugin.title") + "set defaults using [/home setdefault <home name>");
					return true;
				}

				Home home = Home.getHome(player, args[1]);

				if (home == null && Home.numHomes(player) != 0) {
					player.sendMessage(Language.getString("plugin.title") + "no home named " + args[1]);
					return true;
				} else if (Home.numHomes(player) == 0) {
					player.sendMessage(Language.getString("plugin.title") + "no homes saved");
					return true;
				}

				player.sendMessage(Language.getString("plugin.title") + home.getName() + " is now your default home");
				home.makeDefault();
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("teleport.sethome")) {
					sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

		    	if (Home.numHomes(player) > 0) {
					Home home = Home.getHome(player, args[1]);

					if (home != null) {
						home.delete();
						player.sendMessage(Language.getString("plugin.title") + "home removed");
					} else {
						player.sendMessage(Language.getString("plugin.title") + "home not found");
					}
				} else {
					player.sendMessage(Language.getString("plugin.title") + "no homes saved");
				}	
			} else if (args[0].equalsIgnoreCase("teleport")) {
				if (!sender.hasPermission("teleport.teleport")) {
					sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length == 1) {
					Home home = Home.getDefault(player);

					if (home == null && Home.numHomes(player) != 0) {
						player.sendMessage(Language.getString("plugin.title") + "no default home set");
						return true;
					} else if (Home.numHomes(player) == 0) {
						player.sendMessage(Language.getString("plugin.title") + "no homes saved");
						return true;
					}

					player.sendMessage(Language.getString("plugin.title") + "teleporting to " + home.getName());
					plugin.getStorage().back.put(player, player.getLocation());
					home.teleportTo(player);
				} else {
					Home home = Home.getHome(player, args[1]);

					if (home == null && Home.numHomes(player) != 0) {
						player.sendMessage(Language.getString("plugin.title") + "no home named " + args[1]);
						return true;
					} else if (Home.numHomes(player) == 0) {
						player.sendMessage(Language.getString("plugin.title") + "no homes saved");
						return true;
					}

					player.sendMessage(Language.getString("plugin.title") + "teleporting to " + home.getName());
					plugin.getStorage().back.put(player, player.getLocation());
					home.teleportTo(player);
				}
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length != 3) {
					player.sendMessage(Language.getString("plugin.title") + "syntax: [/home rename <current name> <new name>");
				} else {
					if (Home.numHomes(player) > 0) {
						Home home = Home.getHome(player, args[1]);

						if (home != null) {
							home.rename(args[2]);
							player.sendMessage(Language.getString("plugin.title") + "renamed " + args[1] + " to " + args[2]);
						} else {
							player.sendMessage(Language.getString("plugin.title") + "home not found");
						}
					} else {
						player.sendMessage(Language.getString("plugin.title") + "no homes saved");
					}
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length == 1) {
					if (!(sender instanceof Player)) {
			    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
			    		sender.sendMessage(Language.getString("plugin.title") + "try [/home list <player name>]");
			    		return true;
			    	}

			    	player = (Player) sender;

					List<Home> list = Home.getHomes(player.getName());

					if (list == null) {
						player.sendMessage(Language.getString("plugin.title") + "no homes saved");
						return true;
					} else {
						player.sendMessage(Language.getString("plugin.title") + "My homes: ");

						int i = 1;
						for (Home home: list) {
							String message = "   " + ChatColor.GREEN + i++ + ". " + ChatColor.WHITE + home.getName();
							if (home.isDefault) message += " (default)";
							message += " (" + (int)home.getLocation().getX() + ", " + (int)home.getLocation().getY() + ", " + (int)home.getLocation().getZ() + ")";
							player.sendMessage(message);
						}
					}
				} else if (args.length == 2) {
					if (!sender.hasPermission("teleport.list")) {
						sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
						return true;
			    	}

	    			Player p = plugin.getServer().getPlayer(args[1]);
	    			List<Home> list;

	    			if (p == null) {
	    				list = Home.getHomes(args[1]);
	    				if (list == null) {
	    					sender.sendMessage(Language.getString("plugin.title") + "no homes saved for " + args[1]);
	    					return true;
	    				}
	    			} else {
	    				list = Home.getHomes(p.getName());
	    				args[1] = p.getName();
	    			}

					sender.sendMessage(Language.getString("plugin.title") + args[1] + "'s homes: ");

					int i = 1;
					for (Home home: list) {
						String message = "   " + ChatColor.GREEN + i++ + ". " + ChatColor.WHITE + home.getName();
						if (home.isDefault) message += " (default)";
						message += " (" + (int)home.getLocation().getX() + ", " + (int)home.getLocation().getY() + ", " + (int)home.getLocation().getZ() + ")";
						sender.sendMessage(message);
					}
				}
			} else if (args[0].equalsIgnoreCase("player")) {
				if (!sender.hasPermission("teleport.otherhome")) {
					sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + "Only players can execute this command.");
		    		return true;
		    	}

		    	player = (Player) sender;

				if ((args.length < 2) || (args.length > 3)) {
					//invalid syntax
				} else {
					Player receiver = plugin.getServer().getPlayer(args[1]);

					if (receiver == null) {
						player.sendMessage(Language.getString("plugin.title") + "could not find player " + args[1]);
						player.sendMessage(Language.getString("plugin.title") + "the player must be online");
						return true; 
					}

					
					Home home;
					if (args.length == 2) {
						home = Home.getDefault(receiver);

						if (home == null && Home.numHomes(receiver) != 0) {
							player.sendMessage(Language.getString("plugin.title") + receiver.getName() + " has no default home set");
							return true;
						} else if (Home.numHomes(receiver) == 0) {
							player.sendMessage(Language.getString("plugin.title") + receiver.getName() + " has no saved homes");
							return true;
						}
					} else {
						home = Home.getHome(receiver, args[2]);

						if (home == null && Home.numHomes(receiver) != 0) {
							player.sendMessage(Language.getString("plugin.title") + receiver.getName() + " has no home named " + args[2]);
							return true;
						} else if (Home.numHomes(receiver) == 0) {
							player.sendMessage(Language.getString("plugin.title") + receiver.getName() + " has no saved homes");
							return true;
						}
					}

					player.sendMessage(Language.getString("plugin.title") + "waiting for authorization...");
					receiver.sendMessage(Language.getString("plugin.title") + player.getName() + " wishes to teleport to " + home.getName());
					receiver.sendMessage(Language.getString("plugin.title") + "type /accept to allow or /deny to deny");
					Map<Player, Home> map = new HashMap<Player, Home>();
					map.put(player, home);
					plugin.getStorage().homeAccept.put(receiver, map);

				}
			} else {
				sender.sendMessage(Language.getString("plugin.title") + "invalid argument");
				//send to syntax help
				helpSyntax(sender);
				return true;
			}
		}
	   
		return true;
    }

    public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/home] " + ChatColor.WHITE + "commands syntax: ");
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home set <name> <default>] " + ChatColor.WHITE + "sets a new home/optionally default");
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home setdeafult <name>] " + ChatColor.WHITE + "sets home as default");
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home remove <name>] " + ChatColor.WHITE + "removes a home");
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home rename <name> <new name>] " + ChatColor.WHITE + "renames a home to <new name>");
    	if (player.hasPermission("teleport.teleport")) player.sendMessage(Language.getString("plugin.title") + "[/home teleport <name>] " + ChatColor.WHITE + "teleports to one of your homes");
    	player.sendMessage(Language.getString("plugin.title") + "[/home list <name>] " + ChatColor.WHITE + "shows your homes or another player's homes");
    	if (player.hasPermission("teleport.otherhome")) player.sendMessage(Language.getString("plugin.title") + "[/home player <player> <home name>] " + ChatColor.WHITE + "teleports to someone else's homes");
    }
}