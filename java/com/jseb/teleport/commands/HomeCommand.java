package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.storage.Request;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.Language;
import com.jseb.teleport.Config;

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
    	if (!Config.getBoolean("components.homeenabled")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
        else {
        	Player player;

	    	if (args.length == 0) helpSyntax(sender);
			else {
				if (args[0].equalsIgnoreCase("set")) {
					if (!sender.hasPermission("teleport.sethome")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
			    		player = (Player) sender;

						if (args.length < 2) player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home set <name> <default>]"));
						else {
							if (Home.numHomes(player.getName().toLowerCase()) <= Config.getInt("general.maxhomes")) {
								Home home = Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

								if (home == null) {
									player.sendMessage(Language.getString("plugin.title") + Language.getString("home.location.saved"));
									home = Home.newHome(player.getName().toLowerCase(), args[1].toLowerCase(), player.getLocation(), (args.length == 3) ? args[2].equalsIgnoreCase("default") ? true : false : false);
								} else {
									home.setLocation(player.getLocation());
									home.setIsDefault((args.length == 3) ? args[2].equalsIgnoreCase("default") ? true : home.getIsDefault() : home.getIsDefault());
									player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.location.update"), args[1]));
								}

								if (home.getIsDefault()) player.sendMessage(Language.getString("plugin.title") + Language.getString("home.notify.isdefault"));
							} else player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.maxhomes"), Config.getInt("general.maxhomes")));
						}
			    	}
				} else if (args[0].equalsIgnoreCase("setdefault")) {
					if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
			    		player = (Player) sender;

						if (args.length != 2) player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home setdefault <home name>"));
						else {
							Home home = Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

							if (home == null && Home.numHomes(player.getName().toLowerCase()) != 0) player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
							else if (Home.numHomes(player.getName().toLowerCase()) == 0) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
							else {
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.newdefault"), home.getName()));
								home.setIsDefault(true);
							}
						}
			    	}
				} else if (args[0].equalsIgnoreCase("addresident")) {
					if (!sender.hasPermission("teleport.sethome")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else if (args.length < 2) helpSyntax(sender);
			    	else {
			    		player = (Player) sender;

				    	if (Home.numHomes(player.getName().toLowerCase()) > 0) {
				    		Home home = (args.length == 2) ? Home.getDefaultHome(player.getName().toLowerCase()) : Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

							if (home != null) {
								home.addResident(args[(args.length == 2) ? 1 : 2].toLowerCase());
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.residentadded"), args[(args.length == 2) ? 1 : 2]));

								Player residentadded = plugin.getServer().getPlayerExact(args[(args.length == 2) ? 1 : 2]);
								if (residentadded != null) residentadded.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.residentaddednotice"), sender.getName(), home.getName()));
							} else player.sendMessage(Language.getString("plugin.title") + ((args.length == 3) ? String.format(Language.getString("error.home.nosuchhome"), args[1]) : Language.getString("error.home.nodefaulthome")));
						} else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
			    	}
				} else if (args[0].equalsIgnoreCase("removeresident")) {
					if (!sender.hasPermission("teleport.sethome")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else if (args.length < 2) helpSyntax(sender);
			    	else {
			    		player = (Player) sender;

				    	if (Home.numHomes(player.getName().toLowerCase()) > 0) {
				    		Home home = (args.length == 2) ? Home.getDefaultHome(player.getName().toLowerCase()) : Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

							if (home != null) {
								home.removeResident(args[(args.length == 2) ? 1 : 2].toLowerCase());
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.residentremoved"), args[(args.length == 2) ? 1 : 2]));

								Player residentadded = plugin.getServer().getPlayerExact(args[(args.length == 2) ? 1 : 2]);
								if (residentadded != null) residentadded.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.residentremovednotice"), sender.getName(), home.getName()));
							} else player.sendMessage(Language.getString("plugin.title") + ((args.length == 3) ? String.format(Language.getString("error.home.nosuchhome"), args[1]) : Language.getString("error.home.nodefaulthome")));
						} else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
			    	}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (!sender.hasPermission("teleport.sethome")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
			    		player = (Player) sender;

				    	if (Home.numHomes(player.getName().toLowerCase()) > 0) {
				    		Home home = (args.length == 1) ? Home.getDefaultHome(player.getName().toLowerCase()) : Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

							if (home != null) {
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.remove"), home.getName()));
								home.delete();
							} else player.sendMessage(Language.getString("plugin.title") + ((args.length == 2) ? String.format(Language.getString("error.home.nosuchhome"), args[1]) : Language.getString("error.home.nodefaulthome")));
						} else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
			    	}
				} else if (args[0].equalsIgnoreCase("teleport")) {
					if (!sender.hasPermission("teleport.teleport")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
			    		player = (Player) sender;
			    		Home home = (args.length == 1) ? Home.getDefaultHome(player.getName().toLowerCase()) : Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

			    		if (home == null && Home.numHomes(player.getName().toLowerCase()) != 0) {
			    			if (args.length == 1) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nodefaulthome"));
			    			else player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
						} else if (Home.numHomes(player.getName().toLowerCase()) == 0) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
						else {
							player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.teleport"), home.getName()));
							home.teleportTo(player);
						}
			    	}
				} else if (args[0].equalsIgnoreCase("rename")) {
					if (!(sender instanceof Player)) {
			    		sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    		return true;
			    	}

			    	player = (Player) sender;

					if (args.length != 3) player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/home rename <current name> <new name>"));
					else {
						if (Home.numHomes(player.getName().toLowerCase()) > 0) {
							Home home = Home.getHome(player.getName().toLowerCase(), args[1].toLowerCase());

							if (home != null) {
								if (Home.getHome(player.getName().toLowerCase(), args[2]) == null) {
									home.setName(args[2].toLowerCase());
									player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.rename"), args[1], args[2]));
								} else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.alreadyexists"));
							} else player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nosuchhome"), args[1]));
						} else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.home.nohomessaved"));
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					if (sender instanceof Player && !((Player) sender).hasPermission("teleport.list")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
					else if (!(sender instanceof Player) && args.length == 1) sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.list.help"), "[/home list <player name>]"));
					else {
						String target = (args.length == 1) ? ((Player) sender).getName().toLowerCase() : args[1].toLowerCase();
						List<Home> homeList = Home.getHomeList(target);

						if (homeList.size() != 0) sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.list.title.other"), target));
						else sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.home.nohomessaved.other"), target));

						int i = 1;
						for (Home home : homeList) {
							String message = "    " + ChatColor.GREEN + i++ + ". " + ChatColor.WHITE + home.getName();
							if (home.getIsDefault()) message += " " + Language.getString("home.list.isdefault");
							message += " (" + (int)home.getLocation().getX() + ", " + (int)home.getLocation().getY() + ", " + (int)home.getLocation().getZ() + ")";
							sender.sendMessage(message);
						}
					}
				} else if (args[0].equalsIgnoreCase("player")) {
					if (!sender.hasPermission("teleport.otherhome")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
			    	else if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
			    	else {
						if ((args.length == 2) || (args.length == 3)) {
							Player receiver = plugin.getServer().getPlayer(args[1]);
							player = (Player) sender;

							Home home = (args.length == 2) ? Home.getDefaultHome(((receiver != null) ? receiver.getName() : args[1]).toLowerCase()) : Home.getHome(((receiver != null) ? receiver.getName() : args[1]).toLowerCase(), args[2].toLowerCase());						System.out.println(home);
							if (home != null && home.canTeleportTo(player.getName().toLowerCase())) {
								Storage.saveBackLocation(player, player.getLocation());
								player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.player"), home.getOwner(), home.getName()));
								if (receiver != null) receiver.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.owner"), player.getName(), home.getName()));
						
								home.teleportTo(player);
							} else {
								if (home == null || receiver == null) {
									if (receiver == null) player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.playernotfound"), args[1]));
									if (receiver == null) player.sendMessage(Language.getString("plugin.title") + Language.getString("error.playermustbeonline"));
									if (home == null) player.sendMessage(String.format(Language.getString("plugin.title") + Language.getString("error.home.nosuchhome.other"), args[1], args[2]));
								} else Request.makeRequest(player.getName(), home);
							}
						}
			    	}
				} else if (args[0].equalsIgnoreCase("info")) {
					if (args.length == 1 && (sender instanceof Player)) {
						Home closestHome = null;
						double distance = Double.MAX_VALUE;

	                    for (Home home : Home.getHomeList(((Player) sender).getName())) {
	                        if (closestHome == null) {
	                            closestHome = home;
	                            distance = ((Player) sender).getLocation().distance(home.getLocation());
	                        } else {
	                            if (((Player) sender).getLocation().distance(home.getLocation()) < distance) {
	                                closestHome = home;
	                                distance = ((Player) sender).getLocation().distance(home.getLocation());
	                            }
	                        }

	                        
		                    if (Home.numHomes(((Player) sender).getName()) == 0) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.nohomessaved"));
		                    else {
		                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.about"), home.getName()));
		                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.owner"), home.getOwner()));
		                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.location"), (sender instanceof Player ? (home.canTeleportTo(((Player)sender).getName().toLowerCase()) ? home.getLocationString() : Language.getString("home.info.protected")) : home.getLocationString())));
		                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.residents"), TeleportHelper.listToString(home.getResidents())));
		                    }
	                    }
	                } else {
	                    Home home = (args.length == 2) ? Home.getHome(sender.getName().toLowerCase(), args[1].toLowerCase()) : Home.getHome(args[1].toLowerCase(), args[2].toLowerCase());

	                    if (home == null && Home.numHomes((args.length == 2) ? sender.getName().toLowerCase() : args[1].toLowerCase()) != 0) sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosuchhome"), args[(args.length == 2) ? 2 : 1]));
	                    else if (Home.numHomes((args.length == 2) ? sender.getName().toLowerCase() : args[1].toLowerCase()) == 0) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.nohomessaved"));
	                    else {
	                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.about"), home.getName()));
	                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.owner"), home.getOwner()));
	                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.location"), (sender instanceof Player ? (home.canTeleportTo(((Player)sender).getName().toLowerCase()) ? home.getLocationString() : Language.getString("home.info.protected")) : home.getLocationString())));
	                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("home.info.residents"), TeleportHelper.listToString(home.getResidents())));
	                    }
	                }
				} else {
					sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.unknownargument"), args[0]));
					helpSyntax(sender);			
				}
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