package de.codingair.clansystem.spigot.base.listeners;

import de.codingair.clansystem.spigot.ClanSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClanListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ClanSystem.getInstance().getClanManager().onJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ClanSystem.getInstance().getClanManager().onQuit(e.getPlayer());
    }

}
