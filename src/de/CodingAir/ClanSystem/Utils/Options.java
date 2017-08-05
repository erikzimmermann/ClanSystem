package de.CodingAir.ClanSystem.Utils;

import de.CodingAir.ClanSystem.Managers.FileManager;
import org.bukkit.ChatColor;

public enum Options {
	BUNGEECORD("Clans.BungeeCord"),
	BUNGEECORD_PORT("Clans.Synchronization.Server.Port"),
	
	ECONOMY_ENABLED("Clans.Economy.Enabled"),
	
	TAXES_ENABLED("Clans.Economy.Taxes.Enabled"),
	TAXES_PER_MEMBER("Clans.Economy.Taxes.Taxes_Per_Member"),
	TAXES_PERIOD("Clans.Economy.Taxes.Days"),
	
	PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED("Clans.Player.Prefix_Suffix.Enabled"),
	PLAYER_LAYOUT_PREFIX_WITH_CLAN("Clans.Player.Prefix_Suffix.With_Clan.Prefix"),
	PLAYER_LAYOUT_SUFFIX_WITH_CLAN("Clans.Player.Prefix_Suffix.With_Clan.Suffix"),
	PLAYER_LAYOUT_PREFIX("Clans.Player.Prefix_Suffix.Without_Clan.Prefix"),
	PLAYER_LAYOUT_SUFFIX("Clans.Player.Prefix_Suffix.Without_Clan.Suffix"),
	
	GLOBALCHAT_ENABLED("Clans.Player.Globalchat.Enabled"),
	GLOBALCHAT_FORMAT("Clans.Player.Globalchat.Format_Without_Clan"),
	GLOBALCHAT_FORMAT_WITH_CLAN("Clans.Player.Globalchat.Format_With_Clan"),
	GLOBALCHAT_ONLY_PREFIX_ENABLED("Clans.Player.Globalchat.OnlyPrefix.Enabled"),
	GLOBALCHAT_ONLY_PREFIX_PREFIX_WITH("Clans.Player.Globalchat.OnlyPrefix.Prefix.With_Clan"),
	GLOBALCHAT_ONLY_PREFIX_PREFIX_WITHOUT("Clans.Player.Globalchat.OnlyPrefix.Prefix.Without_Clan"),
	
	CLAN_RANK_COLOR_LEADER("Clans.Clan.Rank_Colors.Leader"),
	CLAN_RANK_COLOR_TRUSTED("Clans.Clan.Rank_Colors.Trusted"),
	CLAN_RANK_COLOR_MEMBER("Clans.Clan.Rank_Colors.Member"),
	
	CLAN_NAME_LENGTH("Clans.Clan.Max_Name_Length"),
	CLAN_SIZE("Clans.Clan.Max_Size"),
	CLAN_MAX_ALLIANCES("Clans.Clan.Max_Alliances"),
	CLAN_BASES("Clans.Clan.Bases"),
	
	CLAN_PRIVATE_CHAT_FORMAT("Clans.Clan.PrivateChat.Format"),
	
	TRUSTED_PERMISSIONS_KICK("Clans.Permissions.Trusted.Kick"),
	TRUSTED_PERMISSIONS_INVITE("Clans.Permissions.Trusted.Invite"),
	TRUSTED_PERMISSIONS_ALLIANCE("Clans.Permissions.Trusted.Alliance"),
	TRUSTED_PERMISSIONS_NEUTRAL("Clans.Permissions.Trusted.Neutral"),
	TRUSTED_PERMISSIONS_TOGGLE_CHAT("Clans.Permissions.Trusted.ToggleChat"),
	TRUSTED_PERMISSIONS_PROMOTE("Clans.Permissions.Trusted.Promote"),
	TRUSTED_PERMISSIONS_DEMOTE("Clans.Permissions.Trusted.Demote"),
	TRUSTED_PERMISSIONS_SET_BASE("Clans.Permissions.Trusted.Set_Base"),
	TRUSTED_PERMISSIONS_SET_ICON("Clans.Permissions.Trusted.Set_Icon"),
	TRUSTED_PERMISSIONS_WITHDRAW("Clans.Permissions.Trusted.Withdraw"),
	
	TELEPORT_TIME("Clans.Teleport.Teleport_Time"),
	TELEPORT_CAN_MOVE("Clans.Teleport.Allow_Move"),
	
	TOP_CLANS_COLORS_1("Clans.TopClans.Colors.1"),
	TOP_CLANS_COLORS_2("Clans.TopClans.Colors.2"),
	TOP_CLANS_COLORS_3("Clans.TopClans.Colors.3"),
	TOP_CLANS_COLORS_4("Clans.TopClans.Colors.4"),
	TOP_CLANS_COLORS_5("Clans.TopClans.Colors.5"),
	TOP_CLANS_COLORS_DEFAULT("Clans.TopClans.Colors.Default");
	
	private String path;
	
	Options(String path) {
		this.path = path;
	}
	
	public boolean getBoolean() {
		return FileManager.CONFIG.getFile().getConfig().getBoolean(path, false);
	}
	
	public int getInt() {
		if(this.equals(CLAN_SIZE) || this.equals(CLAN_MAX_ALLIANCES)) return 21;
		return FileManager.CONFIG.getFile().getConfig().getInt(path, 0);
	}
	
	public String getString() {
		String message = FileManager.CONFIG.getFile().getConfig().getString(path, null);
		return (message == null) ? null : ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public void set(Object value) {
		if(this.equals(CLAN_SIZE) || this.equals(CLAN_MAX_ALLIANCES)) return;
		FileManager.CONFIG.getFile().getConfig().set(path, value);
		FileManager.CONFIG.getFile().saveConfig();
	}
}
