package com.jseb.teleport.commands;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.Language;
import com.jseb.teleport.Config;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BedCommand implements CommandExecutor {
	Teleport plugin;

	public BedCommand(Teleport plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!Config.getBoolean("components.bedenabled")) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.featuredisabled"));
        else if (!(sender instanceof Player) || !((Player) sender).hasPermission("teleport.bed")) {
            if (!(sender instanceof Player)) sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.playersonly"));
            else sender.sendMessage(Language.getString("plugin.title") + Language.getString("error.permissiondenied"));
        } else {
            Player player = (Player) sender;
            Location location = player.getBedSpawnLocation();
            
            if (location != null) {
                player.teleport(location);
              	player.sendMessage(Language.getString("plugin.title") + Language.getString("bed.teleport"));
            } else player.sendMessage(Language.getString("plugin.title") + Language.getString("error.bed.bednotfound"));
        }

        return true;
    }
}