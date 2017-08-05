package de.CodingAir.ClanSystem.ClanWars;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.GUI;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_4.CodingAPI.Server.Sound;
import de.CodingAir.v1_4.CodingAPI.Tools.ItemBuilder;
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
		ItemStack ph = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		int slot = 0;
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		
		this.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.NETHER_STAR, "§cSearch")) {
			@Override
			public void onClick(InventoryClickEvent e) {
				Clan clan = ClanSystem.getClanManager().getClan(p);
				
				if(clan == null) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_NO_CLAN.getMessage());
					return;
				}
				
				if(ClanWars.getInstance().isSearching(clan)) {
					ClanWars.getInstance().setSearching(clan, false);
					clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.CLAN_WARS_CANCELLED_SEARCH.getMessage());
					return;
				}
				
				ClanWars.getInstance().setSearching(clan, true);
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.CLAN_WARS_SEARCH.getMessage());
			}
		}.setOnlyLeftClick(true).setClickSound(Sound.LEVEL_UP.bukkitSound()));
		
		this.setItem(slot++, ph);
		
		this.setItem(slot++, ItemBuilder.getItem(Material.DIAMOND_SWORD, "§3Current§8: §b" + ClanWars.getInstance().getSearchingClans().size(), true));
		
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
		this.setItem(slot++, ph);
	}
}
