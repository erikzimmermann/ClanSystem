package de.CodingAir.ClanSystem.Commands;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Options;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class ClanWarsCMD implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if(!ClanSystem.isInited()) return false;
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(LanguageManager.SUBPREFIX.getMessage(null) + LanguageManager.ONLY_FOR_PLAYERS.getMessage(null));
			return false;
		}
		
		Player p = (Player) sender;
		
		if(!ClanSystem.getInstance().getBungeeCordManager().isConnected() && Options.BUNGEECORD.getBoolean()) {
			p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_COMMANDS_BLOCKED.getMessage(p));
			return false;
		}
		
		if(!ClanSystem.isRegistered(p)) {
			p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_PLAYER_NOT_REGISTERED.getMessage(p));
			return false;
		}
		
		if(!p.hasPermission("ClanSystem.ClanWars")) {
			p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.NO_PERMISSION.getMessage(p));
			return false;
		}
		
		
		
		
		
		return false;
	}
}
