package de.CodingAir.ClanSystem.Listeners;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AllianceListener implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();
		
		if(!(entity instanceof Player)) return;
		
		Player p = (Player) entity;
		Player d = null;
		Entity projectile = null;
		
		if(!(damager instanceof Player)) {
			if(damager.getName().equals("Arrow")) {
				projectile = e.getDamager();
				
				try {
					org.bukkit.craftbukkit.v1_11_R1.entity.CraftArrow arrow = (org.bukkit.craftbukkit.v1_11_R1.entity.CraftArrow) damager;
					if(arrow.getShooter() instanceof Player) d = (Player) arrow.getShooter();
				} catch(NoClassDefFoundError ex) {
					try {
						org.bukkit.craftbukkit.v1_10_R1.entity.CraftArrow arrow = (org.bukkit.craftbukkit.v1_10_R1.entity.CraftArrow) damager;
						if(arrow.getShooter() instanceof Player) d = (Player) arrow.getShooter();
					} catch(NoClassDefFoundError ex1) {
						try {
							org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow arrow = (org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow) damager;
							if(arrow.getShooter() instanceof Player) d = (Player) arrow.getShooter();
						} catch(NoClassDefFoundError ex2) {
							org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow arrow = (org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow) damager;
							if(arrow.getShooter() instanceof Player) d = (Player) arrow.getShooter();
						}
					}
				}
			}
			
			if(d == null) return;
		}
		
		if(d == null) d = (Player) damager;
		
		Clan target = ClanSystem.getClanManager().getClan(p);
		Clan clan = ClanSystem.getClanManager().getClan(d);
		
		if(target == null || clan == null) return;
		
		if(target.hasAllianceWith(clan)) d.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_ALLIANCE_BETWEEN_CLANS.getMessage());
		else if(target.equals(clan)) d.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_SAME_CLAN.getMessage());
		else return;
		
		e.setCancelled(true);
		if(projectile != null) {
			projectile.remove();
			if(p.getFireTicks() == 100) p.setFireTicks(0);
		}
	}
	
}
