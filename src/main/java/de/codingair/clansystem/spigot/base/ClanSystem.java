package de.codingair.clansystem.spigot.base;

import org.bukkit.plugin.java.JavaPlugin;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    public static ClanSystem getInstance() {
        return instance;
    }
}
