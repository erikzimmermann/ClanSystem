package de.codingair.clansystem.utils.statistics;

import de.codingair.clansystem.utils.clan.Clan;
import org.bukkit.event.Listener;

public interface Statistic {
    String getName();
    Listener getListener(Clan clan); //create listener instance for clan
}
