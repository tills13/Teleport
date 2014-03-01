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
	public static final String STATUS_ACTIVE = "active";
	public static final String STATUS_ACCEPTED = "accepted";
	public static final String STATUS_DENIED = "denied";
	public static final String STATUS_EXPIRED = "expired";
	public static final String STATUS_INVALID = "invalid";
	public String id;
	public String requester;
	public String type;
	public String target;
	public String target_owner;
	public Object destination;

	public Request(String id, String player, Object destination) {
		try {
			this.id = id;
			this.requester = player;
			this.destination = destination;
			this.type = (destination instanceof Home) ? "home" : "player";
			this.target = ((destination instanceof Home) ? ((Home) destination).getName() : ((Player) destination).getName());
			this.target_owner = (destination instanceof Home) ? ((Home) destination).getOwner() : "N/A";
		} catch (NullPointerException e) {
			YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
			requests.set("history." + this.id + ".status", STATUS_INVALID);
			TeleportHelper.saveConfig("requests.yml", requests);
			this.delete();
		}
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

	public String getStatus() {
		return TeleportHelper.getConfig("requests.yml").getString("history." + getID() + ".status");
	}

	public void setStatus(String status) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		requests.set("history." + getID() + ".status", status);
		
		TeleportHelper.saveConfig("requests.yml", requests);
	}

	public void accept() {
		this.accept(false);
	}

	public void accept(boolean silent) {
		Player mRequester = Bukkit.getServer().getPlayerExact(this.requester);
		Player mTarget = Bukkit.getServer().getPlayerExact(this.type == "home" ? this.target_owner : this.target);

		if (mRequester != null) {
			Storage.saveBackLocation(mRequester, mRequester.getLocation());

			if (this.type == "home") {
				mRequester.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.player"), mTarget.getName(), ((Home) this.destination).getName()));
				mTarget.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.otherhome.owner"), mRequester.getName(), ((Home) this.destination).getName()));
				Home.getHome(this.target_owner, this.target).teleportTo(mRequester);
			} else {
				mRequester.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.player.player"), mTarget.getName()));
				mTarget.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.player.target"), mRequester.getName()));
				mRequester.teleport(mTarget.getLocation());
			}
		} else mTarget.sendMessage(Language.getString("plugin.title") + this.requester + " appears to have gone offline...");
	
		this.setStatus(STATUS_ACCEPTED);
		this.delete();
	}

	public void deny() {
		this.deny(false);
	}

	public void deny(boolean silent) {
		Player mRequester = Bukkit.getServer().getPlayerExact(this.requester);
		Player mTarget = Bukkit.getServer().getPlayerExact(this.type == "home" ? this.target_owner : this.target);

		if (mRequester != null && !silent) mRequester.sendMessage(Language.getString("plugin.title") + Language.getString("teleport.request.denied.player"));
		if (mTarget != null && !silent) mTarget.sendMessage(Language.getString("plugin.title") + String.format(Language.getString("teleport.request.denied.target"), requester));

		this.setStatus(STATUS_DENIED);
		this.delete();
	}

	public void delete() {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		requests.set("requests." + this.id, null);

		TeleportHelper.saveConfig("requests.yml", requests);
	}

	//STATIC METHODS
	public static Request makeRequest(String player, Object destination) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");

		long id =  System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()).toString();

		String path = "requests." + id;
		requests.set(path + ".requester", player);
		requests.set(path + ".requested_at", time);
		requests.set(path + ".type", (destination instanceof Home) ? "home" : "player");
		requests.set(path + ".target", ((destination instanceof Home) ? ((Home) destination).getName() : ((Player) destination).getName()));
		requests.set(path + ".target_owner", ((destination instanceof Home) ? ((Home) destination).getOwner() : ((Player) destination).getName()));

		path = "history." + id;
		requests.set(path + ".from", player);
		requests.set(path + ".to", ((destination instanceof Home) ? "home" : "player") + " (" + ((destination instanceof Home) ? ((Home) destination).getName() + " - " + ((Home) destination).getOwner() : ((Player) destination).getName()) + ")");
		requests.set(path + ".at", time);
		requests.set(path + ".status", "active");

		TeleportHelper.saveConfig("requests.yml", requests);

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

	// shouldn't work...
	public static List<Request> getRequests(String target) {
		YamlConfiguration requests = TeleportHelper.getConfig("requests.yml");
		Map<String, Object> requestList = requests.getConfigurationSection("requests").getValues(false);
		List<Request> targetRequests = new ArrayList<Request>();

		for (String request_id : requestList.keySet()) if (requests.getString("requests." + request_id + ".target_owner").equals(target)) targetRequests.add(getRequest(Long.parseLong(request_id)));
		return targetRequests;
	}

	public void clearRequests(String target) {
		for (Request request : getRequests(target)) {
			if (request.getStatus() == STATUS_ACTIVE) request.deny(true);
			else {
				request.setStatus(STATUS_EXPIRED);
				request.delete();
			}
		}
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