package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LayoutManager {
	
	public static void onUpdate(boolean forceUpdate){
		if(!Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() && forceUpdate) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				Scoreboard board = p.getScoreboard();
				
				List<Team> teams = new ArrayList<>();
				teams.addAll(board.getTeams());
				
				teams.forEach(team -> {
					List<String> entries = new ArrayList<>();
					entries.addAll(team.getEntries());
					
					entries.forEach(entry -> team.removeEntry(entry));
					
					team.unregister();
				});
			}
			return;
		} else if(!Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean()) return;
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			Scoreboard board = p.getScoreboard();
			
			ClanSystem.getClanManager().getClans().forEach(clan -> {
				String rank = clan.getClanRank() + "";
				
				if(clan.getClanRank() < 1000) rank = "0" + rank;
				if(clan.getClanRank() < 100) rank = "0" + rank;
				if(clan.getClanRank() < 10) rank = "0" + rank;
				
				if(board.getTeam(rank + clan.getName()) == null) {
					board.registerNewTeam(rank + clan.getName());
				}
				
				Team team = board.getTeam(rank + clan.getName());
				
				String prefix = Options.PLAYER_LAYOUT_PREFIX_WITH_CLAN.getString();
				String suffix = Options.PLAYER_LAYOUT_SUFFIX_WITH_CLAN.getString();
				
				if(prefix != null) {
					prefix = prefix.replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clan_rank%", clan.getClanRank() + "");
					if(prefix.length() <= 16) team.setPrefix(prefix);
					else ClanSystem.getInstance().getLogger().log(Level.WARNING, "Your Player-Prefix is to long!");
				}
				
				if(suffix != null) {
					suffix = suffix.replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clan_rank%", clan.getClanRank() + "");
					if(suffix.length() <= 16) team.setSuffix(suffix);
					else ClanSystem.getInstance().getLogger().log(Level.WARNING, "Your Player-Suffix is to long!");
				}
				
				Team noClan = board.getTeam("99999NoClan");
				
				clan.getOnlinePlayers().forEach(all -> {
					if(noClan != null && noClan.hasEntry(all.getName())) noClan.removeEntry(all.getName());
					
					if(!team.hasEntry(all.getName())) team.addEntry(all.getName());
				});
			});
			
			if(board.getTeam("99999NoClan") == null) board.registerNewTeam("99999NoClan");
			Team noClan = board.getTeam("99999NoClan");
			
			String prefix = Options.PLAYER_LAYOUT_PREFIX.getString();
			String suffix = Options.PLAYER_LAYOUT_SUFFIX.getString();
			
			if(prefix != null && prefix.length() <= 16) noClan.setPrefix(prefix);
			if(suffix != null && suffix.length() <= 16) noClan.setSuffix(suffix);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(ClanSystem.getClanManager().getClan(all) == null) {
					List<Team> teams = new ArrayList<>();
					teams.addAll(board.getTeams());
					
					teams.forEach(team -> {
						if(team != null && !team.getName().equals("99999NoClan") && ClanSystem.getClanManager().getClan(team.getName().substring(4, team.getName().length())) == null)
							team.unregister();
					});
					
					if(!noClan.hasEntry(all.getName())) noClan.addEntry(all.getName());
				}
			}
			
			List<String> left = new ArrayList<>();
			Bukkit.getOnlinePlayers().forEach(all -> left.add(all.getName()));
			board.getTeams().forEach(team -> left.removeAll(team.getEntries()));
			
			left.forEach(all -> {
				Player player = Bukkit.getPlayer(all);
				
				if(player != null) {
					Clan clan = ClanSystem.getClanManager().getClan(player);
					
					if(clan != null){
						String rank = clan.getClanRank() + "";
						
						if(clan.getClanRank() < 1000) rank = "0" + rank;
						if(clan.getClanRank() < 100) rank = "0" + rank;
						if(clan.getClanRank() < 10) rank = "0" + rank;
						
						if(board.getTeam(rank + clan.getName()) == null) {
							board.registerNewTeam(rank + clan.getName());
						}
						
						Team team = board.getTeam(rank + clan.getName());
						
						String clanPrefix = Options.PLAYER_LAYOUT_PREFIX_WITH_CLAN.getString();
						String clanSuffix = Options.PLAYER_LAYOUT_SUFFIX_WITH_CLAN.getString();
						
						if(clanPrefix != null) {
							clanPrefix = clanPrefix.replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clan_rank%", clan.getClanRank() + "");
							if(clanPrefix.length() <= 16) team.setPrefix(clanPrefix);
							else ClanSystem.getInstance().getLogger().log(Level.WARNING, "Your Player-Prefix is to long!");
						}
						
						if(clanSuffix != null) {
							clanSuffix = clanSuffix.replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clan_rank%", clan.getClanRank() + "");
							if(clanSuffix.length() <= 16) team.setSuffix(clanSuffix);
							else ClanSystem.getInstance().getLogger().log(Level.WARNING, "Your Player-Suffix is to long!");
						}
						
						team.addEntry(player.getName());
					} else {
						noClan.addEntry(p.getName());
					}
				}
			});
			
			
			p.setScoreboard(board);
		}
	}
	
	public static void onUpdate() {
		onUpdate(false);
	}
}
