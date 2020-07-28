package de.codingair.clansystem.bungee;

import de.codingair.clansystem.bungee.listeners.GeneralPacketReader;
import de.codingair.clansystem.transfer.bungee.BungeeDataHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ClanSystem extends Plugin {
    private static ClanSystem instance;
    private final BungeeDataHandler dataHandler = new BungeeDataHandler(this);

    @Override
    public void onEnable() {
        instance = this;
        dataHandler.onEnable();
        dataHandler.register(new GeneralPacketReader());
    }

    @Override
    public void onDisable() {
        dataHandler.onDisable();
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    public static ProxyServer proxy() {
        return getInstance().getProxy();
    }

    public BungeeDataHandler getDataHandler() {
        return dataHandler;
    }
}
