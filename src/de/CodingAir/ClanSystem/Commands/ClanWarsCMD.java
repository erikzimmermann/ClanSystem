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
			sender.sendMessage(LanguageManager.SUBPREFIX.getMessage() + LanguageManager.ONLY_FOR_PLAYERS.getMessage());
			return false;
		}
		
		Player p = (Player) sender;
		
		if(!ClanSystem.getInstance().getBungeeCordManager().isConnected() && Options.BUNGEECORD.getBoolean()) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_COMMANDS_BLOCKED.getMessage());
			return false;
		}
		
		if(!ClanSystem.isRegistered(p)) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_PLAYER_NOT_REGISTERED.getMessage());
			return false;
		}
		
		if(!p.hasPermission("ClanSystem.ClanWars")) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
			return false;
		}
		
		
		
		
		
		return false;
	}
}
