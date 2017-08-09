package de.CodingAir.ClanSystem.Listeners;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Managers.LayoutManager;
import de.CodingAir.ClanSystem.Utils.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener{
	
	public JoinListener() {
		onReload();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		
		if(!Options.BUNGEECORD.getBoolean()) LayoutManager.onUpdate();
		
		if(!p.hasPermission("ClanSystem.Notify")) return;
		
		if(ClanSystem.updateAvailable){
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage(LanguageManager.PREFIX.getMessage(p)+"§aA new update is available §8[§bv"+ClanSystem.getInstance().getUpdateChecker().getVersion()+"§8]§a. Download it on §b§nhttps://www.spigotmc.org/resources/clansystem-full-gui-commands.34696/history");
			p.sendMessage("");
			p.sendMessage("");
		}
	}
	
	public void onReload(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.hasPermission("ClanSystem.Notify")){
				if(ClanSystem.updateAvailable){
					p.sendMessage("");
					p.sendMessage("");
					p.sendMessage(LanguageManager.PREFIX.getMessage(p)+"§aA new update is available §8[§bv"+ClanSystem.getInstance().getUpdateChecker().getVersion()+"§8]§a. Download it on §b§nhttps://www.spigotmc.org/resources/clansystem-full-gui-commands.34696/history");
					p.sendMessage("");
					p.sendMessage("");
				}
			}
		}
	}
}
