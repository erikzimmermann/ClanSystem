package de.CodingAir.ClanSystem.Placeholders;

import de.CodingAir.ClanSystem.ClanAPI;
import de.CodingAir.ClanSystem.ClanSystem;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class Hooker extends EZPlaceholderHook {

    public Hooker() {
        super(ClanSystem.getInstance(), "ClanSystem");
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {

        switch (id.toLowerCase()) {
            case "clan": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getName() : "-";
            case "clan_level": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getLevel()+"" : "0";
            case "clan_leader": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getLeader()+"" : "-";
            case "clan_balance": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getBalance()+"" : "-";
            case "clan_rank": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getClanRank()+"" : "-";
            case "clan_deaths": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getDeaths()+"" : "-";
            case "clan_kills": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getKills()+"" : "-";
            case "clan_size": return ClanAPI.getClan(p) != null ? ClanAPI.getClan(p).getSize()+"" : "-";
            default: return null;
        }
    }
}
