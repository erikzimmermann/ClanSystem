package de.CodingAir.ClanSystem.Managers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum LanguageManager {
	ERROR("§8[§4§lERROR§8] §cCheck the 'Language.yml' at '%place%'!"),
	
	PREFIX(".General.Prefix"),
	SUBPREFIX(".General.SubPrefix"),
	
	ONLY_FOR_PLAYERS(".General.OnlyForPlayers"),
	NO_PERMISSION(".General.No_Permission"),
	NO_LEADER(".General.No_Leader"),
	
	ON(".General.Statement_On"),
	OFF(".General.Statement_Off"),
	
	NUMBER_REQUIRED(".General.Number_Required"),
	
	COMMANDS_HELP(".Commands.SubCommands.Help"),
	COMMANDS_CREATE(".Commands.SubCommands.Create"),
	COMMANDS_DELETE(".Commands.SubCommands.Delete"),
	COMMANDS_LEAVE(".Commands.SubCommands.Leave"),
	COMMANDS_INVITE(".Commands.SubCommands.Invite"),
	COMMANDS_ACCEPT(".Commands.SubCommands.Accept"),
	COMMANDS_DECLINE(".Commands.SubCommands.Decline"),
	COMMANDS_KICK(".Commands.SubCommands.Kick"),
	COMMANDS_ALLIANCE(".Commands.SubCommands.Alliance"),
	COMMANDS_NEUTRAL(".Commands.SubCommands.Neutral"),
	COMMANDS_INFO(".Commands.SubCommands.Info"),
	COMMANDS_CHAT(".Commands.SubCommands.Chat"),
	COMMANDS_DEMOTE(".Commands.SubCommands.Demote"),
	COMMANDS_PROMOTE(".Commands.SubCommands.Promote"),
	COMMANDS_LEADER(".Commands.SubCommands.Leader"),
	COMMANDS_SET_BASE(".Commands.SubCommands.SetBase"),
	COMMANDS_BASE(".Commands.SubCommands.Base"),
	COMMANDS_CLAN_CHAT(".Commands.SubCommands.ClanChat"),
	COMMANDS_DEPOSIT(".Commands.SubCommands.Deposit"),
	COMMANDS_WITHDRAW(".Commands.SubCommands.Withdraw"),
	
	HELP_HELP(".Commands.Help.Help"),
	HELP_CREATE(".Commands.Help.Create"),
	HELP_INVITE(".Commands.Help.Invite"),
	HELP_INVITE_ACCEPT(".Commands.Help.Accept"),
	HELP_INVITE_DECLINE(".Commands.Help.Decline"),
	HELP_KICK(".Commands.Help.Kick"),
	HELP_ALLIANCE(".Commands.Help.Alliance"),
	HELP_ALLIANCE_ACCEPT(".Commands.Help.Alliance_Accept"),
	HELP_ALLIANCE_DECLINE(".Commands.Help.Alliance_Decline"),
	HELP_NEUTRAL(".Commands.Help.Neutral"),
	HELP_INFO(".Commands.Help.Info"),
	HELP_CHAT(".Commands.Help.Chat"),
	HELP_DEMOTE(".Commands.Help.Demote"),
	HELP_PROMOTE(".Commands.Help.Promote"),
	HELP_LEADER(".Commands.Help.Leader"),
	HELP_CLAN_CHAT(".Commands.Help.ClanChat"),
	
	ERROR_UNKNOWN_COMMAND(".Commands.Error.Unknown_Command"),
	ERROR_CLAN_ALREADY_EXISTS(".Commands.Error.Clan_Already_Exists"),
	ERROR_NO_CLAN(".Commands.Error.No_Clan"),
	ERROR_CLAN_NOT_EXISTS(".Commands.Error.Clan_Not_Exists"),
	ERROR_LEADERS_CAN_NOT_LEAVE(".Commands.Error.Leaders_Can_Not_Leave_Clans"),
	ERROR_CLAN_NOT_DELETED(".Commands.Error.Clan_Not_Deleted"),
	ERROR_CLAN_NOT_LEAVED(".Commands.Error.Clan_Not_Leaved"),
	ERROR_PLAYER_IS_OFFLINE(".Commands.Error.Player_Is_Offline"),
	ERROR_PLAYER_NOT_EXISTS(".Commands.Error.Player_Not_Exists"),
	ERROR_NO_INVITE(".Commands.Error.No_Invite"),
	ERROR_NO_ALLIANCE_INVITE(".Commands.Error.No_Alliance_Invite"),
	ERROR_SELF_INVITATION(".Commands.Error.Self_Invite"),
	ERROR_SELF_KICK(".Commands.Error.Self_Kick"),
	ERROR_PLAYER_IS_ALREADY_IN_A_CLAN(".Commands.Error.Player_Is_Already_In_A_Clan"),
	ERROR_THIS_PLAYER_IS_ALREADY_IN_A_CLAN(".Commands.Error.This_Player_Is_Already_In_A_Clan"),
	ERROR_PLAYER_HAS_ALREADY_A_INVITATION(".Commands.Error.Player_Has_Already_A_Invitation"),
	ERROR_OTHER_CLAN(".Commands.Error.Other_Clan"),
	ERROR_LEADERS_CAN_NOT_BE_KICKED(".Commands.Error.Leaders_Can_Not_Be_Kicked"),
	ERROR_CHAT_IS_NOT_ENABLED(".Commands.Error.Chat_Is_Not_Enabled"),
	ERROR_LEADER_NOT_ONLINE(".Commands.Error.Leader_Is_Not_Online"),
	ERROR_ALLIANCE_BETWEEN_CLANS(".Commands.Error.Alliance_Between_Clans"),
	ERROR_ALREADY_IN_ALLIANCE(".Commands.Error.Already_In_Alliance"),
	ERROR_SAME_CLAN(".Commands.Error.Same_Clan"),
	ERROR_NO_ALLIANCE(".Commands.Error.No_Alliance"),
	ERROR_ALREADY_LEADER(".Commands.Error.Already_Leader"),
	ERROR_ALREADY_TRUSTED(".Commands.Error.Already_Trusted"),
	ERROR_ALREADY_MEMBER(".Commands.Error.Already_Member"),
	ERROR_DEMOTE_WRONG_COMMAND(".Commands.Error.Demote_Wrong_Command"),
	ERROR_UNKNOWN_PAGE(".Commands.Error.Unknown_Page"),
	ERROR_CLAN_NAME_TO_LONG(".Commands.Error.Clan_Name_To_Long"),
	ERROR_CLAN_MAX_ALLIANCES(".Commands.Error.Clan_Max_Alliances"),
	ERROR_CLAN_ALREADY_FULL(".Commands.Error.Clan_Already_Full"),
	ERROR_THIS_CLAN_IS_ALREADY_FULL(".Commands.Error.This_Clan_Is_Full"),
	ERROR_PLAYER_NOT_KICKED(".Commands.Error.Player_Not_Kicked"),
	ERROR_NO_CLAN_BASE(".Commands.Error.No_Clan_Base"),
	ERROR_OWN_CLAN(".Commands.Error.Own_Clan"),
	ERROR_CLAN_NOT_NEUTRALIZED(".Commands.Error.Clan_Not_Neutralized"),
	ERROR_NEEDED_ITEM_IN_HAND(".Commands.Error.Item_In_Hand_Required"),
	ERROR_LEADER_NOT_SET(".Commands.Error.Leader_Not_Set"),
	ERROR_TELEPORT_CANCELED(".Commands.Error.Teleport_Cancel"),
	ERROR_NOT_ENOUGH_LEVEL(".Commands.Error.Not_Enough_Level"),
	ERROR_NOT_ENOUGH_MONEY(".Commands.Error.Not_Enough_Money"),
	ERROR_WRONG_SERVER(".Commands.Error.Wrong_Server"),
	ERROR_CANNOT_INTERACT_WITH_YOURSELF(".Commands.Error.Interact_With_Yourself"),
	ERROR_PLAYER_NOT_REGISTERED(".Commands.Error.Player_Not_Registered"),
	ERROR_CLIENT_NOT_CONNECTED(".Commands.Error.Client_Not_Connected"),
	ERROR_COMMANDS_BLOCKED(".Commands.Error.Commands_Blocked"),
	
	SUCCESS_CLAN_CREATED(".Commands.Success.Clan_Created"),
	SUCCESS_CLAN_DELETED(".Commands.Success.Clan_Deleted"),
	SUCCESS_JOIN(".Commands.Success.Join"),
	SUCCESS_LEAVE(".Commands.Success.Leave"),
	SUCCESS_CLAN_CHAT_ENABLED(".Commands.Success.Clan_Chate_Enabled"),
	SUCCESS_CLAN_CHAT_DISABLED(".Commands.Success.Clan_Chate_Disabled"),
	SUCCESS_CLAN_JOINED_ALLIANCE(".Commands.Success.Joined_Alliance"),
	SUCCESS_ALLIANCE_REMOVED(".Commands.Success.Alliance_Removed"),
	SUCCESS_NEW_LEADER(".Commands.Success.New_Leader"),
	SUCCESS_PROMOTE(".Commands.Success.Promote"),
	SUCCESS_DEMOTE(".Commands.Success.Demote"),
	SUCCESS_BASE_SET(".Commands.Success.Base_Set"),
	SUCCESS_TELEPORT_TO_BASE(".Commands.Success.Base_Teleport"),
	SUCCESS_PLAYER_DEPOSIT(".Commands.Success.Player_Deposit"),
	SUCCESS_PLAYER_WITHDRAW(".Commands.Success.Player_Withdraw"),
	
	INFO_OUTPUT(".Commands.Info.Output"),
	INFO_FORMAT_MEMBER_LIST(".Commands.Info.Format.Member_List"),
	INFO_FORMAT_TRUSTED_LIST(".Commands.Info.Format.Trusted_List"),
	INFO_FORMAT_EMPTY_LIST(".Commands.Info.Format.Empty_List"),
	
	HELP_OUTPUT_PAGE_1(".Commands.Help.Output.Page1"),
	HELP_OUTPUT_PAGE_2(".Commands.Help.Output.Page2"),
	
	CLAN_DELETED_BY_LEADER(".Clan.Deleted_By_Leader"),
	CLAN_PLAYER_LEAVE(".Clan.Player_Leave"),
	CLAN_PLAYER_JOIN(".Clan.Player_Join"),
	CLAN_PLAYER_INVITE(".Clan.Invite_Player_Message"),
	CLAN_PLAYER_INVITE_NOTIFY(".Clan.Invite_Clan_Message"),
	CLAN_INVITATION_DECLINE_CLAN(".Clan.Invitation_Decline_Clan"),
	CLAN_INVITATION_DECLINE_PLAYER(".Clan.Invitation_Decline_Player"),
	CLAN_PREFIX(".Clan.Prefix"),
	CLAN_KICK(".Clan.Kick_Player"),
	CLAN_ALLIANCE_TARGET(".Clan.Alliance_Target_Message"),
	CLAN_ALLIANCE_CLAN(".Clan.Alliance_Clan_Message"),
	CLAN_CLAN_JOINED_ALLIANCE(".Clan.Clan_Joined_Alliance"),
	CLAN_ALLIANCE_DECLINED_ClAN(".Clan.Alliance_Invite_Decline_Clan"),
	CLAN_ALLIANCE_DECLINED_TARGET(".Clan.Alliance_Invite_Decline_Target"),
	CLAN_TAXES_COLLECT(".Clan.Taxes_Collect"),
	CLAN_TAXES_COLLECT_FAILURE(".Clan.Taxes_Collect_Failure"),
	
	CLAN_WARS_SEARCH(".ClanWars.GUI.Search"),
	CLAN_WARS_CANCELLED_SEARCH(".ClanWars.GUI.Cancelled_Searching"),
	
	GUI_YES(".GUI.Statement_Yes"),
	GUI_NO(".GUI.Statement_No"),
	GUI_DELETE_APPLY(".GUI.Delete_Apply"),
	GUI_CLICK(".GUI.Click"),
	GUI_LEAVE(".GUI.Leave_Apply"),
	GUI_KICK_APPLY(".GUI.Kick_Apply"),
	GUI_STATE(".GUI.State"),
	GUI_ENABLED(".GUI.Statement_Enabled"),
	GUI_DISABLED(".GUI.Statement_Disabled"),
	GUI_TOGGLE(".GUI.Toggle"),
	GUI_TOGGLED(".GUI.Toggled"),
	GUI_CONFIGURE(".GUI.Configure"),
	GUI_CONFIGURED(".GUI.Configured"),
	GUI_ENTER_A_NUMBER(".GUI.Enter_A_Number"),
	GUI_ENTER_A_NAME(".GUI.Enter_A_Name"),
	GUI_MEMBERS(".GUI.Members"),
	GUI_BACK(".GUI.Back"),
	GUI_MORE_OPTIONS(".GUI.More_Options"),
	GUI_KICK(".GUI.Kick"),
	GUI_BASE(".GUI.Base"),
	GUI_ALLIANCES(".GUI.Alliances"),
	GUI_OPTIONS(".GUI.Options"),
	GUI_PROMOTE(".GUI.Promote"),
	GUI_CONFIRM(".GUI.Confirm"),
	GUI_DEMOTE(".GUI.Demote"),
	GUI_INVITE_MEMBERS(".GUI.Invite_Members"),
	GUI_NAME(".GUI.Name"),
	GUI_CLAN(".GUI.Clan"),
	GUI_CLAN_ICON(".GUI.Clan_Icon"),
	GUI_CHARACTERS(".GUI.Characters"),
	GUI_GLOBALCHAT(".GUI.Globalchat"),
	GUI_PREFIX_AND_SUFFIX(".GUI.Prefix_And_Suffix"),
	GUI_TRUSTED_PERMISSIONS(".GUI.Trusted_Permissions"),
	GUI_CLAN_NAME_LENGTH(".GUI.Clan_Name_Length"),
	GUI_FOUND_ALLIANCES(".GUI.Found_Alliances"),
	GUI_NEUTRAL_APPLY(".GUI.Neutral_Apply"),
	GUI_PRIVATE_CHAT(".GUI.Private_Chat"),
	GUI_CLAN_TOGGLE(".GUI.Clan_Toggled"),
	GUI_CLAN_CONFIGURED(".GUI.Clan_Configured"),
	GUI_SET_ICON(".GUI.Set_Icon"),
	GUI_SET_LEADER(".GUI.Set_Leader"),
	GUI_LEADER(".GUI.Leader"),
	GUI_CLICK_FOR_NEW_LEADER(".GUI.Click_For_Set_Leader"),
	GUI_LEADER_APPLY(".GUI.Leader_Apply"),
	GUI_DELETE(".GUI.Delete"),
	GUI_CLAN_APPLY(".GUI.Clan_Apply"),
	GUI_LENGTH(".GUI.Length"),
	GUI_PRICE(".GUI.Price"),
	GUI_COSTS(".GUI.Costs"),
	GUI_CANCEL(".GUI.Cancel"),
	GUI_MONEY(".GUI.Money"),
	GUI_LEVEL(".GUI.Level"),
	GUI_CURRENCY(".GUI.Currency"),
	GUI_DEPOSIT_RIGHT_CLICK_LEVEL(".GUI.Deposit_Right_Click_Level"),
	GUI_DEPOSIT_LEFT_CLICK_LEVEL(".GUI.Deposit_Left_Click_Level");
	
	private String path;
	
	LanguageManager(String message) {
		this.path = message;
	}
	
	public String getMessage() {
		String message = FileManager.LANGUAGE.getFile().getConfig().getString(FileManager.LANGUAGE.getFile().getConfig().getString("Current_Language", "English") + "." + path, null);
		return (message == null) ? null : ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public String getMessage(Player p) {
		String message = FileManager.LANGUAGE.getFile().getConfig().getString(FileManager.LANGUAGE.getFile().getConfig().getString("Current_Language", "English") + "." + path, null);
		message =  (message == null) ? null : ChatColor.translateAlternateColorCodes('&', message);
		return replacePlaceholders(p, message);
	}
	
	public List<String> getStringList() {
		List<String> out = new ArrayList<>();
		List<String> list = FileManager.LANGUAGE.getFile().getConfig().getStringList(FileManager.LANGUAGE.getFile().getConfig().getString("Current_Language", "English") + "." + path);
		
		list.forEach(s -> out.add(ChatColor.translateAlternateColorCodes('&', s)));
		return out;
	}
	
	public static String replacePlaceholders(Player p, String message) {
		return PlaceholderAPI.setPlaceholders(p, message);
	}
}
