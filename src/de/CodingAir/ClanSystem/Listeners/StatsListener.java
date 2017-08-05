package de.CodingAir.ClanSystem.Listeners;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LayoutManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatsListener implements Listener {
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player death = e.getEntity();
		Player killer = e.getEntity().getKiller();
		
		Clan target = ClanSystem.getClanManager().getClan(death);
		if(target != null) target.setDeaths(target.getDeaths() + 1);
		
		if(killer != null){
			Clan clan = ClanSystem.getClanManager().getClan(killer);
			if(clan != null) clan.setKills(clan.getKills() + 1);
			LayoutManager.onUpdate();
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.ACTION, "UPDATE_LAYOUT", null, null, null));
		}
	}
	
}
