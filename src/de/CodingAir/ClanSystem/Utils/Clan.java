package de.CodingAir.ClanSystem.Utils;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LayoutManager;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxiedPlayer;
import de.CodingAir.v1_6.CodingAPI.Tools.Location;
import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Clan {
	private String homeServer = null;
	private String name;
	private int balance;
	private List<String> alliances;
	private boolean chat;
	private int level;
	private int kills;
	private int deaths;
	private Location base;
	
	private String leader;
	private String leader_uuid;
	
	private HashMap<String, String> trusted;
	private HashMap<String, String> members;
	
	private ItemStack icon;
	
	public Clan(String name, Player leader) {
		if(Options.BUNGEECORD.getBoolean()) {
			this.homeServer = ClanSystem.SERVER;
		}
		
		this.name = name;
		this.leader = leader.getName();
		this.leader_uuid = ClanSystem.getUUID(leader).toString();
		
		this.balance = 0;
		this.alliances = new ArrayList<>();
		this.chat = true;
		this.level = 1;
		this.kills = 0;
		this.deaths = 0;
		this.trusted = new HashMap<>();
		this.members = new HashMap<>();
		this.base = null;
		this.icon = null;
	}
	
	public Clan(JSONObject json) {
		this.name = (String) json.get("Name");
		this.alliances = this.stringToList((String) json.get("Alliances"));
		this.chat = (boolean) json.get("Chat");
		this.leader = ((String) json.get("Leader")).split("#")[0];
		this.leader_uuid = ((String) json.get("Leader")).split("#")[1];
		this.trusted = this.stringToMap((String) json.get("Trusted"));
		this.members = this.stringToMap((String) json.get("Members"));
		this.base = (json.get("Base") != null ? Location.getByJSONString((String) json.get("Base")) : null);
		this.icon = (json.get("Icon") != null ? OldItemBuilder.translateSimple((String) json.get("Icon")) : null);
		this.homeServer = (json.get("HomeServer") != null ? (String) json.get("HomeServer") : null);
		
		try {
			this.balance = (int) json.get("Balance");
			this.level = (int) json.get("Level");
			this.kills = (int) json.get("Kills");
			this.deaths = (int) json.get("Deaths");
		} catch(ClassCastException ex) {
			this.balance = Math.toIntExact((long) json.get("Balance"));
			this.level = Math.toIntExact((long) json.get("Level"));
			this.kills = Math.toIntExact((long) json.get("Kills"));
			this.deaths = Math.toIntExact((long) json.get("Deaths"));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void addAlliance(Clan clan) {
		this.alliances.add(clan.getName());
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "alliances", Update.Encoding.LIST, this.alliances));
	}
	
	public void removeAlliance(Clan clan) {
		this.removeAlliance(clan.getName());
	}
	
	public void removeAlliance(String name) {
		this.alliances.remove(name);
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "alliances", Update.Encoding.LIST, this.alliances));
	}
	
	public List<String> getAlliances() {
		return alliances;
	}
	
	public List<Clan> getClansWithAlliance() {
		List<Clan> clans = new ArrayList<>();
		
		this.alliances.forEach(name -> {
			clans.add(ClanSystem.getClanManager().getClan(name));
		});
		
		return clans;
	}
	
	public HashMap<String, String> getTrusted() {
		return trusted;
	}
	
	public HashMap<String, String> getMembers() {
		return members;
	}
	
	public HashMap<String, String> getAllMembers() {
		HashMap<String, String> members = new HashMap<>();
		
		members.putAll(getMembers());
		members.putAll(getTrusted());
		members.put(this.leader, this.leader_uuid);
		
		return members;
	}
	
	public String getLeader() {
		return leader;
	}
	
	public String getLeader_uuid() {
		return leader_uuid;
	}
	
	public void setLeader(String leader) {
		this.leader = leader;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "leader", Update.Encoding.STRING, leader));
	}
	
	public void setLeader_uuid(String leader_uuid) {
		this.leader_uuid = leader_uuid;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "leader_uuid", Update.Encoding.STRING, leader_uuid));
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "balance", Update.Encoding.INT, balance));
	}
	
	public boolean isChat() {
		return chat;
	}
	
	public void setChat(boolean chat) {
		this.chat = chat;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "chat", Update.Encoding.BOOLEAN, chat));
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "level", Update.Encoding.INT, level));
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setKills(int kills) {
		this.kills = kills;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "kills", Update.Encoding.INT, kills));
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void setDeaths(int deaths) {
		this.deaths = deaths;
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "deaths", Update.Encoding.INT, deaths));
	}
	
	public boolean isLeader(Player p) {
		return this.isLeader(ClanSystem.getUUID(p));
	}
	
	public boolean isLeader(UUID uID) {
		if(uID == null) return false;
		if(this.leader_uuid == null) return false;
		return this.leader_uuid.equals(uID.toString());
	}
	
	public boolean isTrusted(Player p) {
		return this.isTrusted(ClanSystem.getUUID(p));
	}
	
	public boolean isTrusted(UUID uID) {
		if(uID == null) return false;
		if(this.isLeader(uID)) return true;
		
		for(String uuid : this.trusted.values()) {
			if(uuid.equals(uID.toString())) return true;
		}
		
		return false;
	}
	
	public boolean isMember(Player p) {
		return this.isMember(ClanSystem.getUUID(p));
	}
	
	public boolean isMember(UUID uID) {
		if(uID == null) return false;
		if(this.isLeader(uID) || this.isTrusted(uID)) return true;
		
		for(String uuid : this.members.values()) {
			if(uuid.equals(uID.toString())) return true;
		}
		
		return false;
	}
	
	public String toString() {
		return getJSON().toJSONString();
	}
	
	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		
		json.put("Name", this.name);
		json.put("Balance", this.balance);
		json.put("Alliances", this.listToString(this.alliances));
		json.put("Chat", this.chat);
		json.put("Level", this.level);
		json.put("Kills", this.kills);
		json.put("Deaths", this.deaths);
		json.put("Leader", this.leader + "#" + this.leader_uuid);
		json.put("Trusted", this.mapToString(this.trusted));
		json.put("Members", this.mapToString(this.members));
		json.put("Base", (this.base == null ? null : base.toJSONString()));
		json.put("Icon", (this.icon == null ? null : OldItemBuilder.translateSimple(icon)));
		json.put("HomeServer", this.homeServer);
		
		return json;
	}
	
	private String mapToString(HashMap<String, String> map) {
		String res = "";
		
		for(String key : map.keySet()) {
			res = res + key + "#" + map.get(key) + ";";
		}
		
		if(!res.isEmpty()) res = res.substring(0, res.length() - 1);
		
		return res;
	}
	
	private HashMap<String, String> stringToMap(String code) {
		HashMap<String, String> map = new HashMap<>();
		
		if(code == null || code.isEmpty()) return map;
		
		String[] codes = code.split(";");
		
		for(String info : codes) {
			String name = info.split("#")[0];
			String uuid = info.split("#")[1];
			
			map.put(name, uuid);
		}
		
		return map;
	}
	
	private String listToString(List<String> list) {
		String res = "";
		
		for(String key : list) {
			res = res + key + ";";
		}
		
		if(!res.isEmpty()) res = res.substring(0, res.length() - 1);
		
		return res;
	}
	
	private List<String> stringToList(String code) {
		List<String> list = new ArrayList<>();
		
		if(code == null || code.isEmpty()) return list;
		
		String[] codes = code.split(";");
		
		for(String info : codes) {
			list.add(info);
		}
		
		return list;
	}
	
	public void kick(Player p) {
		this.kick(ClanSystem.getUUID(p));
	}
	
	public void kick(UUID uniqueId) {
		HashMap<String, String> members = new HashMap<>();
		members.putAll(this.getMembers());
		
		this.getMembers().forEach((name, id) -> {
			if(id.equals(uniqueId.toString())) {
				members.remove(name, id);
				ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "members", Update.Encoding.MAP, members));
			}
		});
		
		this.members = members;
		
		HashMap<String, String> trusted = new HashMap<>();
		trusted.putAll(this.getTrusted());
		
		this.getTrusted().forEach((name, id) -> {
			if(id.equals(uniqueId.toString())) {
				trusted.remove(name, id);
				ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "trusted", Update.Encoding.MAP, trusted));
			}
		});
		
		this.trusted = trusted;
		
		if(this.leader_uuid.equals(uniqueId.toString())) {
			this.leader_uuid = null;
			this.leader = null;
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "leader_uuid", Update.Encoding.STRING, this.leader_uuid));
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "leader", Update.Encoding.STRING, this.leader));
		}
		
		LayoutManager.onUpdate();
	}
	
	public void kickAll() {
		this.members.forEach((name, uuid) -> this.kick(UUID.fromString(uuid)));
		this.trusted.forEach((name, uuid) -> this.kick(UUID.fromString(uuid)));
		this.kick(UUID.fromString(this.leader_uuid));
	}
	
	public void add(Player p) {
		if(this.isMember(p)) return;
		
		this.members.put(p.getName(), ClanSystem.getUUID(p).toString());
		
		LayoutManager.onUpdate();
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "members", Update.Encoding.MAP, this.members));
	}
	
	public void setTrusted(Player p, boolean trusted) {
		setTrusted(ClanSystem.getUUID(p), trusted);
	}
	
	public void setTrusted(UUID uniqueId, boolean trusted) {
		if(this.isLeader(uniqueId)) return;
		
		if(trusted) {
			if(this.isTrusted(uniqueId)) return;
			
			String targetName = null;
			
			for(String key : this.members.keySet()) {
				if(this.members.get(key).equals(uniqueId.toString())) {
					targetName = key;
					break;
				}
			}
			
			if(targetName == null) return;
			
			this.members.remove(targetName);
			this.trusted.put(targetName, uniqueId.toString());
			
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "trusted", Update.Encoding.MAP, this.trusted));
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "members", Update.Encoding.MAP, this.members));
		} else {
			if(!this.isTrusted(uniqueId)) return;
			
			String targetName = null;
			
			for(String key : this.trusted.keySet()) {
				if(this.trusted.get(key).equals(uniqueId.toString())) {
					targetName = key;
					break;
				}
			}
			
			if(targetName == null) return;
			
			this.trusted.remove(targetName);
			this.members.put(targetName, uniqueId.toString());
			
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "trusted", Update.Encoding.MAP, this.trusted));
			ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "members", Update.Encoding.MAP, this.members));
		}
	}
	
	public boolean hasAllianceWith(Clan other) {
		for(String alliance : this.alliances) {
			if(alliance.equals(other.getName())) return true;
		}
		
		return false;
	}
	
	public void broadcast(String msg, Player... exceptions) {
		List<String> uuids = new ArrayList<>();
		
		for(Player player : exceptions) {
			uuids.add(ClanSystem.getUUID(player).toString());
		}
		
		this.getOnlinePlayers().forEach(p -> {
			if(!uuids.contains(ClanSystem.getUUID(p).toString())) {
				p.sendMessage(msg);
				uuids.add(ClanSystem.getUUID(p).toString());
			}
		});
		
		if(Options.BUNGEECORD.getBoolean())
			this.getOnlineProxiedPlayers().forEach(p -> {
				if(!uuids.contains(p.getUniqueId().toString())) p.sendMessage(msg);
			});
	}
	
	@Override
	public boolean equals(Object clan) {
		if(!(clan instanceof Clan)) return false;
		return this.name.equals(((Clan) clan).getName());
	}
	
	public int getClanRank() {
		List<Clan> ranked = ClanSystem.getClanManager().getRankList();
		
		int rank = 0;
		for(Clan clan : ranked) {
			if(clan == null) continue;
			rank++;
			if(clan.equals(this)) return rank;
		}
		
		return rank;
	}
	
	public List<Player> getOnlinePlayers() {
		List<Player> online = new ArrayList<>();
		
		if(this.leader_uuid != null && !this.leader_uuid.equalsIgnoreCase("null") && ClanSystem.isOnline(UUID.fromString(this.leader_uuid)))
			online.add(ClanSystem.getPlayer(UUID.fromString(this.leader_uuid)));
		
		this.trusted.forEach((name, uuid) -> {
			if(ClanSystem.isOnline(UUID.fromString(uuid))) online.add(ClanSystem.getPlayer(UUID.fromString(uuid)));
		});
		
		this.members.forEach((name, uuid) -> {
			if(ClanSystem.isOnline(UUID.fromString(uuid))) online.add(ClanSystem.getPlayer(UUID.fromString(uuid)));
		});
		
		return online;
	}
	
	public List<ProxiedPlayer> getOnlineProxiedPlayers() {
		List<ProxiedPlayer> online = new ArrayList<>();
		
		if(this.leader_uuid != null && !this.leader_uuid.equalsIgnoreCase("null") && ClanSystem.isOnProxy(UUID.fromString(this.leader_uuid)))
			online.add(ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer(UUID.fromString(this.leader_uuid)));
		
		this.trusted.forEach((name, uuid) -> {
			if(ClanSystem.isOnProxy(UUID.fromString(uuid)))
				online.add(ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer((UUID.fromString(uuid))));
		});
		
		this.members.forEach((name, uuid) -> {
			if(ClanSystem.isOnProxy(UUID.fromString(uuid)))
				online.add(ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer((UUID.fromString(uuid))));
		});
		
		return online;
	}
	
	public int getSize() {
		return 1 + trusted.size() + members.size();
	}
	
	public boolean rightServer() {
		if(!Options.BUNGEECORD.getBoolean()) return true;
		else if(this.homeServer == null) return true;
		else return this.homeServer.equalsIgnoreCase(ClanSystem.SERVER);
	}
	
	public Location getBase() {
		return base;
	}
	
	public void setBase(Location base) {
		this.base = base;
		
		if(Options.BUNGEECORD.getBoolean()) {
			this.homeServer = ClanSystem.SERVER;
		}
		
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "base", Update.Encoding.CUSTOM_LOCATION, this.base));
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "homeServer", Update.Encoding.STRING, homeServer));
	}
	
	public void setBase(org.bukkit.Location base) {
		this.base = Location.getByLocation(base);
		
		if(Options.BUNGEECORD.getBoolean()) {
			this.homeServer = ClanSystem.SERVER;
		}
		
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "base", Update.Encoding.CUSTOM_LOCATION, this.base));
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "homeServer", Update.Encoding.STRING, homeServer));
	}
	
	public ItemStack getIcon() {
		if(this.icon == null) return null;
		ItemStack icon = this.icon.clone();
		
		icon = OldItemBuilder.getItem(icon.getType(), (icon.getMaxStackSize() > 1 ? icon.getDurability() : 0));
		icon = OldItemBuilder.removeEnchantLore(icon);
		icon = OldItemBuilder.removeStandardLore(icon);
		
		return icon;
	}
	
	public void setIcon(ItemStack icon) {
		this.icon = icon.clone();
		ClanSystem.getInstance().getBungeeCordManager().synchronize(new Update(Update.Type.CLAN, this.name, "homeServer", Update.Encoding.ITEMSTACK, icon));
	}
	
	public void updateName(Player p) {
		if(isLeader(ClanSystem.getUUID(p))) setLeader(p.getName());
		
		if(isTrusted(ClanSystem.getUUID(p))) {
			String name = null;
			
			for(String key : this.trusted.keySet()) {
				if(this.trusted.get(key).equals(ClanSystem.getUUID(p).toString())) {
					name = key;
					break;
				}
			}
			
			if(name == null || this.trusted.containsKey(p.getName())) return;
			
			this.trusted.remove(name);
			this.trusted.put(p.getName(), ClanSystem.getUUID(p).toString());
		}
		
		if(isMember(ClanSystem.getUUID(p))) {
			String name = null;
			
			for(String key : this.members.keySet()) {
				if(this.members.get(key).equals(ClanSystem.getUUID(p).toString())) {
					name = key;
					break;
				}
			}
			
			if(name == null || this.members.containsKey(p.getName())) return;
			
			this.members.remove(name);
			this.members.put(p.getName(), ClanSystem.getUUID(p).toString());
		}
	}
	
	public String getHomeServer() {
		return homeServer;
	}
	
	public void setHomeServer(String homeServer) {
		this.homeServer = homeServer;
	}
}
