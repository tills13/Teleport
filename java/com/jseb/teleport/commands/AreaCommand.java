package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.Language;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.storage.Area;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaCommand implements CommandExecutor {
    Teleport plugin;


    public AreaCommand(Teleport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!plugin.getSettings().areaEnabled) {
            sender.sendMessage(Language.getString("plugin.title") + "this feature has been disabled");
            return true;
        }

        Player player;

        if (args.length == 0) {
           helpSyntax(sender);
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                int page = 1, end;
                end = Area.numAreas();

                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {

                    }

                    if (page > Math.ceil(Area.numAreas() / 5.0)) {
                        page = (int)Math.ceil(Area.numAreas() / 5.0);
                    }
                }

                if (Area.numAreas() == 0) {
                    sender.sendMessage(Language.getString("plugin.title") + "no areas saved");
                    return true;
                }

                sender.sendMessage(Language.getString("plugin.title") + "Areas: [Page " + page + " of " + (int)Math.ceil(Area.numAreas() / 5.0) + "]");

                int j = 0;
                for (int i = ((page - 1) * 5); i < end; i++) {
                    if (((i % 5) == 0) && (j++ != 0)) {
                        break;
                    }

                    Area area = (Area)Area.toArray()[i];
                    String message = "   " + ChatColor.GREEN + (i + 1) + ". " + ChatColor.WHITE + area.getName();
                    if (area.canTeleportTo(sender)) message += " (" + (int)area.getLocation().getX() + ", " + (int)area.getLocation().getY() + ", " + (int)area.getLocation().getZ() + ")";
                    sender.sendMessage(message);
                }
            } else if (args[0].equalsIgnoreCase("teleport")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can execute this command.");
                    return true;
                }

                player = (Player) sender;

                if (args.length == 1) {
                    player.sendMessage(Language.getString("plugin.title") + "syntax: [/area teleport <name>]");
                } else {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        player.sendMessage(Language.getString("plugin.title") + "no area named " + args[1]);
                        return true;
                    } else if (Area.numAreas() == 0) {
                        player.sendMessage(Language.getString("plugin.title") + "no areas saved");
                        return true;
                    }

                    plugin.getStorage().back.put(player, player.getLocation());
                    if (area.teleportTo(player)) {
                        player.sendMessage(Language.getString("plugin.title") + "teleporting to " + area.getName());
                    } else {
                        player.sendMessage(Language.getString("plugin.title") + "you do not have the required permission: " + area.getPermissionString());
                    }
                    
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can execute this command.");
                    return true;
                }

                player = (Player) sender;
                if (!player.hasPermission("teleport.area.set")) {
                    player.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(Language.getString("plugin.title") + "syntax: [/area set <name>]");
                } else if (args.length == 2) {
                    Area area = Area.getArea(args[1]);
                    
                    if (area != null) {
                        player.sendMessage(Language.getString("plugin.title") + "updated " + args[1] + "\'s location");
                        area.setLocation(player.getLocation());
                    } else {
                        player.sendMessage(Language.getString("plugin.title") + "added area: " + ChatColor.WHITE + args[1]);
                        new Area(args[1], player.getLocation(), player.getName());
                    }
                } else if (args.length == 3) {
                    Area area = Area.getArea(args[1]);
                    
                    if (area != null) {
                        player.sendMessage(Language.getString("plugin.title") + "updated " + args[1] + "\'s location, permissions");
                        area.setLocation(player.getLocation());
                        area.setPermissions(Boolean.parseBoolean(args[2]));
                    } else {
                        area = new Area(args[1], player.getLocation(), player.getName(), Boolean.parseBoolean(args[2]));
                        player.sendMessage(Language.getString("plugin.title") + "added area: " + ChatColor.WHITE + args[1] + ChatColor.GREEN + " with permission " + ChatColor.WHITE + area.getPermissionString());
                    }
                }
            } else if (args[0].equalsIgnoreCase("setperms")) {
                if (!sender.hasPermission("teleport.area.setperms")) {
                    sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
                    return true;
                }

                if (args.length == 3) {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no area named " + args[1]);
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no areas saved");
                        return true;
                    }

                    sender.sendMessage(Language.getString("plugin.title") + "updated permissions for " + area.getName());
                    area.setPermissions(Boolean.parseBoolean(args[2]));
                } else {
                    helpSyntax(sender);
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("teleport.area.remove")) {
                    sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
                    return true;
                }

                if (args.length == 1) {
                    sender.sendMessage(Language.getString("plugin.title") + "syntax: [/area remove <name>]");
                } else {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no area named " + args[1]);
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no areas saved");
                        return true;
                    } 

                    area.delete();
                    sender.sendMessage(Language.getString("plugin.title") + "removed " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("rename")) {
                if (!sender.hasPermission("teleport.area.rename")) {
                    sender.sendMessage(Language.getString("plugin.title") + "you do not have the required permission.");
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(Language.getString("plugin.title") + "syntax: [/area rename <name> <new name>]");
                } else {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no area named " + args[1]);
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no areas saved");
                        return true;
                    } 

                    area.rename(args[2]);
                    sender.sendMessage(Language.getString("plugin.title") + args[1] + " renamed to " + args[2]);
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                if (args.length == 1) {
                    // closest area
                } else if (args.length == 2) {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no area named " + args[1]);
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + "no areas saved");
                        return true;
                    } 

                    sender.sendMessage(Language.getString("plugin.title") + "about: " + ChatColor.WHITE + area.getName());
                    sender.sendMessage(Language.getString("plugin.title") + "location: " + (area.canTeleportTo(sender) : ChatColor.WHITE + area.getLocationString() ? "(protected)"));
                    sender.sendMessage(Language.getString("plugin.title") + "author: " + ChatColor.WHITE + area.getAuthor());
                    sender.sendMessage(Language.getString("plugin.title") + "permission: " + ChatColor.WHITE + area.getPermissionString());
                } else {

                }
            } else {
                helpSyntax(sender);
            }
        }
        return true;
    }   

    public void helpSyntax(CommandSender player) {
        player.sendMessage(Language.getString("plugin.title") + "[/area] " + ChatColor.WHITE + "commands syntax: ");
        if (player.hasPermission("teleport.area.set")) player.sendMessage(Language.getString("plugin.title") + "[/area set <name> <bool>] " + ChatColor.WHITE + "sets a new area, optional permission requirement");
        if (player.hasPermission("teleport.area.setperms")) player.sendMessage(Language.getString("plugin.title") + "[/area setperms <name> <bool>] " + ChatColor.WHITE + "sets area permissions");
        if (player.hasPermission("teleport.area.remove")) player.sendMessage(Language.getString("plugin.title") + "[/area remove <name>] " + ChatColor.WHITE + "removes an area");
        if (player.hasPermission("teleport.area.rename")) player.sendMessage(Language.getString("plugin.title") + "[/area rename <name> <new name>] " + ChatColor.WHITE + "renames an area to <new name>");
        if (player.hasPermission("teleport.area.teleport")) player.sendMessage(Language.getString("plugin.title") + "[/area teleport <name>] " + ChatColor.WHITE + "teleports to an area");
        player.sendMessage(Language.getString("plugin.title") + "[/area list <name>] " + ChatColor.WHITE + "lists areas");
    }
}