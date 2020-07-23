package de.codingair.clansystem.spigot;

import de.codingair.clansystem.transfer.spigot.SpigotDataHandler;
import de.codingair.codingapi.API;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;
    private final SpigotDataHandler dataHandler = new SpigotDataHandler(this);

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        dataHandler.onEnable();
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        dataHandler.onDisable();
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    public SpigotDataHandler getDataHandler() {
        return dataHandler;
    }
}
