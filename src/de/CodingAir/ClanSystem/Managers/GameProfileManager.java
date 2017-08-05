package de.CodingAir.ClanSystem.Managers;

import com.mojang.authlib.GameProfile;
import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Profile;
import de.CodingAir.v1_4.CodingAPI.Database.MySQL;
import de.CodingAir.v1_4.CodingAPI.Database.QueryUpdate;
import de.CodingAir.v1_4.CodingAPI.Player.Data.GameProfile.GameProfileUtils;
import de.CodingAir.v1_4.CodingAPI.Tools.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameProfileManager {
	private List<Profile> profiles = new ArrayList<>();
	
	public void load() {
		if(Bukkit.getOnlineMode()) return;
		
		MySQL database = ClanSystem.getInstance().getMySQL();
		if(database == null || !database.isConnected()) return;
		
		try {
			ResultSet set = database.query("SELECT * from PlayerData");
			while(set.next()) {
				UUID uniqueId = UUID.fromString(set.getString("UniqueId"));
				GameProfile gameProfile = GameProfileUtils.gameProfileFromJSON(set.getString("GameProfile"));
				
				this.profiles.add(new Profile(uniqueId, gameProfile));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		if(Bukkit.getOnlineMode()) return;
		
		MySQL database = ClanSystem.getInstance().getMySQL();
		if(database == null || !database.isConnected()) return;
		
		List<Profile> profiles = new ArrayList<>();
		profiles.addAll(this.profiles);
		
		profiles.forEach(profile -> {
			if(profile != null && profile.getUniqueId() != null) {
				QueryUpdate update = new QueryUpdate(database, "PlayerData", "UniqueId", profile.getUniqueId().toString());
				
				update.addEntry("UniqueId", profile.getUniqueId().toString());
				update.addEntry("GameProfile", GameProfileUtils.gameProfileToString(profile.getGameProfile()));
				
				update.send();
			}
			
		});
	}
	
	public Profile getProfile(Player p) {
		if(ClanSystem.getUUID(p) == null) return null;
		
		for(Profile profile : profiles) {
			if(profile == null || profile.getUniqueId() == null) continue;
			if(profile.getUniqueId().toString().equals(ClanSystem.getUUID(p).toString())) return profile;
		}
		
		return null;
	}
	
	public GameProfile getGameProfile(Player p) {
		if(Bukkit.getOnlineMode()) {
			return GameProfileUtils.getGameProfile(p);
		} else {
			Profile profile = getProfile(p);
			if(profile == null) return null;
			return profile.getGameProfile();
		}
	}
	
	public void update(Player p) {
		Profile profile = getProfile(p);
		
		if(profile != null && profile.getGameProfile() != null) return;
		
		Bukkit.getScheduler().runTaskAsynchronously(ClanSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				UUID uniqueId = ClanSystem.getUUID(p);
				GameProfileUtils.loadGameProfile(uniqueId, new Callback<GameProfile>() {
					@Override
					public void accept(GameProfile gameProfile) {
						Profile profile = getProfile(p);
						
						if(profile == null) {
							profile = new Profile(uniqueId, gameProfile);
							profiles.add(profile);
						} else {
							if(gameProfile != null) profile.setGameProfile(gameProfile);
						}
					}
				});
			}
		});
	}
	
	public void onEnable() {
		Bukkit.getOnlinePlayers().forEach(p -> update(p));
	}
}
