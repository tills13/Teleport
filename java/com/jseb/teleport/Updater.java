package com.jseb.teleport;

import com.jseb.teleport.Language;

import org.bukkit.scheduler.BukkitRunnable;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

	
public class Updater extends BukkitRunnable {
	Teleport plugin;
	public boolean notify;
	public boolean enabled; 

	public Updater(Teleport plugin) {
		this.plugin = plugin;
		this.notify = false;
		this.enabled = Config.getBoolean("updateenabled");
	}

	@Override
	public void run() {
		if (enabled) checkForUpdates();
	}

	public void checkForUpdates() {
		if (getLocalVersion() < getRemoteVersion()) this.notify = true;
		else this.notify = false;
	}

	public double getLocalVersion() {
		return Double.parseDouble(this.plugin.getDescription().getVersion());
	}

	public double getRemoteVersion() {
		Pattern pattern = Pattern.compile("v(\\d\\.\\d)");
		boolean read = false;

		try {
			URLConnection connection = new URL("http://dev.bukkit.org/server-mods/" + Config.getString("projectname") + "/").openConnection();
			InputStream is = connection.getInputStream();
			Scanner in = new Scanner(is);
			
			while(in.hasNext()) {
				String s = in.next();
				if (s.contains("file-type")) read = true;
				if ((s.contains("/server-mods/teleport-home/files/") && read)) {
					s = in.next();
					Matcher matcher = pattern.matcher(s);
					matcher.find();
					
					return Double.parseDouble(matcher.group(1));
				}
			}
		} catch (IOException e) {
			System.out.println("[Teleport] " + Language.getString("error.updater.check"));
		} catch (IllegalStateException e) {
			
		}

		return 0;
	}

	public boolean getNotify() {
		return this.notify;
	}
}