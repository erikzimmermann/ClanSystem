package de.codingair.clansystem.spigot.base.utils.money;

import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.codingapi.files.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Bank {
    private static Bank instance;
    private String displayName;
    private Adapter adapter;

    private Bank() {
        //priority
        ConfigFile file = ClanSystem.getInstance().getFileManager().getFile("config");
        FileConfiguration config = file.getConfig();
        this.displayName = config.getString("ClanSystem.Economy.Name", "Coin(s)").trim();

        if(!config.getBoolean("ClanSystem.Economy.Enabled", true)) return;

        for(String s : config.getStringList("ClanSystem.Economy.Priority")) {
            PreDefined preDefined = PreDefined.getByName(s);
            if(preDefined != null && preDefined.getAdapter() != null) {
                adapter = preDefined.getAdapter();
                break;
            }
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static Bank getInstance() {
        if(instance == null) instance = new Bank();
        return instance;
    }

    public static String name() {
        return getInstance().displayName;
    }

    public static double getMoney(Player player) {
        if(!isReady()) return 0;
        return adapter().getMoney(player);
    }

    public static boolean hasMoney(Player player, double money) {
        if(!isReady()) return false;
        return adapter().getMoney(player) >= money;
    }

    public static void withdraw(Player player, double amount) {
        if(!isReady()) return;
        adapter().withdraw(player, amount);
    }

    public static void deposit(Player player, double amount) {
        if(!isReady()) return;
        adapter().withdraw(player, amount);
    }

    public static Adapter adapter() {
        return getInstance().adapter;
    }

    public static boolean isReady() {
        return adapter() != null;
    }
}
