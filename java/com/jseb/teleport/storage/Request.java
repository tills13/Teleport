/* 
 *
 * 
 *
 */ 

package com.jseb.teleport.storage;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.Language;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Request {
	public static Teleport plugin;
	public static Map<String, List<Request>> requests;
	public long requestTime;
	public Player requester;
	public Player target;
	public Object destination;

	public Request(Player player, Object destination) {
		this.requestTime = System.currentTimeMillis();
		this.requester = player;
		this.destination = destination;
		if (this.destination instanceof Player) this.target = (Player) this.destination;
		else this.target = requester.getServer().getPlayer(((Home) this.destination).getOwner());

		if (requests == null) requests = new HashMap<String, List<Request>>();
		List<Request> requestlist = requests.get(target.getName());
		if (requestlist == null) requestlist = new ArrayList<Request>();

		requestlist.add(this);
		requests.put(target.getName(), requestlist);
		notifyPlayers();
	}

	public void notifyPlayers() {
		requester.sendMessage(Language.getString("plugin.title") + Language.getString("general.waitforauth"));
		String targetLocation = destination instanceof Home ? ((Home) destination).getName() : Language.getString("requests.yourlocation");
		target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.teleport.request"), requester.getName(), targetLocation));
		target.sendMessage(Language.getString("plugin.title") + Language.getString("general.teleport.help"));
	}

	public void accept() {
		if (this.destination instanceof Player) {
			plugin.getStorage().back.put(requester, requester.getLocation());
			requester.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.player.player"), target.getName()));
			target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.player.target"), requester.getName()));

			requester.teleport(target.getLocation());
		} else {
			Home home = (Home) this.destination;
			
			plugin.getStorage().back.put(requester, requester.getLocation());
			requester.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.player"), target.getName(), home.getName()));
			target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.owner"), requester.getName(), home.getName()));
	
			Location location = home.getLocation();
			TeleportHelper.loadChunkAt(location);
			requester.teleport(location);
		}

		removeRequest();
	}

	public void deny() {
		requester.sendMessage(Language.getString("plugin.title") + Language.getString("teleport.request.denied.player"));
		target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.request.denied.target"), requester.getName()));

		removeRequest();
	}

	public Player getRequester() {
		return this.requester;
	}

	public Player getTarget() {
		return this.target;
	}

	public Object getDestination() {
		return this.destination;
	}

	public void removeRequest() {
		List<Request> list = requests.get(this.target.getName());
		list.remove(this);
	}

	//STATIC METHODS

	public static void setPlugin(Teleport teleport) {
		plugin = teleport;
	}

	public static List<Request> getRequests(Player target) {
		return requests == null ? null : requests.get(target.getName());
	}

	public static int numRequests(Player target) {
		return getRequests(target) == null ? 0 : getRequests(target).size();
	}

	public static Request getRequest(Player target) {
		if ((numRequests(target) > 1) || (numRequests(target) == 0) || (requests == null)) {
			return null;
		} else {
			return requests.get(target.getName()).get(0);
		}
	}

	public static Request getRequest(Player target, String mrequester) {
		if (mrequester.equalsIgnoreCase("")) return getRequest(target);
		if (numRequests(target) == 0 || requests == null) {
			return null;
		} else {
			List<Request> requestList = requests.get(target.getName());
			for (Request request : requestList) {
				if (request.requester.getName().equalsIgnoreCase(mrequester)) {
					return request;
				}
			}
		}
		return null;
	}
}