package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.ClanSystem.Utils.Teleport;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager implements Listener {
	private static HashMap<String, Teleport> teleporting = new HashMap<>();
	private static boolean running = false;
	
	public static void teleport(Player p, Location loc) {
		int time = Options.TELEPORT_TIME.getInt();
		boolean canMove = Options.TELEPORT_CAN_MOVE.getBoolean();
		
		teleport(p, loc, time, canMove);
	}
	
	public static void teleport(Player p, Location loc, int time, boolean canMove) {
		if(time <= 0) {
			p.teleport(loc);
			return;
		}
		
		Teleport teleport = new Teleport(p, loc, time, canMove);
		teleporting.put(p.getUniqueId().toString(), teleport);
		
		p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.SUCCESS_TELEPORT_TO_BASE.getMessage(p).replace("%seconds%", time+""));
		
		startScheduler();
	}
	
	public static void startScheduler() {
		if(running) return;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(ClanSystem.getInstance(), new Runnable() {
			int ticks = 0;
			
			@Override
			public void run() {
				if(ticks >= 20) {
					HashMap<String, Teleport> teleporting = new HashMap<>();
					teleporting.putAll(TeleportManager.teleporting);
					
					teleporting.forEach((uniqueId, teleport) -> {
						teleport.setTimeLeft(teleport.getTimeLeft() - 1);
						
						if(teleport.getTimeLeft() <= 0) {
							TeleportManager.teleporting.remove(uniqueId);
							Player p = Bukkit.getPlayer(UUID.fromString(uniqueId));
							
							if(p != null) {
								p.teleport(teleport.getLocation());
								p.playSound(p.getLocation(), Sound.LEVEL_UP.bukkitSound(), 1F, 1F);
							}
							
						} else {
							Player p = Bukkit.getPlayer(UUID.fromString(uniqueId));
							
							if(p != null) {
								p.playSound(p.getLocation(), Sound.NOTE_PLING.bukkitSound(), 1F, 1F);
							}
							
						}
					});
					
					ticks = 0;
				}
				
				ticks++;
			}
		}, 1, 1);
		
		running = true;
	}
	
	public static boolean isTeleporting(Player p) {
		for(String id : teleporting.keySet()) {
			if(id.equals(p.getUniqueId().toString())) return true;
		}
		
		return false;
	}
	
	public static Teleport getTeleport(Player p) {
		return teleporting.get(p.getUniqueId().toString());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!isTeleporting(p)) return;
		
		Teleport teleport = getTeleport(p);
		
		if(teleport.canMove()) return;
		
		double x = e.getFrom().getX() - e.getTo().getX();
		double y = e.getFrom().getY() - e.getTo().getY();
		double z = e.getFrom().getZ() - e.getTo().getZ();
		
		if(x < 0) x *= -1;
		if(y < 0) y *= -1;
		if(z < 0) z *= -1;
		
		double result = x + y + z;
		
		if(result > 0.05){
			//Cancel
			teleporting.remove(p.getUniqueId().toString());
			p.playSound(p.getLocation(), Sound.ITEM_BREAK.bukkitSound(), 1F, 1F);
			p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_TELEPORT_CANCELED.getMessage(p));
		}
		
	}
}
