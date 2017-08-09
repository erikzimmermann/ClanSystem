package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.v1_6.CodingAPI.Time.TimeFetcher;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TaxManager {
	
	public static void save() {
		FileManager.ConfigFile file = FileManager.DATA.getFile();
		FileConfiguration config = file.getConfig();
		
		int days = config.getInt("Time.Days", -1);
		
		int day = config.getInt("Time.Day", -1);
		int oDay = TimeFetcher.getDay();
		
		if(day != oDay) {
			config.set("Time.Day", oDay);
			config.set("Time.Days", days + 1);
			
			file.saveConfig();
			newDay();
		}
	}
	
	private static void newDay() {
		if(!Options.TAXES_ENABLED.getBoolean()) return;
		
		FileManager.ConfigFile file = FileManager.DATA.getFile();
		FileConfiguration config = file.getConfig();
		
		int days = config.getInt("Time.Days", -1);
		int taxPeriod = Options.TAXES_PERIOD.getInt();
		
		if(days >= taxPeriod) {
			config.set("Time.Days", 0);
			file.saveConfig();
		} else {
			config.set("Time.Days", days + 1);
		}
	}
	
	private static void collectTaxes() {
		List<Clan> clans = new ArrayList<>();
		clans.addAll(ClanSystem.getClanManager().getClans());
		
		clans.forEach(clan -> {
			int taxes = Options.TAXES_PER_MEMBER.getInt() * clan.getSize();
			
			if(clan.getBalance() < taxes) {
				clan.broadcast(ClanManager.getClanPrefix(clan)+ LanguageManager.CLAN_TAXES_COLLECT_FAILURE.getMessage(null));
				ClanSystem.getClanManager().removeClan(clan);
				LayoutManager.onUpdate();
			} else {
				clan.setBalance(clan.getBalance() - taxes);
				clan.broadcast(ClanManager.getClanPrefix(clan)+ LanguageManager.CLAN_TAXES_COLLECT.getMessage(null).replace("%amount%", taxes+""));
			}
		});
	}
}
