package de.CodingAir.ClanSystem.BungeeCord;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import org.bukkit.event.EventHandler;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class Listener implements net.md_5.bungee.api.plugin.Listener {
	
	@EventHandler
	public void onJoin(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		
		BungeeUpdater.getInstance().getEchoServer().sendToAll(p.getName() + " | " + p.getUniqueId());
	}
	
	@EventHandler
	public void onJoin(ServerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		
		BungeeUpdater.getInstance().getEchoServer().sendToAll(p.getName() + " | " + p.getUniqueId());
	}
	
}
