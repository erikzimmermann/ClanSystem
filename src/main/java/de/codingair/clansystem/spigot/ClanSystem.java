package de.codingair.clansystem.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import de.codingair.codingapi.API;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);
    }

    @Override
    public void onDisable() {

    }

    public static ClanSystem getInstance() {
        return instance;
    }
}
