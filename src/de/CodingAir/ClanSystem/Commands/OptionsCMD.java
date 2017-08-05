package de.CodingAir.ClanSystem.Commands;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.GUIs.ClanGUI;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.v1_4.CodingAPI.Time.Timer;
import de.CodingAir.v1_4.CodingAPI.Tools.Callback;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OptionsCMD implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
		
		if(!p.hasPermission("ClanSystem.Options")) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
			return false;
		}
		
		if(args.length == 0) ClanGUI.OPTIONS.open(p);
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("FetchData")) {
				Timer timer = new Timer();
				timer.start();
				
				p.sendMessage(LanguageManager.PREFIX.getMessage() + "Fetching...");
				
				ClanSystem.getClanManager().fetch(new Callback<List<Clan>>() {
					@Override
					public void accept(List<Clan> clans) {
						if(clans == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + "Could not fetch clan data.");
						} else {
							ClanSystem.getClanManager().setClans(clans);
							timer.stop();
							p.sendMessage(LanguageManager.PREFIX.getMessage() + "Fetching completed. ("+timer.getLastStoppedTime()+"s)");
						}
					}
				});
			}
		}
		
		return false;
	}
}
