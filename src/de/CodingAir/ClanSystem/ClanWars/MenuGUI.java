package de.CodingAir.ClanSystem.ClanWars;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class MenuGUI extends GUI {
	
	public MenuGUI(Player p) {
		super(p, "§cClanWars", 9, ClanSystem.getInstance());
		this.setEditableItems(false);
	}
	
	@Override
	public void initialize(Player p) {
		ItemStack ph = OldItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		int slot = 0;
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		
		this.addButton(new ItemButton(slot++, OldItemBuilder.getItem(Material.NETHER_STAR, "§cSearch")) {
			@Override
			public void onClick(InventoryClickEvent e) {
				Clan clan = ClanSystem.getClanManager().getClan(p);
				
				if(clan == null) {
					p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_NO_CLAN.getMessage(p));
					return;
				}
				
				if(ClanWars.getInstance().isSearching(clan)) {
					ClanWars.getInstance().setSearching(clan, false);
					clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage(p).replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.CLAN_WARS_CANCELLED_SEARCH.getMessage(p));
					return;
				}
				
				ClanWars.getInstance().setSearching(clan, true);
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage(p).replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.CLAN_WARS_SEARCH.getMessage(p));
			}
		}.setOnlyLeftClick(true).setClickSound(Sound.LEVEL_UP.bukkitSound()));
		
		this.setItem(slot++, ph);
		
		this.setItem(slot++, OldItemBuilder.getItem(Material.DIAMOND_SWORD, "§3Current§8: §b" + ClanWars.getInstance().getSearchingClans().size(), true));
		
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
	}
}
