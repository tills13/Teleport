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
            sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
            return true;
        }

        Player player;

        if (args.length == 0) helpSyntax(sender);
        else {
            if (args[0].equalsIgnoreCase("list")) {
                int page = 1, end;
                end = Area.numAreas();

                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {

                    }

                    if (page > Math.ceil(Area.numAreas() / 5.0)) page = (int)Math.ceil(Area.numAreas() / 5.0);
                    else if (page <= 0) page = 1;
                }

                if (Area.numAreas() == 0) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                    return true;
                }

                sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.list.title"), page, (int)Math.ceil(Area.numAreas() / 5.0)));
                Area[] areas = (Area[]) Area.getAreaList().toArray();

                int j = 0;
                for (int i = ((page - 1) * 5); i < end; i++) {
                    if (((i % 5) == 0) && (j++ != 0)) {
                        break;
                    }

                    Area area = areas[i];
                    String message = "   " + ChatColor.GREEN + (i + 1) + ". " + ChatColor.WHITE + area.getName();
                    if (!area.getAlias().equals("")) message += " [" + area.getAlias() + "] ";
                    if (area.canTeleportTo(sender)) message += " (" + (int)area.getLocation().getX() + ", " + (int)area.getLocation().getY() + ", " + (int)area.getLocation().getZ() + ")";
                    sender.sendMessage(message);
                }
            } else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("teleporta")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
                    return true;
                }

                player = (Player) sender;

                if (args.length == 1) {
                    player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/area teleport <name>]"));
                } else {
                    Area area;
                    if (args[0].equalsIgnoreCase("teleport")) area = Area.getArea(args[1]);
                    else area = Area.getAreaByAlias(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        player.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    }

                    plugin.getStorage().back.put(player, player.getLocation());
                    if (area.teleportTo(player)) {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.teleport"), area.getName()));
                    } else {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.permission"), area.getPermissionString()));
                    }
                    
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
                    return true;
                }

                player = (Player) sender;
                if (!player.hasPermission("teleport.area.set")) {
                    player.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/area set <name>]"));
                } else if (args.length == 2) {
                    Area area = Area.getArea(args[1]);
                    
                    if (area != null) {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.update.location"), args[1]));
                        area.setLocation(player.getLocation());
                    } else {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.addlocation"), args[1]));
                        new Area(args[1], player.getLocation(), player.getName());
                    }
                } else if (args.length == 3) {
                    Area area = Area.getArea(args[1]);
                    
                    if (area != null) {
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.update.locationandpermission"), args[1]));
                        area.setLocation(player.getLocation());
                        area.setPermissions(Boolean.parseBoolean(args[2]));
                    } else {
                        area = new Area(args[1], player.getLocation(), player.getName(), Boolean.parseBoolean(args[2]));
                        player.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.addlocationandpermission"), args[1], area.getPermissionString()));
                    }
                }
            } else if (args[0].equalsIgnoreCase("setperms")) {
                if (!sender.hasPermission("teleport.area.setperms")) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
                    return true;
                }

                if (args.length == 3) {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    }

                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.update.permission"), area.getName()));
                    area.setPermissions(Boolean.parseBoolean(args[2]));
                } else {
                    helpSyntax(sender);
                }
            } else if (args[0].equalsIgnoreCase("setalias")) {
                if (!sender.hasPermission("teleport.area.set")) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
                    return true;
                }

                if (args.length == 3) {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    }

                    if (area.setAlias(args[2])) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.update.alias"), area.getName()));
                    } else {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.aliasinuse"));
                    }
                } else {
                    helpSyntax(sender);
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("teleport.area.remove")) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
                    return true;
                }

                if (args.length == 1) {
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/area remove <name>]"));
                } else {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    } 

                    area.delete();
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.remove"), args[1]));
                }
            } else if (args[0].equalsIgnoreCase("rename")) {
                if (!sender.hasPermission("teleport.area.rename")) {
                    sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.syntax"), "[/area rename <name> <new name>]"));
                } else {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    } 

                    area.rename(args[2]);
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.rename"), args[1], args[2]));
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                if (args.length == 1) {
                    // closest area
                } else if (args.length == 2) {
                    Area area = Area.getArea(args[1]);

                    if (area == null && Area.numAreas() != 0) {
                        sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("error.area.nosucharea"), args[1]));
                        return true;
                    } else if (Area.numAreas() == 0) {
                        sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.area.noareassaved"));
                        return true;
                    } 

                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.info.about"), area.getName()));
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.info.alias"), area.getAlias()));
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.info.location"), (area.canTeleportTo(sender) ? area.getLocationString() : Language.getString("area.info.protected"))));
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.info.author"), area.getAuthor()));
                    sender.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("area.info.permission"), area.getPermissionString()));
                } else {

                }
            } else {
                helpSyntax(sender);
            }
        }
        return true;
    }   

    public void helpSyntax(CommandSender player) {
        player.sendMessage(Language.getString("plugin.title") + "[/area] " + Language.getString("general.commandhelp.title"));
        if (player.hasPermission("teleport.area.set")) player.sendMessage(Language.getString("plugin.title") + "[/area set <name> <bool>] " + ChatColor.WHITE + Language.getString("area.help.set"));
        if (player.hasPermission("teleport.area.set")) player.sendMessage(Language.getString("plugin.title") + "[/area setalias <name> <alias>] " + ChatColor.WHITE + Language.getString("area.help.setalias"));
        if (player.hasPermission("teleport.area.setperms")) player.sendMessage(Language.getString("plugin.title") + "[/area setperms <name> <bool>] " + ChatColor.WHITE + Language.getString("area.help.setperms"));
        if (player.hasPermission("teleport.area.remove")) player.sendMessage(Language.getString("plugin.title") + "[/area remove <name>] " + ChatColor.WHITE + Language.getString("area.help.remove"));
        if (player.hasPermission("teleport.area.rename")) player.sendMessage(Language.getString("plugin.title") + "[/area rename <name> <new name>] " + ChatColor.WHITE + Language.getString("area.help.rename"));
        if (player.hasPermission("teleport.area.teleport")) player.sendMessage(Language.getString("plugin.title") + "[/area teleport <name>] " + ChatColor.WHITE + Language.getString("area.help.teleport"));
        if (player.hasPermission("teleport.area.teleport")) player.sendMessage(Language.getString("plugin.title") + "[/area teleporta <alias>] " + ChatColor.WHITE + Language.getString("area.help.teleporta"));
        player.sendMessage(Language.getString("plugin.title") + "[/area list <page num>] " + ChatColor.WHITE + Language.getString("area.help.list"));
        player.sendMessage(Language.getString("plugin.title") + "[/home] and [/teleport] " + ChatColor.WHITE + Language.getString("teleport.help.general"));
    }
}