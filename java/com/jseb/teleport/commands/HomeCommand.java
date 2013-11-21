package com.jseb.teleport.commands;

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
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

    	Player player;

    	if (args.length == 0) {
			//send to syntax help
			helpSyntax(sender);
		} else {
			if (args[0].equalsIgnoreCase("set")) {
				if (!sender.hasPermission("teleport.sethome")) {
					sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length < 2) {
					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home set <name> <default>]"));
					return true;
				} else {
					if (Home.numHomes(player) < plugin.getSettings().maxHomes) {
						if (args.length == 3) {
							if (args[2].equalsIgnoreCase("default")) {
								new Home(player.getName(), args[1], player.getLocation(), true);
								player.sendMessage(Language.getString("plugin.title") + Language.getString("home.default.saved"));
							} else {
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.unknownargument"),args[2]));
							}
						} else {
							Home home = Home.getHome(player, args[1]);
							if (home != null) {
								home.setLocation(player.getLocation());
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.location.update"), args[1]));
							} else {
								new Home(player.getName(), args[1], player.getLocation());
								player.sendMessage(Language.getString("plugin.title") + Language.getString("home.location.saved"));
							}
						}
					} else {
						player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.maxhomes"), plugin.getSettings().maxHomes));
					}
				}
			} else if (args[0].equalsIgnoreCase("setdefault")) {
				if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length != 2) {
					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home setdefault <home name>"));
					return true;
				}

				Home home = Home.getHome(player, args[1]);

				if (home == null && Home.numHomes(player) != 0) {
					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
					return true;
				} else if (Home.numHomes(player) == 0) {
					player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
					return true;
				}

				player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.newdefault"), home.getName()));
				home.makeDefault();
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("teleport.sethome")) {
					sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

		    	if (Home.numHomes(player) > 0) {
		    		Home home = null;
		    		if (args.length == 1) home = Home.getDefault(player);
		    		else if (args.length == 2) home = Home.getHome(player, args[1]);

					if (home != null) {
						home.delete();
						player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.remove"), args[1]));
					} else {
						player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
					}
				} else {
					player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
				}	
			} else if (args[0].equalsIgnoreCase("teleport")) {
				if (!sender.hasPermission("teleport.teleport")) {
					sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length == 1) {
					Home home = Home.getDefault(player);

					if (home == null && Home.numHomes(player) != 0) {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nodefaulthome"));
						return true;
					} else if (Home.numHomes(player) == 0) {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
						return true;
					}

					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.teleport"), home.getName()));
					plugin.getStorage().back.put(player, player.getLocation());
					home.teleportTo(player);
				} else {
					Home home = Home.getHome(player, args[1]);

					if (home == null && Home.numHomes(player) != 0) {
						player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
						return true;
					} else if (Home.numHomes(player) == 0) {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
						return true;
					}

					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.teleport"), home.getName()));
					plugin.getStorage().back.put(player, player.getLocation());
					home.teleportTo(player);
				}
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

				if (args.length != 3) {
					player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home rename <current name> <new name>"));
				} else {
					if (Home.numHomes(player) > 0) {
						Home home = Home.getHome(player, args[1]);

						if (home != null) {
							home.rename(args[2]);
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.rename"), args[1], args[2]));
						} else {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
						}
					} else {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
					}
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length == 1) {
					if (!(sender instanceof Player)) {
			    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    		sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.list.help"), "[/home list <player name>]"));
			    		return true;
			    	}

			    	player = (Player) sender;

					List<Home> list = Home.getHomes(player.getName());

					if (list == null) {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
						return true;
					} else {
						player.sendMessage(Language.getString("plugin.title") + Language.getString("home.list.title.self"));

						int i = 1;
						for (Home home: list) {
							String message = "   " + ChatColor.GREEN + i++ + ". " + ChatColor.WHITE + home.getName();
							if (home.getIsDefault()) message += " " + Language.getString("home.list.isdefault");
							message += " (" + (int)home.getLocation().getX() + ", " + (int)home.getLocation().getY() + ", " + (int)home.getLocation().getZ() + ")";
							player.sendMessage(message);
						}
					}
				} else if (args.length == 2) {
					if (!sender.hasPermission("teleport.list")) {
						sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
						return true;
			    	}

	    			Player p = plugin.getServer().getPlayer(args[1].toLowerCase());
	    			List<Home> list;

	    			if (p == null) {
	    				list = Home.getHomes(args[1].toLowerCase());
	    				if (list == null) {
	    					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nohomessaved.other"), args[1]));
	    					return true;
	    				}
	    			} else {
	    				list = Home.getHomes(p.getName().toLowerCase());
	    				if (list == null) {
	    					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nohomessaved.other"), p.getName()));
	    					return true;
	    				}
	    				args[1] = p.getName().toLowerCase();
	    			}

					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.list.title.other"), args[1]));

					int i = 1;
					for (Home home: list) {
						String message = "   " + ChatColor.GREEN + i++ + ". " + ChatColor.WHITE + home.getName();
						if (home.getIsDefault()) message += " " + Language.getString("home.list.isdefault");
						message += " (" + (int)home.getLocation().getX() + ", " + (int)home.getLocation().getY() + ", " + (int)home.getLocation().getZ() + ")";
						sender.sendMessage(message);
					}
				}
			} else if (args[0].equalsIgnoreCase("player")) {
				if (!sender.hasPermission("teleport.otherhome")) {
					sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					return true;
		    	} else if (!(sender instanceof Player)) {
		    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
		    		return true;
		    	}

		    	player = (Player) sender;

				if ((args.length < 2) || (args.length > 3)) {
					//invalid syntax
				} else {
					Player receiver = plugin.getServer().getPlayer(args[1]);

					if (receiver == null) {
						player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.playernotfound"), args[1]));
						player.sendMessage(Language.getString("plugin.title") + Language.getString("error.playermustbeonline"));
						return true; 
					}

					
					Home home;
					if (args.length == 2) {
						home = Home.getDefault(receiver);

						if (home == null && Home.numHomes(receiver) != 0) {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nodefaulthome.other"), receiver.getName()));
							return true;
						} else if (Home.numHomes(receiver) == 0) {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nohomessaved.other"), receiver.getName()));
							return true;
						}
					} else {
						home = Home.getHome(receiver, args[2]);

						if (home == null && Home.numHomes(receiver) != 0) {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome.other"), receiver.getName(), args[2]));
							return true;
						} else if (Home.numHomes(receiver) == 0) {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nohomessaved.other"), receiver.getName()));
							return true;
						}
					}

					new Request(receiver, home);
				}
			} else {
				sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.unknownargument"), args[0]));
				//send to syntax help
				helpSyntax(sender);
				return true;
			}
		}
	   
		return true;
    }

    public void helpSyntax(CommandSender player) {
    	player.sendMessage(Language.getString("plugin.title") + "[/home] " + Language.getString("general.commandhelp.title"));
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home set <name> <default>] " + ChatColor.WHITE + Language.getString("home.help.set"));
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home setdefault <name>] " + ChatColor.WHITE + Language.getString("home.help.setdefault"));
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home remove <name>] " + ChatColor.WHITE + Language.getString("home.help.remove"));
    	if (player.hasPermission("teleport.sethome")) player.sendMessage(Language.getString("plugin.title") + "[/home rename <name> <new name>] " + ChatColor.WHITE + Language.getString("home.help.rename"));
    	if (player.hasPermission("teleport.teleport")) player.sendMessage(Language.getString("plugin.title") + "[/home teleport <name>] " + ChatColor.WHITE + Language.getString("home.help.teleport"));
    	player.sendMessage(Language.getString("plugin.title") + "[/home list <name>] " + ChatColor.WHITE + Language.getString("home.help.list"));
    	if (player.hasPermission("teleport.otherhome")) player.sendMessage(Language.getString("plugin.title") + "[/home player <player> <home name>] " + ChatColor.WHITE + Language.getString("home.help.player"));
    	player.sendMessage(Language.getString("plugin.title") + "[/teleport] and [/area] " + ChatColor.WHITE + Language.getString("teleport.help.general"));
    }
}