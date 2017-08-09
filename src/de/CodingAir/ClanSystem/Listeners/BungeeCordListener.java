package de.CodingAir.ClanSystem.Listeners;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.ClanWars.ClanWars;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Managers.LayoutManager;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Request;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxiedPlayer;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxyJoinEvent;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxyQuitEvent;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.logging.Level;

public class BungeeCordListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(!ClanSystem.isInited()) return;
		
		Player p = e.getPlayer();
		
		if(Options.BUNGEECORD.getBoolean() && !ClanSystem.getInstance().getBungeeCordManager().isConnected() && p.hasPermission("ClanSystem.Notify")) {
			p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_CLIENT_NOT_CONNECTED.getMessage(p));
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(ClanSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				ClanSystem.getInstance().getBungeeCordManager().getCurrentServer(new Callback<String>() {
					@Override
					public void accept(String server) {
						if(server != null && !server.equalsIgnoreCase("null")) {
							ClanSystem.SERVER = server;
							ClanSystem.getInstance().getBungeeCordManager().request(new Request(Request.Type.PLAYERS, ClanSystem.SERVER));
						}
						
						ClanSystem.getInstance().getBungeeCordManager().register(p);
						new BukkitRunnable() {
							@Override
							public void run() {
								if(ClanSystem.getInstance().getBungeeCordManager().isRegistered(p)) {
									if(ClanSystem.getUUID(p) != null) {
										Update update = new Update(Update.Type.PLAYER, "JOIN", null, Update.Encoding.LIST, Arrays.asList(ClanSystem.getUUID(p).toString(), p.getName(), ClanSystem.SERVER));
										ClanSystem.getInstance().getBungeeCordManager().synchronize(update);
										
										ClanSystem.getInstance().getGameProfileManager().update(p);
										LayoutManager.onUpdate();
										
										if(ClanSystem.isOnProxy(ClanSystem.getUUID(p))) {
											ClanSystem.getInstance().getBungeeCordManager().setOnline(ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer(ClanSystem.getUUID(p)), false);
										}
										
										Clan clan = ClanSystem.getClanManager().getClan(p);
										
										if(clan != null) {
											clan.updateName(p);
											ClanWars.getInstance().onJoin(p);
										}
									} else {
										ClanSystem.getInstance().getLogger().log(Level.SEVERE, "Could not load the UUID of '" + p.getName() + "'");
									}
									
									this.cancel();
								}
							}
						}.runTaskTimer(ClanSystem.getInstance(), 2L, 2L);
					}
				});
			}
		});
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(ClanSystem.getUUID(p) == null) return;
		
		Update update = new Update(Update.Type.PLAYER, "QUIT", null, Update.Encoding.LIST, Arrays.asList(ClanSystem.getUUID(p).toString(), p.getName(), ClanSystem.SERVER));
		ClanSystem.getInstance().getBungeeCordManager().synchronize(update);
		
		ClanSystem.getInstance().getBungeeCordManager().unregister(p);
	}
	
	@EventHandler
	public void onJoin(ProxyJoinEvent e) {
		ProxiedPlayer p = e.getPlayer();
		
		ClanSystem.getInstance().getBungeeCordManager().setOnline(p, true);
		
		//Bukkit.broadcastMessage("§5"+p.getName()+" joined the proxy on '"+e.getServer()+"'");
	}
	
	@EventHandler
	public void onQuit(ProxyQuitEvent e) {
		ProxiedPlayer p = e.getPlayer();
		
		ClanSystem.getInstance().getBungeeCordManager().setOnline(p, false);
		
		//Bukkit.broadcastMessage("§5"+p.getName()+" quit the proxy on '"+e.getServer()+"'");
	}
}
