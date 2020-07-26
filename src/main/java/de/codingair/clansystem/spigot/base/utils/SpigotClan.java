package de.codingair.clansystem.spigot.base.utils;

import de.codingair.clansystem.utils.clan.Clan;
import de.codingair.clansystem.utils.clan.Rank;
import de.codingair.clansystem.utils.statistics.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpigotClan extends Clan {
    public SpigotClan() {
    }

    public SpigotClan(long id, String name, HashMap<Rank, Integer> ranks, HashMap<UUID, Rank> members, HashMap<Statistic, Long> statistics, int level, float exp) {
        super(id, name, ranks, members, statistics, level, exp);
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();

        for(UUID uuid : members.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) players.add(p);
        }

        return players;
    }

    public boolean isSomebodyOnline() {
        return isSomebodyOnline(null);
    }

    public boolean isSomebodyOnline(Player except) {
        Set<Player> players = getOnlinePlayers();
        if(except != null) players.remove(except);

        boolean online = !players.isEmpty();
        players.clear();

        return online;
    }
}
