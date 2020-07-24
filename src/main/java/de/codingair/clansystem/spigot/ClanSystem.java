package de.codingair.clansystem.spigot;

import de.codingair.clansystem.spigot.base.managers.ClanManager;
import org.bukkit.plugin.java.JavaPlugin;
import de.codingair.codingapi.API;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;
    private final ClanManager clanManager = new ClanManager();

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);
        clanManager.load();
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        clanManager.save(false);
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }
}
