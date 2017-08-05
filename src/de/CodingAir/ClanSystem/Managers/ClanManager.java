package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Invite;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import de.CodingAir.v1_4.CodingAPI.Database.MySQL;
import de.CodingAir.v1_4.CodingAPI.Database.QueryUpdate;
import de.CodingAir.v1_4.CodingAPI.Tools.Callback;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ClanManager {
	private static final int INVITE_EXPIRATION = 30; //SECONDS
	
	private List<Clan> clans = new ArrayList<>();
	private List<Invite> invites = new ArrayList<>();
	private List<Invite> allianceInvites = new ArrayList<>();
	
	public void fetch(Callback<List<Clan>> callback) {
		MySQL database = ClanSystem.getInstance().getMySQL();
		
		if(database != null && database.isConnected()) {
			List<Clan> clans = new ArrayList<>();
			
			try {
				ResultSet set = database.query("SELECT * from Clans");
				while(set.next()) {
					JSONObject json = new JSONObject();
					
					json.put("Name", set.getString("Name"));
					json.put("Balance", set.getInt("Balance"));
					json.put("Alliances", set.getString("Alliances"));
					json.put("Chat", set.getBoolean("Chat"));
					json.put("Level", set.getInt("Level"));
					json.put("Kills", set.getInt("Kills"));
					json.put("Deaths", set.getInt("Deaths"));
					json.put("Leader", set.getString("Leader"));
					json.put("Trusted", set.getString("Trusted"));
					json.put("Members", set.getString("Members"));
					json.put("Base", set.getString("Base"));
					json.put("Icon", set.getString("Icon"));
					json.put("HomeServer", set.getString("HomeServer"));
					
					Clan clan = new Clan(json);
					clans.add(clan);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			callback.accept(clans);
			return;
		}
		
		callback.accept(null);
	}
	
	public void load() {
		MySQL database = ClanSystem.getInstance().getMySQL();
		
		if(database != null && database.isConnected()) {
			
			try {
				ResultSet set = database.query("SELECT * from Clans");
				while(set.next()) {
					JSONObject json = new JSONObject();
					
					json.put("Name", set.getString("Name"));
					json.put("Balance", set.getInt("Balance"));
					json.put("Alliances", set.getString("Alliances"));
					json.put("Chat", set.getBoolean("Chat"));
					json.put("Level", set.getInt("Level"));
					json.put("Kills", set.getInt("Kills"));
					json.put("Deaths", set.getInt("Deaths"));
					json.put("Leader", set.getString("Leader"));
					json.put("Trusted", set.getString("Trusted"));
					json.put("Members", set.getString("Members"));
					json.put("Base", set.getString("Base"));
					json.put("Icon", set.getString("Icon"));
					json.put("HomeServer", set.getString("HomeServer"));
					
					Clan clan = new Clan(json);
					this.clans.add(clan);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration config = FileManager.CLANS.getFile().getConfig();
		
		config.getKeys(false).forEach(key -> {
			try {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(config.getString(key));
				
				JSONObject json = (JSONObject) obj;
				
				Clan clan = new Clan(json);
				if(!this.exists(clan.getName())) this.clans.add(clan);
			} catch(ParseException ex) {
				ex.printStackTrace();
				ClanSystem.getInstance().getLogger().log(Level.SEVERE, "Could not load clans!");
				clans = new ArrayList<>();
				return;
			}
		});
		
		checkClans();
	}
	
	public void save() {
		FileManager.ConfigFile file = FileManager.CLANS.getFile();
		FileConfiguration config = file.getConfig();
		
		try {
			if(ClanSystem.getInstance().getMySQL() != null && ClanSystem.getInstance().getMySQL().isConnected()) {
				MySQL database = ClanSystem.getInstance().getMySQL();
				
				List<QueryUpdate> updates = new ArrayList<>();
				
				this.clans.forEach(clan -> {
					JSONObject json = clan.getJSON();
					
					QueryUpdate update = new QueryUpdate(database, "Clans", "Name", clan.getName());
					
					update.addEntry("Name", json.get("Name"));
					update.addEntry("Balance", clan.getBalance());
					update.addEntry("Alliances", json.get("Alliances"));
					update.addEntry("Chat", (clan.isChat() ? 1 : 0));
					update.addEntry("Level", clan.getLevel());
					update.addEntry("Kills", clan.getKills());
					update.addEntry("Deaths", clan.getDeaths());
					update.addEntry("Leader", json.get("Leader"));
					update.addEntry("Trusted", json.get("Trusted"));
					update.addEntry("Members", json.get("Members"));
					update.addEntry("Base", json.get("Base"));
					update.addEntry("Icon", json.get("Icon"));
					update.addEntry("HomeServer", json.get("HomeServer"));
					
					updates.add(update);
				});
				
				database.queryUpdate("TRUNCATE TABLE `Clans`");
				
				updates.forEach(update -> update.send());
			}
			
			config.getKeys(false).forEach(key -> config.set(key, null));
			
			this.clans.forEach(clan -> config.set(clan.getName(), clan.toString()));
			
			file.saveConfig();
		} catch(Exception e) {
			try {
				config.getKeys(false).forEach(key -> config.set(key, null));
				
				this.clans.forEach(clan -> config.set(clan.getName(), clan.toString()));
				
				file.saveConfig();
				
				ClanSystem.getInstance().getLogger().log(Level.SEVERE, "Could not save the data on the database! Please contact me (CodingAir) at the Spigot forum with following exception:");
				e.printStackTrace();
			} catch(Exception ex) {
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				ClanSystem.getInstance().getLogger().log(Level.SEVERE, "Could not save the data! Please contact me (CodingAir) at the Spigot forum with following exception:");
				System.out.println(" ");
				ex.printStackTrace();
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
			}
		}
	}
	
	public void checkClans() {
		this.clans.forEach(clan -> {
			List<String> alliances = new ArrayList<>();
			alliances.addAll(clan.getAlliances());
			
			alliances.forEach(name -> {
				if(this.getClan(name) == null) clan.removeAlliance(name);
			});
		});
	}
	
	public void startInviteChecker() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(ClanSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				invites.forEach(invite -> {
					if(invite.getExpire() > 0) invite.setExpire(invite.getExpire() - 1);
					else invite.setExpired(true);
				});
				
				allianceInvites.forEach(invite -> {
					if(invite.getExpire() > 0) invite.setExpire(invite.getExpire() - 1);
					else invite.setExpired(true);
				});
				
				List<Invite> invites = new ArrayList<>();
				invites.addAll(ClanManager.this.invites);
				
				invites.forEach(invite -> {
					if(invite.isExpired()) ClanManager.this.invites.remove(invite);
				});
				
				List<Invite> allianceInvites = new ArrayList<>();
				allianceInvites.addAll(ClanManager.this.allianceInvites);
				
				allianceInvites.forEach(invite -> {
					if(invite.isExpired()) ClanManager.this.allianceInvites.remove(invite);
				});
			}
		}, 20L, 20L);
	}
	
	public Clan getClan(String name) {
		for(Clan clan : this.clans) {
			if(clan != null && clan.getName().equalsIgnoreCase(name)) return clan;
		}
		
		return null;
	}
	
	public Clan getClan(Player p) {
		return getClan(ClanSystem.getUUID(p));
	}
	
	public Clan getClan(UUID uniqueId) {
		for(Clan clan : this.clans) {
			if(clan != null && clan.isMember(uniqueId)) return clan;
		}
		
		return null;
	}
	
	public List<Clan> getClans() {
		return clans;
	}
	
	public void setClans(List<Clan> clans) {
		this.clans = clans;
	}
	
	public boolean exists(String name) {
		return this.getClan(name) != null;
	}
	
	public Clan createClan(Player leader, String name) {
		Clan clan = new Clan(name, leader);
		this.clans.add(clan);
		LayoutManager.onUpdate();
		
		Update update = new Update(Update.Type.CLAN, name, null, Update.Encoding.STRING, clan.toString());
		ClanSystem.getInstance().getBungeeCordManager().synchronize(update);
		
		return clan;
	}
	
	public void removeClan(Clan clan) {
		this.clans.remove(clan);
		
		this.clans.forEach(clans -> clans.removeAlliance(clan));
		
		Update update = new Update(Update.Type.ACTION, "DELETE", null, Update.Encoding.STRING, clan.getName());
		ClanSystem.getInstance().getBungeeCordManager().synchronize(update);
	}
	
	public void invite(Clan clan, UUID uniqueId) {
		this.invites.add(new Invite(clan, uniqueId, INVITE_EXPIRATION));
	}
	
	public void alliance(Clan clan, Clan target) {
		this.allianceInvites.add(new Invite(clan, target, INVITE_EXPIRATION));
	}
	
	public List<Invite> getInvites(Player p) {
		return getInvites(ClanSystem.getUUID(p));
	}
	
	public List<Invite> getInvites(UUID uniqueId) {
		List<Invite> invites = new ArrayList<>();
		this.invites.forEach(invite -> {
			if(invite.getTarget().toString().equals(uniqueId.toString()))
				invites.add(invite);
		});
		
		return invites;
	}
	
	public Invite getInvite(Player p, Clan clan) {
		return getInvite(ClanSystem.getUUID(p), clan);
	}
	
	public Invite getInvite(UUID uniqueId, Clan clan) {
		for(Invite invite : this.getInvites(uniqueId)) {
			if(((UUID)invite.getTarget()).toString().equals(uniqueId.toString()) && ((Clan) invite.getHandler()).equals(clan))
				return invite;
		}
		
		return null;
	}
	
	public boolean hasInvite(Player p, Clan clan) {
		return this.getInvite(p, clan) != null;
	}
	
	public boolean hasInvite(UUID uniqueId, Clan clan) {
		return this.getInvite(uniqueId, clan) != null;
	}
	
	public void removeInvite(Player target, Clan clan) {
		Invite invite = this.getInvite(target, clan);
		
		this.invites.remove(invite);
	}
	
	public List<Invite> getAllianceInvites(Clan target) {
		List<Invite> allianceInvites = new ArrayList<>();
		this.allianceInvites.forEach(invite -> {
			if(((Clan) invite.getTarget()).equals(target)) allianceInvites.add(invite);
		});
		
		return allianceInvites;
	}
	
	public Invite getAllianceInvite(Clan target, Clan clan) {
		for(Invite invite : this.getAllianceInvites(target)) {
			if(((Clan) invite.getTarget()).equals(target) && ((Clan) invite.getHandler()).equals(clan)) return invite;
		}
		
		return null;
	}
	
	public boolean hasAllianceInvite(Clan target, Clan clan) {
		return this.getAllianceInvite(target, clan) != null;
	}
	
	public void removeAllianceInvite(Clan target, Clan clan) {
		Invite invite = this.getAllianceInvite(target, clan);
		
		this.allianceInvites.remove(invite);
	}
	
	public boolean trustedCanKick() {
		return Options.TRUSTED_PERMISSIONS_KICK.getBoolean();
	}
	
	public boolean trustedCanInvite() {
		return Options.TRUSTED_PERMISSIONS_INVITE.getBoolean();
	}
	
	public boolean trustedCanAlliance() {
		return Options.TRUSTED_PERMISSIONS_ALLIANCE.getBoolean();
	}
	
	public boolean trustedCanNeutral() {
		return Options.TRUSTED_PERMISSIONS_NEUTRAL.getBoolean();
	}
	
	public boolean trustedCanToggleChat() {
		return Options.TRUSTED_PERMISSIONS_TOGGLE_CHAT.getBoolean();
	}
	
	public boolean trustedCanPromote() {
		return Options.TRUSTED_PERMISSIONS_PROMOTE.getBoolean();
	}
	
	public boolean trustedCanDemote() {
		return Options.TRUSTED_PERMISSIONS_DEMOTE.getBoolean();
	}
	
	public boolean trustedCanSetBase() {
		return Options.TRUSTED_PERMISSIONS_SET_BASE.getBoolean();
	}
	
	public boolean trustedCanSetIcon() {
		return Options.TRUSTED_PERMISSIONS_SET_ICON.getBoolean();
	}
	
	public String getColor(int rank) {
		switch(rank) {
			case 0:
				return Options.CLAN_RANK_COLOR_LEADER.getString();
			case 1:
				return Options.CLAN_RANK_COLOR_TRUSTED.getString();
			case 2:
				return Options.CLAN_RANK_COLOR_MEMBER.getString();
			default:
				return Options.CLAN_RANK_COLOR_MEMBER.getString();
		}
	}
	
	public String getClanColor(int rank) {
		switch(rank) {
			case 1:
				return Options.TOP_CLANS_COLORS_1.getString();
			case 2:
				return Options.TOP_CLANS_COLORS_2.getString();
			case 3:
				return Options.TOP_CLANS_COLORS_3.getString();
			case 4:
				return Options.TOP_CLANS_COLORS_4.getString();
			case 5:
				return Options.TOP_CLANS_COLORS_5.getString();
			default:
				return Options.TOP_CLANS_COLORS_DEFAULT.getString();
		}
	}
	
	public List<Clan> getRankList() {
		List<Clan> clans = new ArrayList<>();
		
		for(int i = 0; i < this.clans.size(); i++) {
			Clan top = null;
			
			for(Clan clan : this.clans) {
				if(clans.contains(clan)) continue;
				if(top == null) top = clan;
				else if(top.getKills() < clan.getKills()) top = clan;
			}
			
			clans.add(top);
		}
		
		return clans;
	}
	
	public static String getClanPrefix(Clan clan) {
		return LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank()));
	}
}
