package de.codingair.clansystem.spigot.base.utils.lang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.clansystem.spigot.base.utils.PAPI;
import de.codingair.codingapi.files.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lang {
    private static final Cache<String, Boolean> EXIST = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static ConfigFile config = null;

    public static void initPreDefinedLanguages(JavaPlugin plugin) throws IOException {
        List<String> languages = new ArrayList<>();
        languages.add("ENG.yml");

        File folder = new File(plugin.getDataFolder(), "/Languages/");
        if(!folder.exists()) mkDir(folder);

        for(String language : languages) {
            InputStream is = plugin.getResource("languages/" + language);

            File file = new File(plugin.getDataFolder() + "/Languages/", language);
            if(!file.exists()) {
                file.createNewFile();
                copy(is, new FileOutputStream(file));
            }
        }
    }

    private static void mkDir(File file) {
        if(!file.exists()) {
            try {
                file.mkdirs();
            } catch(SecurityException ex) {
                throw new IllegalArgumentException("Plugin is not permitted to create a folder!");
            }
        }
    }

    private static long copy(InputStream from, OutputStream to) throws IOException {
        if(from == null) return -1;
        if(to == null) throw new NullPointerException();

        byte[] buf = new byte[4096];
        long total = 0L;

        while(true) {
            int r = from.read(buf);
            if(r == -1) {
                return total;
            }

            to.write(buf, 0, r);
            total += r;
        }
    }

    private static boolean exist(String tag) {
        Boolean b = EXIST.getIfPresent(tag);

        if(b == null) {
            b = new File(ClanSystem.getInstance().getDataFolder(), "/Languages/" + tag + ".yml").exists();
            EXIST.put(tag, b);
        }

        return b;
    }

    public static String getCurrentLanguage() {
        String s = getConfig().getString("ClanSystem.Language", "ENG");
        if(exist(s)) return s;
        return "ENG";
    }

    public static String getPrefix() {
        return get(null, "Prefix");
    }

    public static List<String> getStringList(String key) {
        List<String> l = getLanguageFile(getCurrentLanguage()).getStringList(key);
        List<String> prepared = new ArrayList<>();

        for(String s : l) {
            if(s == null) prepared.add(null);
            else prepared.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return prepared;
    }

    public static String get(Player player, String key) {
        String text = getLanguageFile(getCurrentLanguage()).getString(key);

        if(text == null) {
            if(key.equalsIgnoreCase("Yes") && get(null, "true") != null) {
                String s = get(null, "true");
                return s.equalsIgnoreCase("true") ? "Yes" : s;
            } else if(key.equalsIgnoreCase("No") && get(null, "false") != null) {
                String s = get(null, "false");
                return s.equalsIgnoreCase("false") ? "No" : s;
            }

            throw new IllegalStateException("Unknown translation key: '" + key + "' >> Check " + getCurrentLanguage() + ".yml at '" + key + "'");
        }

        return prepare(player, text);
    }

    private static String prepare(Player player, String s) {
        s = s.replace("\\n", "\n");
        s = ChatColor.translateAlternateColorCodes('&', s);
        s = PAPI.convert(s, player);
        return s;
    }

    public static void send(Player player, String s) {
        player.sendMessage(getPrefix() + get(player, s));
    }

    public static void send(Player player, String s, StringModifier modifier) {
        player.sendMessage(getPrefix() + modifier.modify(get(player, s)));
    }

    private static FileConfiguration getConfig() {
        if(config == null) {
            try {
                config = ClanSystem.getInstance().getFileManager().getFile("Config");
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return config.getConfig();
    }

    private static FileConfiguration getLanguageFile(String langTag) {
        try {
            ConfigFile file = ClanSystem.getInstance().getFileManager().loadFile(langTag, "/Languages/", "languages/");
            return file.getConfig();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void save(Runnable task) {
        ConfigFile file = ClanSystem.getInstance().getFileManager().getFile("Language");
        file.loadConfig();
        task.run();
        file.saveConfig();
    }
}
