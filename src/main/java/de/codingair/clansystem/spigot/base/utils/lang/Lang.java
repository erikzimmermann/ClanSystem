package de.codingair.clansystem.spigot.base.utils.lang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.clansystem.spigot.base.utils.money.Bank;
import de.codingair.clansystem.spigot.extras.placeholderapi.PAPI;
import de.codingair.codingapi.files.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
    private static ConfigFile language = null;
    private static FileConfiguration lang = null;

    public static void init() {
        try {
            initPreDefinedLanguages(ClanSystem.getInstance());
            lang(getCurrentLanguage());
            lang = language.getConfig();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

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
        return get(null, "Prefix", s -> s.replace("%PREFIX%", ""));
    }

    public static List<String> getStringList(CommandSender sender, String key) {
        List<String> l = lang.getStringList(key);
        List<String> prepared = new ArrayList<>();

        for(String s : l) {
            if(s == null) prepared.add(null);
            else prepared.add(prepare(sender, s));
        }

        return prepared;
    }

    public static String get(CommandSender sender, String key) {
        return get(sender, key, null);
    }

    public static String get(CommandSender sender, String key, StringModifier modifier) {
        String text = lang.getString(key);

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

        if(modifier != null) text = modifier.modify(text);
        return prepare(sender, text);
    }

    private static String prepare(CommandSender sender, String s) {
        s = s.replace("\\n", "\n");
        s = ChatColor.translateAlternateColorCodes('&', s);
        if(sender instanceof Player) s = PAPI.convert(s, (Player) sender);
        s = s.replace("%ECO%", Bank.name());
        s = s.replace("%PREFIX%", getPrefix());
        return s;
    }

    private static String combine(String s) {
        boolean noPrefix = s.startsWith("%NO_PREFIX%");
        if(noPrefix) s = s.replace("%NO_PREFIX%", "").trim();

        return (noPrefix ? "" : getPrefix()) + s;
    }

    public static void send(CommandSender sender, String s) {
        sender.sendMessage(combine(get(sender, s)));
    }

    /**
     * @param sender CommandSender, Text receiver
     * @param s      Language tag in 'success' category
     */
    public static void suc(CommandSender sender, String s) {
        suc(sender, s, null);
    }

    /**
     * @param sender CommandSender, Text receiver
     * @param s      Language tag in 'success' category
     */
    public static void suc(CommandSender sender, String s, StringModifier modifier) {
        sender.sendMessage(combine(get(sender, "Exception." + s, modifier)));
    }

    /**
     * @param sender CommandSender, Text receiver
     * @param s      Language tag in 'exception' category
     */
    public static void exc(CommandSender sender, String s) {
        exc(sender, s, null);
    }

    /**
     * @param sender CommandSender, Text receiver
     * @param s      Language tag in 'exception' category
     */
    public static void exc(CommandSender sender, String s, StringModifier modifier) {
        sender.sendMessage(getPrefix() + get(sender, "Exception." + s, modifier));
    }

    /**
     * @param sender CommandSender, Text receiver
     * @param s      Language tag in 'command usage' category
     */
    public static void cu(CommandSender sender, String s) {
        sender.sendMessage(combine(get(sender, "Command_Usage." + s)));
    }

    public static void send(CommandSender sender, String s, StringModifier modifier) {
        sender.sendMessage(combine(modifier.modify(get(sender, s))));
    }

    private static FileConfiguration getConfig() {
        if(config == null) {
            try {
                config = ClanSystem.getInstance().getFileManager().getFile("config");
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return config.getConfig();
    }

    private static FileConfiguration lang(String langTag) {
        if(language == null) {
            try {
                language = ClanSystem.getInstance().getFileManager().loadFile(langTag, "/Languages/", "languages/");
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return language.getConfig();
    }
}
