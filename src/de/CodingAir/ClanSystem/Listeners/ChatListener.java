package de.CodingAir.ClanSystem.Listeners;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Clan clan = ClanSystem.getClanManager().getClan(p);
		
		final String rankColor;
		if(clan != null && clan.isLeader(p)) rankColor = ClanSystem.getClanManager().getColor(0);
		else if(clan != null && clan.isTrusted(p)) rankColor = ClanSystem.getClanManager().getColor(1);
		else rankColor = ClanSystem.getClanManager().getColor(2);
		
		if(Options.GLOBALCHAT_ENABLED.getBoolean()) {
			if(Options.GLOBALCHAT_ONLY_PREFIX_ENABLED.getBoolean()){
				String prefix = (clan == null ? Options.GLOBALCHAT_ONLY_PREFIX_PREFIX_WITHOUT.getString() : Options.GLOBALCHAT_ONLY_PREFIX_PREFIX_WITH.getString());
				if(clan != null) {
					prefix = prefix.replace("%clanname%", clan.getName());
					prefix = prefix.replace("%rank_color%", rankColor);
					prefix = prefix.replace("%clan_rank%", clan.getClanRank() + "");
					prefix = prefix.replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank()));
				}
				
				p.setDisplayName(prefix + p.getName());
				
			} else {
				String format = (clan == null ? Options.GLOBALCHAT_FORMAT.getString() : Options.GLOBALCHAT_FORMAT_WITH_CLAN.getString());
				
				if(clan != null) {
					format = format.replace("%clanname%", clan.getName());
					format = format.replace("%rank_color%", rankColor);
					format = format.replace("%clan_rank%", clan.getClanRank() + "");
					format = format.replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank()));
				}
				
				format = format.replace("%player%", p.getName());
				format = format.replace("%message%", e.getMessage());
				
				format = format.replace("%", "%%");
				
				e.setFormat(format);
			}
		}
		
		if(clan == null || !e.getMessage().startsWith(LanguageManager.COMMANDS_CLAN_CHAT.getMessage())) return;
		e.setCancelled(true);
		
		if(!clan.isChat()) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CHAT_IS_NOT_ENABLED.getMessage());
			return;
		}
		
		
		String msg = e.getMessage();
		msg = msg.replaceFirst(LanguageManager.COMMANDS_CLAN_CHAT.getMessage(), "");
		
		if(msg.length() == 0) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.HELP_CLAN_CHAT.getMessage());
			return;
		}
		
		String format = Options.CLAN_PRIVATE_CHAT_FORMAT.getString();
		format = format.replace("%player%", p.getName());
		format = format.replace("%message%", msg);
		
		format = format.replace("%clanname%", clan.getName());
		format = format.replace("%rank_color%", rankColor);
		format = format.replace("%clan_rank%", clan.getClanRank() + "");
		format = format.replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank()));
		
		clan.broadcast(format);
	}
	
}
