package de.codingair.clansystem.spigot;

import de.codingair.clansystem.spigot.base.managers.ClanManager;
import de.codingair.clansystem.spigot.base.utils.lang.Lang;
import de.codingair.clansystem.transfer.spigot.SpigotDataHandler;
import de.codingair.codingapi.API;
import de.codingair.codingapi.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;
    private final SpigotDataHandler dataHandler = new SpigotDataHandler(this);
    private final ClanManager clanManager = new ClanManager();
    private final FileManager fileManager = new FileManager(this);

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        this.fileManager.loadFile("Config");
        Lang.init();
        dataHandler.onEnable();
        clanManager.load();
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        dataHandler.onDisable();
        clanManager.save(false);
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    public SpigotDataHandler getDataHandler() {
        return dataHandler;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
