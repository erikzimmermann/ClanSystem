package de.CodingAir.ClanSystem.ClanWars;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class ClanWarsListener implements Listener {
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		Player p = e.getPlayer();
		
		if(!p.hasPermission("ClanSystem.ClanWars")) return;
		
		if(!e.getLine(0).equalsIgnoreCase("[ClanWars]")) return;
		
		e.setLine(0, "§8[§6ClanWars§8]");
		e.setLine(1, "");
		e.setLine(2, "§7Click here!");
		e.setLine(3, "");
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		if(b == null || !(b.getState() instanceof Sign)) return;
		
		Sign s = (Sign) b.getState();
		
		if(s.getLine(0).equals("§8[§6ClanWars§8]")) {
			new MenuGUI(p).open();
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		ClanWars.getInstance().onQuit(e.getPlayer());
	}
	
}
