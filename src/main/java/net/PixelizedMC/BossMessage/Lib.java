package net.PixelizedMC.BossMessage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.confuser.barapi.BarAPI;

public class Lib {
	
	static int count = 0;
	
	public static List<String> getMessage() {
		if (CM.messages.size() > 0) {
			if (CM.random) {
				int r = Utils.randInt(0, CM.messages.size() - 1);
				List<String> message = CM.messages.get(r);
				message.set(0, preGenMsg(message.get(0)));
				return message;
			} else {
				List<String> message = CM.messages.get(count);
				Bukkit.broadcastMessage(message.get(0));
				message.set(0, preGenMsg(message.get(0)));
				count++;
				if (count >= CM.messages.size()) {
					resetCount();
				}
				return message;
			}
		} else {
			List<String> message = new ArrayList<>();
			message.add("�cNo messages were found! Please check your �bconfig.yml�c!");
			message.add("100");
			message.add("100");
			message.add("0");
			return message;
		}
	}
	
	public static String preGenMsg(String m) {
		String rawmsg = m;
		String message = m;
		if (rawmsg.toLowerCase().contains("%rdm_color%".toLowerCase())) {
			String colorcode;
			String colorcodes = CM.colorcodes;
			while (message.toLowerCase().contains("%rdm_color%".toLowerCase())) {
				int randint = Utils.randInt(0, colorcodes.length() - 1);
				colorcode = ChatColor.COLOR_CHAR + Character.toString(colorcodes.charAt(randint));
				message = message.replaceFirst("(?i)%rdm_color%", colorcode);
				if (!CM.repeatrdmcolors) {
					StringBuilder sb = new StringBuilder(colorcodes);
					sb.deleteCharAt(randint);
					colorcodes = sb.toString();
				}
			}
		}
		if (rawmsg.toLowerCase().contains("%rdm_player%".toLowerCase())) {
			List<String> players = getRdmPlayers();
			int randint = Utils.randInt(0, players.size() - 1);
			message = message.replaceAll("(?i)%rdm_player%", players.get(randint));
		}
		if (rawmsg.toLowerCase().contains("%online_players%".toLowerCase())) {
			message = message.replaceAll("(?i)%online_players%", Integer.toString(Bukkit.getOnlinePlayers().length));
		}
		if (rawmsg.toLowerCase().contains("%max_players%".toLowerCase())) {
			message = message.replaceAll("(?i)%max_players%", Integer.toString(Bukkit.getMaxPlayers()));
		}
		if (rawmsg.toLowerCase().contains("%server_name%".toLowerCase())) {
			message = message.replaceAll("(?i)%server_name%", Bukkit.getServerName());
		}
		
		return message;
	}

	public static void setPlayerMsg(Player p, List<String> msg) {
		if (p.hasPermission("bossmessage.see")) {
			if (msg.size() == 4) {
				if (msg.get(0) != null && NumberUtils.isNumber(msg.get(1))) {
					
					String message = generateMsg(p.getName(), msg);
					float percent = Float.parseFloat(msg.get(1));
					
					BarAPI.setMessage(p, message, percent);
				}
			}
		}
	}
	
	public static String generateMsg(String p, List<String> m) {
		List<String> msg = m;
		String message = msg.get(0);
		
		if (msg.get(0).toLowerCase().contains("%player%".toLowerCase())) {
			message = message.replaceAll("(?i)%player%", p);
		}
		if (msg.get(0).toLowerCase().contains("%rdms_color%".toLowerCase())) {
			String colorcode;
			String colorcodes = CM.colorcodes;
			while (message.toLowerCase().contains("%rdms_color%".toLowerCase())) {
				int randint = Utils.randInt(0, colorcodes.length() - 1);
				colorcode = ChatColor.COLOR_CHAR + Character.toString(colorcodes.charAt(randint));
				message = message.replaceFirst("(?i)%rdm_color%", colorcode);
				if (!CM.repeatrdmcolors) {
					StringBuilder sb = new StringBuilder(colorcodes);
					sb.deleteCharAt(randint);
					colorcodes = sb.toString();
				}
			}
		}
		
		return message;
	}
	
	public static List<String> getRdmPlayers() {
		List<String> players = new ArrayList<>();
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (!p.hasPermission("bossmessage.exemptrdm")) {
				players.add(p.getName());
			}
		}
		if (players.size() < 1) {
			players.add("NullPlayer");
		}
		return players;
	}
	
	public static void setMsg(List<String> msg) {
		if (CM.whitelist) {
			List<String> worlds = CM.worlds;
			List<Player> players;
			for (String w:worlds) {
				if (Bukkit.getWorld(w) != null) {
					players = Bukkit.getWorld(w).getPlayers();
					for (Player p:players) {
						setPlayerMsg(p, msg);
					}
					players.clear();
				}
			}
		} else {
			for (Player p:Bukkit.getOnlinePlayers()) {
				setPlayerMsg(p, msg);
			}
		}
	}
	
	public static void resetCount() {
		count = 0;
	}
}