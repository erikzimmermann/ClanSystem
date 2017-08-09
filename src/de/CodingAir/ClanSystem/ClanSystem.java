package de.CodingAir.ClanSystem;

import com.mojang.authlib.GameProfile;
import de.CodingAir.ClanSystem.ClanWars.ClanWars;
import de.CodingAir.ClanSystem.Commands.ClanCMD;
import de.CodingAir.ClanSystem.Commands.OptionsCMD;
import de.CodingAir.ClanSystem.Listeners.*;
import de.CodingAir.ClanSystem.Managers.*;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.ClanSystem.Utils.UpdateChecker;
import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.Database.MySQL;
import de.CodingAir.v1_6.CodingAPI.Database.Table;
import de.CodingAir.v1_6.CodingAPI.Server.Version;
import de.CodingAir.v1_6.CodingAPI.Time.Timer;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class ClanSystem extends JavaPlugin {
	public static final boolean DEBUG = false;
	public static final String HEADER_SHORT = "§6>§m-=-=-=-§6| §b§lClan §6|§m-=-=-=-§6<";
	public static final String HEADER = "§6>§m-=-=-=-=-=-§6| §b§lClans §6|§m-=-=-=-=-=-§6<";
	public static String SERVER = null;
	
	public static ItemStack MAIN_ICON() {
		return OldItemBuilder.removeStandardLore(OldItemBuilder.getItem(Material.IRON_SWORD, "§cClanname")).clone();
	}
	
	private static ClanSystem instance;
	
	private ClanManager clanManager = new ClanManager();
	private UpdateChecker updateChecker = new UpdateChecker("https://www.spigotmc.org/resources/clansystem-full-gui-commands.34696/history");
	private Timer timer = new Timer();
	private MySQL database;
	private BungeeCordManager bungeeCordManager = new BungeeCordManager();
	private GameProfileManager gameProfileManager = new GameProfileManager();
	private PlaceholderManager placeholderManager = new PlaceholderManager();
	
	public static boolean updateAvailable = false;
	private static boolean initialized = false;
	
	@Override
	public void onEnable() {
		instance = this;
		API.getInstance().onEnable(this);
		
		this.getLogger().log(Level.INFO, "Startup at post activation.");
		
		Bukkit.getScheduler().runTask(this, new Runnable() {
			@Override
			public void run() {
				timer.start();
				ClanSystem.this.placeholderManager.check();
				
				updateAvailable = updateChecker.needsUpdate();
				
				ClanSystem.log(" ");
				ClanSystem.log("__________________________________________________________");
				ClanSystem.log(" ");
				ClanSystem.log("                       ClanSystem [" + getDescription().getVersion() + "]");
				if(updateAvailable) {
					ClanSystem.log(" ");
					ClanSystem.log("New update available [v" + ClanSystem.this.updateChecker.getVersion() + "]. Download it on \n\nhttps://www.spigotmc.org/resources/clansystem-full-gui-commands.34696/history\n");
				}
				ClanSystem.log(" ");
				ClanSystem.log("Status:");
				ClanSystem.log(" ");
				ClanSystem.log("MC-Versions: " + Version.getVersion().name());
				ClanSystem.log("CodingAPI-Version: " + API.VERSION);
				ClanSystem.log("PlaceholderAPI: " + (ClanSystem.this.placeholderManager.isEnabled() ? "Enabled" : "Disabled"));
				ClanSystem.log(" ");
				
				ClanSystem.log("Connecting to database...");
				if(connectToDatabase()) {
					ClanSystem.log("Connected successfully.");
					refactorDatabase();
				} else ClanSystem.log("Could not connect to the database!");
				
				ClanSystem.log(" ");
				
				getCommand("Clan").setExecutor(new ClanCMD());
				getCommand("Clan").setTabCompleter(new ClanCMD());
				getCommand("Options").setExecutor(new OptionsCMD());
				
				Bukkit.getPluginManager().registerEvents(new ChatListener(), ClanSystem.this);
				Bukkit.getPluginManager().registerEvents(new AllianceListener(), ClanSystem.this);
				Bukkit.getPluginManager().registerEvents(new StatsListener(), ClanSystem.this);
				Bukkit.getPluginManager().registerEvents(new TeleportManager(), ClanSystem.this);
				Bukkit.getPluginManager().registerEvents(new JoinListener(), ClanSystem.this);
				
				ClanWars.setInstance(new ClanWars());
				
				if(Options.BUNGEECORD.getBoolean()) {
					Bukkit.getPluginManager().registerEvents(new BungeeCordListener(), ClanSystem.this);
					ClanAPI.ECHO_PORT = Options.BUNGEECORD_PORT.getInt();
				}
				
				bungeeCordManager.startClient(true, true, new Callback<Boolean>() {
					@Override
					public void accept(Boolean connected) {
						if(Options.BUNGEECORD.getBoolean()) log(" ");
						
						bungeeCordManager.onEnable(new Callback<Boolean>() {
							@Override
							public void accept(Boolean finished) {
								ClanSystem.log("Loading files.");
								gameProfileManager.load();
								gameProfileManager.onEnable();
								clanManager.load();
								clanManager.startInviteChecker();
								
								ClanSystem.log(" ");
								ClanSystem.log("BungeeCord: " + Options.BUNGEECORD.getBoolean());
								ClanSystem.log("Economy: " + Options.ECONOMY_ENABLED.getBoolean());
								ClanSystem.log("Taxes: " + (Options.ECONOMY_ENABLED.getBoolean() && Options.TAXES_ENABLED.getBoolean()));
								ClanSystem.log(" ");
								
								ClanSystem.log("Starting AutoSaver.");
								startAutoSaver();
								
								timer.stop();
								
								ClanSystem.log(" ");
								ClanSystem.log("Done (" + timer.getLastStoppedTime() + "s)");
								ClanSystem.log(" ");
								ClanSystem.log("__________________________________________________________");
								ClanSystem.log(" ");
								
								LayoutManager.onUpdate();
								TaxManager.save();
								bungeeCordManager.refactorData(null, false);
								
								API.getInstance().onEnable(ClanSystem.this);
								
								initialized = true;
							}
						});
					}
				});
			}
		});
	}
	
	@Override
	public void onDisable() {
		API.getInstance().onDisable();
		
		if(!initialized) {
			ClanSystem.log(" ");
			ClanSystem.log("__________________________________________________________");
			ClanSystem.log(" ");
			ClanSystem.log("                       ClanSystem [" + getDescription().getVersion() + "]");
			if(updateAvailable) {
				ClanSystem.log(" ");
				ClanSystem.log("New update available [v" + this.updateChecker.getVersion() + "]. Download it on \n\n" + this.updateChecker.getDownload() + "\n");
			}
			ClanSystem.log("Status:");
			ClanSystem.log(" ");
			ClanSystem.log("MC-Versions: " + Version.getVersion().name());
			ClanSystem.log("CodingAPI-Version: " + API.VERSION);
			ClanSystem.log("PlaceholderAPI: " + (ClanSystem.this.placeholderManager.isEnabled() ? "Enabled" : "Disabled"));
			ClanSystem.log(" ");
			
			timer.start();
			
			ClanSystem.log("Plugin was not initialized.");
			ClanSystem.log("Please report bugs to CodingAir in the Spigot forum.");
			ClanSystem.log("Thanks!");
			
			timer.stop();
			
			ClanSystem.log(" ");
			ClanSystem.log("Done (" + timer.getLastStoppedTime() + "s)");
			ClanSystem.log(" ");
			ClanSystem.log("__________________________________________________________");
			ClanSystem.log(" ");
			return;
		}
		
		ClanSystem.log(" ");
		ClanSystem.log("__________________________________________________________");
		ClanSystem.log(" ");
		ClanSystem.log("                       ClanSystem [" + getDescription().getVersion() + "]");
		if(updateAvailable) {
			ClanSystem.log(" ");
			ClanSystem.log("New update available [v" + this.updateChecker.getVersion() + "]. Download it on \n\n" + this.updateChecker.getDownload() + "\n");
		}
		ClanSystem.log("Status:");
		ClanSystem.log(" ");
		ClanSystem.log("MC-Versions: " + Version.getVersion().name());
		ClanSystem.log("CodingAPI-Version: " + API.VERSION);
		ClanSystem.log("PlaceholderAPI: " + (ClanSystem.this.placeholderManager.isEnabled() ? "Enabled" : "Disabled"));
		ClanSystem.log(" ");
		
		timer.start();
		
		ClanSystem.log("Saving files.");
		this.clanManager.save();
		this.gameProfileManager.save();
		this.bungeeCordManager.onDisable();
		
		timer.stop();
		TaxManager.save();
		
		ClanSystem.log(" ");
		ClanSystem.log("Done (" + timer.getLastStoppedTime() + "s)");
		ClanSystem.log(" ");
		ClanSystem.log("__________________________________________________________");
		ClanSystem.log(" ");
	}
	
	public void startAutoSaver() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().runTaskAsynchronously(ClanSystem.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(initialized) {
							clanManager.save();
							gameProfileManager.save();
							TaxManager.save();
						}
					}
				});
			}
		}, 60 * 20, 60 * 20);
	}
	
	public boolean connectToDatabase() {
		FileConfiguration config = FileManager.MYSQL.getFile().getConfig();
		String host = config.getString("MySQL.Host", null);
		int port = config.getInt("MySQL.Port", -1);
		String database = config.getString("MySQL.Database", null);
		String user = config.getString("MySQL.User", null);
		String password = config.getString("MySQL.Password", null);
		
		if(host == null || port == -1 || database == null || user == null || password == null) return false;
		
		this.database = new MySQL(this, host, port, database, user, password);
		this.database.openConnection();
		
		if(this.database.isConnected()) {
			Table table = new Table(this.database, "Clans");
			
			table.addEntry("Name", "VARCHAR(50) NOT NULL PRIMARY KEY");
			table.addEntry("Leader", "TEXT NOT NULL");
			table.addEntry("Trusted", "TEXT NOT NULL");
			table.addEntry("Members", "TEXT NOT NULL");
			table.addEntry("Level", "INT(50) NOT NULL");
			table.addEntry("Kills", "INT(50) NOT NULL");
			table.addEntry("Deaths", "INT(50) NOT NULL");
			table.addEntry("Balance", "INT(50) NOT NULL");
			table.addEntry("Alliances", "TEXT NOT NULL");
			table.addEntry("Chat", "TINYINT(1) NOT NULL");
			table.addEntry("Base", "TEXT NOT NULL");
			table.addEntry("Icon", "TEXT NOT NULL");
			
			table.create();
			
			table = new Table(this.database, "PlayerData");
			table.addEntry("UniqueId", "VARCHAR(50) NOT NULL PRIMARY KEY");
			table.addEntry("GameProfile", "TEXT NOT NULL");
			table.create();
		}
		
		return this.database.isConnected();
	}
	
	public static final ClanSystem getInstance() {
		return instance;
	}
	
	public static ClanManager getClanManager() {
		return getInstance().clanManager;
	}
	
	public static void log(String message) {
		System.out.println(message);
	}
	
	public UpdateChecker getUpdateChecker() {
		return updateChecker;
	}
	
	public MySQL getMySQL() {
		return database;
	}
	
	public static boolean isInited() {
		return initialized;
	}
	
	public BungeeCordManager getBungeeCordManager() {
		return bungeeCordManager;
	}
	
	public GameProfileManager getGameProfileManager() {
		return gameProfileManager;
	}
	
	public static UUID getUUID(Player p) {
		if(!Options.BUNGEECORD.getBoolean()) return p.getUniqueId();
		return getInstance().getBungeeCordManager().getUniqueIds().get(p.getName());
	}
	
	public static Player getPlayer(UUID uniqueId) {
		if(uniqueId == null) return null;
		if(!Options.BUNGEECORD.getBoolean()) return Bukkit.getPlayer(uniqueId);
		
		for(String name : getInstance().getBungeeCordManager().getUniqueIds().keySet()) {
			if(getInstance().getBungeeCordManager().getUniqueIds().get(name) != null && getInstance().getBungeeCordManager().getUniqueIds().get(name).toString().equals(uniqueId.toString())) {
				return Bukkit.getPlayer(name);
			}
		}
		
		return null;
	}
	
	public static boolean isRegistered(Player p) {
		if(!Options.BUNGEECORD.getBoolean()) return true;
		return getInstance().getBungeeCordManager().isRegistered(p);
	}
	
	public static boolean isOnline(UUID uniqueId) {
		if(!Options.BUNGEECORD.getBoolean()) return Bukkit.getPlayer(uniqueId) != null;
		return getPlayer(uniqueId) != null;
	}
	
	public static boolean isOnProxy(UUID uniqueId) {
		if(!Options.BUNGEECORD.getBoolean()) return Bukkit.getPlayer(uniqueId) != null;
		return getInstance().getBungeeCordManager().isOnline(uniqueId);
	}
	
	public static boolean isOnProxy(String name) {
		if(!Options.BUNGEECORD.getBoolean()) return Bukkit.getPlayer(name) != null;
		return getInstance().getBungeeCordManager().isOnline(name);
	}
	
	public static GameProfile getGameProfile(Player p) {
		return getInstance().getGameProfileManager().getGameProfile(p);
	}
	
	private void refactorDatabase() {
		if(this.database == null || !this.database.isConnected()) return;
		
		try {
			this.database.query("SELECT HomeServer FROM Clans");
		} catch(SQLException ex) {
			log(" ");
			log("Refactor database...");
			database.queryUpdate("ALTER TABLE Clans ADD HomeServer VARCHAR(50) NOT NULL AFTER Icon");
			log("Refactoring completed.");
		}
	}
}
