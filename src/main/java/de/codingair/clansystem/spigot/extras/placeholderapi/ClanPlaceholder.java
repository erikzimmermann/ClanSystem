package de.codingair.clansystem.spigot.extras.placeholderapi;

import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.clansystem.utils.clan.Clan;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ClanPlaceholder extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "clans";
    }

    @Override
    public String getAuthor() {
        return "CodingAir";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {
        if(id.equals("name")) {
            Clan clan = ClanSystem.man().getClan(p);
            if(clan != null) return clan.getName();
        }

        return null;
    }
}
