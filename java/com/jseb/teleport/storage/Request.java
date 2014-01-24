/* format: 
 * requests:
 * 		(id):
 * 			(attrs)
 * 
 *
 */ 

package com.jseb.teleport.storage;

import com.jseb.teleport.Teleport;
import com.jseb.teleport.TeleportHelper;
import com.jseb.teleport.storage.Home;
import com.jseb.teleport.storage.Storage;
import com.jseb.teleport.Language;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class Request {
	public static Teleport plugin;
	public String id;
	public String requester;
	public String type;
	public String target;
	public String target_owner;
	public Object destination;

	public Request(String id, String player, Object destination) {
		this.id = id;
		this.requester = player;
		this.destination = destination;
		this.type = (destination instanceof Home) ? "home" : "player";
		this.target = ((destination instanceof Home) ? ((Home) destination).getName() : ((Player) destination).getName());
		this.target_owner = (destination instanceof Home) ? ((Home) destination).getOwner() : "N/A";
	}

	public String getID() {
		return this.id;
	}

	public String getRequester() {
		return this.requester;
	}

	public String getType() {
		return this.type;
	}

	public String getTarget() {
		return this.target;
	}

	public String getTargetOwner() {
		return this.target_owner;
	}

	public void accept() {
		Player mRequester = Bukkit.getServer().getPlayerExact(this.requester);
		Player mTarget = Bukkit.getServer().getPlayerExact(this.type == "home" ? this.target_owner : this.target);

		if (mRequester != null) {
			Storage.saveBackLocation(mRequester, mRequester.getLocation());
			mRequester.sendMessage(Language.getString("plugin.title") + "teleporting..."); //CHANGE
			mTarget.sendMessage(Language.getString("plugin.title") + "teleporting " + this.requester + "..."); //CHANGE
			if (this.type == "home") Home.getHome(this.target_owner, this.target).teleportTo(mRequester);
			else mRequester.teleport(mTarget.getLocation());
		} else mTarget.sendMessage(Language.getString("plugin.title") + this.requester + " appears to have gone offline...");
		
		//requester.sendMessage( + String.format(Language.getString("teleport.player.player"), target.getName()));
		//target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.player.target"), requester.getName()));
	
		//requester.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.player"), target.getName(), home.getName()));
		//target.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.owner"), requester.getName(), home.getName()));

		removeRequest();
	}

	public void deny() {
		Player mRequester = Bukkit.getServer().getPlayerExact(this.requester);
		Player mTarget = Bukkit.getServer().getPlayerExact(this.type == "home" ? this.target_owner : this.target);

		if (mRequester != null) mRequester.sendMessage(Language.getString("plugin.title") + Language.getString("teleport.request.denied.player"));
		if (mTarget != null) mTarget.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.request.denied.target"), requester));

		removeRequest();
	}

	public void removeRequest() {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		requests.set("requests." + this.id, null);

		TeleportHelper.saveConfig("requests.yml", requests);
	}

	//STATIC METHODS
	public static Request makeRequest(String player, Object destination) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		YamlConfiguration requesthistory = TeleportHelper.getConfig("requesthistory.yml");

		long id =  System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()).toString();
		String path = "requests." + id + ".";

		requests.set(path + id + ".requester", player);
		requests.set(path + id + ".requested_at", time);
		requests.set(path + id + ".type", (destination instanceof Home) ? "home" : "player");
		requests.set(path + id + ".target", ((destination instanceof Home) ? ((Home) destination).getName() : ((Player) destination).getName()));
		requests.set(path + id + ".target_owner", ((destination instanceof Home) ? ((Home) destination).getOwner() : ((Player) destination).getName()));

		path = "history." + id;
		requesthistory.set(path + ".from", player);
		requesthistory.set(path + ".to", ((destination instanceof Home) ? "home" : "player") + " (" + ((destination instanceof Home) ? ((Home) destination).getName() : ((Player) destination).getName()) + ")");
		requesthistory.set(path + ".on", time);
		requesthistory.set(path + ".status", "active");

		TeleportHelper.saveConfig("requests.yml", requests);
		TeleportHelper.saveConfig("requesthistory.yml", requesthistory);
		Request request = getRequest(id);
		notifyPlayers(request);
		return request;
	}

	public static Request getRequest(long id) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		if (requests.contains("requests." + id)) {
			
			return new Request(id + "", requests.getString("requests." + id + ".requester"), (requests.getString("requests." + id + ".type").equals("home") ? Home.getHome(requests.getString("requests." + id + ".target_owner"), requests.getString("requests." + id + ".target")) : Bukkit.getPlayerExact(requests.getString("requests." + id + ".target"))));
		} else return null;
	}

	public static Request getRequest(String target) {
		if ((numRequests(target) > 1) || (numRequests(target) == 0)) return null;
		else return getRequests(target).get(0);
	}

	public static Request getRequest(String target, String requester) {
		if (requester.equals("")) return getRequest(target);
		else if (numRequests(target) == 0) return null;
		else {
			for (Request request : getRequests(target)) {
				if (request.getRequester().equalsIgnoreCase(requester)) return request;
			}
		}

		return null;
	}

	public static List<Request> getRequests(String target) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		Map<String, Object> requestList = requests.getValues(false);
		List<Request> targetRequests = new ArrayList<Request>();

		for (String request_id : requestList.keySet()) if (requests.getString(request_id + ".target_owner").equals(target)) targetRequests.add(getRequest(Long.parseLong(request_id)));
		return targetRequests;
	}

	public static int numRequests(String target) {
		return getRequests(target).size();
	}

	public static void notifyPlayers(Request request) {
		Player mRequester = Bukkit.getServer().getPlayerExact(request.requester);
		Player mTarget = Bukkit.getServer().getPlayerExact(request.type == "home" ? request.target_owner : request.target);

		if (mRequester != null) mRequester.sendMessage(Language.getString("plugin.title") + Language.getString("general.waitforauth"));
		if (mTarget != null) {
			String targetLocation = (request.destination instanceof Home) ? ((Home) request.destination).getName() : Language.getString("requests.yourlocation");

			mTarget.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("general.teleport.request"), mRequester.getName(), targetLocation));
			mTarget.sendMessage(Language.getString("plugin.title") + Language.getString("general.teleport.help"));
		}
	}
}