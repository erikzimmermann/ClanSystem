package de.CodingAir.ClanSystem.ClanWars;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.ClanWars.Arena;
import de.CodingAir.ClanSystem.Utils.ClanWars.ProxyArena;
import de.CodingAir.v1_4.CodingAPI.Particles.Animations.Animation;
import de.CodingAir.v1_4.CodingAPI.Particles.Animations.CircleAnimation;
import de.CodingAir.v1_4.CodingAPI.Particles.Particle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class ClanWars {
	private static ClanWars instance;
	
	private List<Arena> arenas = new ArrayList<>();
	private List<ProxyArena> proxyArenas = new ArrayList<>();
	
	private List<Clan> searching = new ArrayList<>();
	private HashMap<Player, WaitingActionBar> actionBars = new HashMap<>();
	private HashMap<Player, Animation> animations = new HashMap<>();
	
	public ClanWars() {
		Bukkit.getPluginManager().registerEvents(new ClanWarsListener(), ClanSystem.getInstance());
		
		startScheduler();
	}
	
	private void startScheduler() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(ClanSystem.getInstance(), new Runnable() {
			int time = 0;
			
			@Override
			public void run() {
				if(time == 5) {
					actionBars.forEach((p, bar) -> bar.onTick());
					time = 0;
				} else time++;
				
				
			}
		}, 1L, 1L);
	}
	
	public void setSearching(Clan clan, boolean searching) {
		if(searching) {
			clan.getOnlinePlayers().forEach(p -> {
				actionBars.remove(p);
				actionBars.put(p, new WaitingActionBar(p, "Test", 4));
				
				CircleAnimation anim = new CircleAnimation(Particle.FLAME, p, ClanSystem.getInstance(), 1);
				anim.setRunning(true);
				animations.remove(p);
				animations.put(p, anim);
			});
			
			if(!this.searching.contains(clan)) this.searching.add(clan);
		} else {
			clan.getOnlinePlayers().forEach(p -> {
				actionBars.remove(p);
				animations.get(p).setRunning(false);
				animations.remove(p);
			});
			
			this.searching.remove(clan);
		}
	}
	
	public boolean isSearching(Clan clan) {
		return this.searching.contains(clan);
	}
	
	public List<Clan> getSearchingClans() {
		return searching;
	}
	
	public static ClanWars getInstance() {
		return instance;
	}
	
	public static void setInstance(ClanWars instance) {
		ClanWars.instance = instance;
	}
	
	public void onJoin(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		
		if(clan == null) return;
		
		if(isSearching(clan)) actionBars.put(p, new WaitingActionBar(p, "Test", 4));
	}
	
	public void onQuit(Player p) {
		this.actionBars.remove(p);
	}
	
	public List<Arena> getArenas() {
		return arenas;
	}
	
	public Arena getArena(String name) {
		for(Arena arena : this.arenas) {
			if(arena.getName().equalsIgnoreCase(name)) return arena;
		}
		
		return null;
	}
	
	public List<ProxyArena> getProxyArenas() {
		return proxyArenas;
	}
	
	public ProxyArena getProxyArena(String name) {
		for(ProxyArena arena : this.proxyArenas) {
			if(arena.getName().equalsIgnoreCase(name)) return arena;
		}
		
		return null;
	}
}
