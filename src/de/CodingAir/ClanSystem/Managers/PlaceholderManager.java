package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.Placeholder.Placeholders.Hooker;
import org.bukkit.Bukkit;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class PlaceholderManager {
	private boolean enabled = false;
	private Hooker hooker;
	
	public void check() {
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			enabled = true;
			
			this.hooker = new Hooker();
			this.hooker.hook();
		} else {
			enabled = false;
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
}
