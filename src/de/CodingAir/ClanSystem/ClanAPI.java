package de.CodingAir.ClanSystem;

import de.CodingAir.ClanSystem.Utils.Clan;
import org.bukkit.entity.Player;

public class ClanAPI {
	public static int ECHO_PORT = 3006;
	
	public static Clan getClan(Player p){
		return ClanSystem.getClanManager().getClan(p);
	}
	
}
