package de.codingair.clansystem.spigot.base.managers;

import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.clansystem.spigot.base.listeners.ClanListener;
import de.codingair.clansystem.spigot.base.utils.SpigotClan;
import de.codingair.clansystem.spigot.base.utils.lang.Lang;
import de.codingair.clansystem.utils.DataModule;
import de.codingair.clansystem.utils.clan.Clan;
import de.codingair.clansystem.utils.clan.Permission;
import de.codingair.clansystem.utils.clan.Rank;
import de.codingair.clansystem.utils.clan.exceptions.ClanNameNotAvailableException;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.time.TimeSet;
import de.codingair.codingapi.utils.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClanManager implements DataModule {
    private final HashMap<String, SpigotClan> clans = new HashMap<>(); //SpigotClan name -> SpigotClan
    private final HashMap<Player, SpigotClan> playerClans = new HashMap<>(); //player -> SpigotClan

    private static final long TIME_OUT = 5 * 60 * 1000;  //timeout in milliseconds to remove clan from cache
    private final TimeSet<String> waiting;  //remove clan after timeout, cancel if clan member joins again
    private final Set<Object> loading = new HashSet<>(); //String (SpigotClan name) or Player; for command feedback -> "Please wait a moment while loading your data."

    private ClanListener listener;

    public ClanManager() {
        waiting = new TimeSet<String>() {
            @Override
            public void timeout(String key) {
                //ignore playerClans <-- no player is online if timeout is triggered
                Clan c = clans.remove(key);
                if(c != null) save(c);
            }
        };
    }

    @Override
    public void load() {
        if(!clans.isEmpty() || !playerClans.isEmpty()) throw new IllegalStateException("Cannot load with existing data!");

        Bukkit.getPluginManager().registerEvents(listener = new ClanListener(), ClanSystem.getInstance());

        //load data for online players
        loadClan(Bukkit.getOnlinePlayers());
    }

    @Override
    public void destroy() {
        clans.clear();
        playerClans.clear();

        if(this.listener != null) {
            HandlerList.unregisterAll(this.listener);
            this.listener = null;
        }
    }

    @Override
    public void save(boolean autoSaver) {
        //todo: save all to db
    }

    private void save(Clan clan) {
        //todo: save clan to db
    }

    public void onJoin(Player player) {
        SpigotClan c = getClan(player);
        if(c != null) this.waiting.remove(prepareName(c.getName(), true));
        else {
            //try to load
            loadClan(player, null);
        }
    }

    public void onQuit(Player player) {
        SpigotClan c = getClan(player);

        if(c != null) {
            if(!c.isSomebodyOnline(player)) {
                this.waiting.add(prepareName(c.getName(), true), TIME_OUT);
            }
        }
    }

    public void create(Player player, String name, Callback<SpigotClan> callback) throws ClanNameNotAvailableException {
        if(exists(name)) throw new ClanNameNotAvailableException();

        //todo: insert clan for id

        Node<HashMap<Integer, Rank>, Rank> ranks = createDefaultRankMap(/*TODO*/ 0);
        SpigotClan clan = new SpigotClan(/*TODO: NEW ID*/ 0, prepareName(name, false), player, ranks.getKey(), ranks.getValue());

        this.clans.put(prepareName(name, true), clan);
        this.playerClans.put(player, clan);

        callback.accept(clan);
    }

    /**
     * @param clanId Provides the Clan Id to create default ranks in the DB
     * @return Node<HashMap < Integer, Rank>, Rank>. HashMap contains all available ranks and the value of the node equals a president rank.
     */
    public Node<HashMap<Integer, Rank>, Rank> createDefaultRankMap(int clanId) {
        Node<HashMap<Integer, Rank>, Rank> ranks = new Node<>(new HashMap<>(), null);

        Rank member = new Rank(/*TODO: NEW ID*/ 0, "&7Member", new HashSet<Permission>() {{
            add(Permission.DEPOSIT);
        }});

        //todo: insert member for id (and traceId)
        ranks.getKey().put(member.getId(), member);

        Rank vice = new Rank(/*TODO: NEW ID*/ 0, member.getId(), "&bVice", new HashSet<Permission>() {{
            add(Permission.WITHDRAW);
            add(Permission.INVITE);
            add(Permission.KICK);
        }});

        //todo: insert vice for id (and traceId)
        ranks.getKey().put(vice.getId(), vice);

        Rank president = new Rank(/*TODO: NEW ID*/ 0, vice.getId(), "&cPresident", new HashSet<Permission>() {{
            add(Permission.PROMOTE);
            add(Permission.DEMOTE);
            add(Permission.RENAME);
            add(Permission.TRANSFER);
            add(Permission.DELETE);
        }});

        //todo: insert president for id
        ranks.getKey().put(president.getId(), president);
        ranks.setValue(president);

        return ranks;
    }

    private void loadClan(String name, @Nullable Callback<SpigotClan> callback) {
        SpigotClan c = getClan(name);
        if(c != null) {
            if(callback != null) callback.accept(c);
            return;
        }

        loading.add(name);

        /*
            TODO: LOADING
            - load SpigotClan
            - add to cache
            - remove SpigotClan name from loading list
            - trigger callback IF NOT NULL
         */
    }

    private void loadClan(Player player, @Nullable Callback<SpigotClan> callback) {
        SpigotClan c = getClan(player);
        if(c != null) {
            if(callback != null) callback.accept(c);
            return;
        }

        loading.add(player);

        /*
            TODO: LOADING
            - load SpigotClan
            - add to cache
            - remove player from loading list
            - trigger callback IF NOT NULL
         */
    }

    private void loadClan(Collection<? extends Player> players) {
        players.removeIf(p -> getClan(p) != null);
        if(players.isEmpty()) return;

        loading.addAll(players);

        /*
            TODO: LOADING
            - load clans
            - add to cache
            - remove players and SpigotClan names from loading list
         */
    }

    public boolean hasClan(Player player) {
        return getClan(player) != null;
    }

    public SpigotClan getClan(Player player) {
        return playerClans.get(player);
    }

    public SpigotClan getClan(String name) {
        return clans.get(prepareName(name, true));
    }

    public boolean exists(String name) {
        return getClan(name) != null;
    }

    public static String prepareName(String clanName, boolean asKey) {
        if(clanName == null) return null;

        clanName = ChatColor.translateAlternateColorCodes('&', clanName);
        clanName = ChatColor.stripColor(clanName);
        clanName.trim();
        if(asKey) clanName = clanName.replace(" ", "_");

        return clanName;
    }

    public boolean isPending(Player player) {
        return loading.contains(player);
    }

    public boolean isPending(String SpigotClan) {
        return loading.contains(SpigotClan);
    }

    public boolean check(CommandSender sender) {
        if(sender instanceof Player) {
            if(isPending((Player) sender)) {
                Lang.exc(sender, "Loading");
                return true;
            }
        }

        return false;
    }
}
