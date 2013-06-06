package com.jseb.teleport;

import org.bukkit.scheduler.BukkitRunnable;
 	
import java.net.HttpURLConnection;
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
		this.enabled = plugin.getSettings().updateEnabled;
	}

	@Override
	public void run() {
		if (enabled) {
			checkForUpdates();
		}
	}

	public void checkForUpdates() {
		if (getPluginVersion() < getRemoteVersion()) {
			this.notify = true;
			return;
		} 

		this.notify = false;
	}

	public double getPluginVersion() {
		return Double.parseDouble(this.plugin.getDescription().getVersion());
	}

	public double getRemoteVersion() {
		try {
			URLConnection connection;
			URL url = new URL("http://dev.bukkit.org/server-mods/" + plugin.getSettings().projectName + "/");
			connection = url.openConnection();
			InputStream is = connection.getInputStream();
			Scanner in = new Scanner(is);
			boolean read = false;
			Pattern pattern = Pattern.compile("v(\\d\\.\\d)");
			Matcher matcher;
			
			while(in.hasNext()) {
				String s = in.next();
				if (s.contains("file-type")) {
					read = true;
				}
				if ((s.contains("/server-mods/teleport-home/files/") && read)) {
					s = in.next();
					matcher = pattern.matcher(s);
					matcher.find();
					try {
						return Double.parseDouble(matcher.group(1));
					} catch (IllegalStateException e) {
						System.out.println("[Teleport] something went wrong checking for updates :(");
					}
				}
			}
		} catch (IOException e) {
			System.out.println("[Teleport] something went wrong checking for updates :(");
		}

		return 0;
	}

	public boolean getNotify() {
		return this.notify;
	}
}