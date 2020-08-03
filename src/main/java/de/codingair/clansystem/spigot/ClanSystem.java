package de.codingair.clansystem.spigot;

import de.codingair.clansystem.spigot.base.managers.ClanManager;
import de.codingair.clansystem.spigot.base.utils.lang.Lang;
import de.codingair.clansystem.transfer.spigot.SpigotDataHandler;
import de.codingair.clansystem.utils.database.ConnectionManager;
import de.codingair.codingapi.API;
import de.codingair.codingapi.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;

public class ClanSystem extends JavaPlugin {
    private static ClanSystem instance;
    private final SpigotDataHandler dataHandler = new SpigotDataHandler(this);
    private final ClanManager clanManager = new ClanManager();
    private final FileManager fileManager = new FileManager(this);

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);
        this.fileManager.loadFile("config");

        /*
            TODO: Better wording and visualization
         */
        try {
            new ConnectionManager();
        } catch (SQLException sqlex) {
            Bukkit.getLogger().log(Level.WARNING,sqlex.toString());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } catch (ParseException parsex) {
            Bukkit.getLogger().log(Level.WARNING,parsex.toString());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Lang.init();
        dataHandler.onEnable();
        clanManager.load();
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        dataHandler.onDisable();
        clanManager.save(false);
        try {
            ConnectionManager.close();
        } catch (Exception e) {
            //we cannot handle an exception properly at this point...
        }
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
