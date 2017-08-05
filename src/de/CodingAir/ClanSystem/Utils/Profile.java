package de.CodingAir.ClanSystem.Utils;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class Profile {
	private UUID uniqueId;
	private GameProfile gameProfile;
	
	public Profile(UUID uniqueId, GameProfile gameProfile) {
		this.uniqueId = uniqueId;
		this.gameProfile = gameProfile;
	}
	
	public UUID getUniqueId() {
		return uniqueId;
	}
	
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
	public void setGameProfile(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
}
