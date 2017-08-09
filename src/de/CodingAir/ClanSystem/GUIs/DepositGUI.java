package de.CodingAir.ClanSystem.GUIs;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.EconomyManager;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Interface;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.InterfaceListener;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Server.Environment;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DepositGUI {
	
	public static void openInterface(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.COMMANDS_DEPOSIT.getMessage(p), 54, ClanSystem.getInstance());
		inv.setEditableItems(true);
		
		inv.addListener(new InterfaceListener() {
			@Override
			public void onInvClickEvent(InventoryClickEvent e) {
				if(e.isShiftClick()) e.setCancelled(true);
				if(e.getClickedInventory() != null && inv.getName().equals(e.getClickedInventory().getName()) && e.getSlot() < 27)
					e.setCancelled(true);
			}
			
			@Override
			public void onInvOpenEvent(InventoryOpenEvent e) {
				
			}
			
			@Override
			public void onInvCloseEvent(InventoryCloseEvent e) {
				List<ItemStack> items = inv.getItemsFromRow(3);
				items.addAll(inv.getItemsFromRow(4));
				items.addAll(inv.getItemsFromRow(5));
				
				items.forEach(item -> Environment.dropItem(item, p));
			}
			
			@Override
			public void onInvDragEvent(InventoryDragEvent e) {
				for(Integer slot : e.getRawSlots()) {
					if(slot < 27) e.setCancelled(true);
				}
			}
		});
		
		ItemStack[] forSale = new ItemStack[6];
		forSale[0] = OldItemBuilder.getItem(Material.COAL, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.COAL.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		forSale[1] = OldItemBuilder.getItem(Material.WOOD, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.WOOD.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		forSale[2] = OldItemBuilder.getItem(Material.IRON_INGOT, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.IRON.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		forSale[3] = OldItemBuilder.getItem(Material.GOLD_INGOT, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.GOLD.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		forSale[4] = OldItemBuilder.getItem(Material.DIAMOND, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.DIAMOND.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		forSale[5] = OldItemBuilder.getItem(Material.EMERALD, "§3" + LanguageManager.GUI_PRICE.getMessage(p) + "§8: §b" + EconomyManager.SellItem.EMERALD.getPrice() + LanguageManager.GUI_CURRENCY.getMessage(p));
		
		ItemStack level = OldItemBuilder.setLore(OldItemBuilder.getItem(Material.EXP_BOTTLE, "§3" + LanguageManager.GUI_LEVEL.getMessage(p) + "§8: §b0"), "", LanguageManager.GUI_DEPOSIT_LEFT_CLICK_LEVEL.getMessage(p), LanguageManager.GUI_DEPOSIT_RIGHT_CLICK_LEVEL.getMessage(p));
		
		ItemStack pane = OldItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		inv.setItem(0, forSale[0]);
		inv.setItem(1, forSale[1]);
		inv.setItem(2, forSale[2]);
		inv.setItem(9, forSale[3]);
		inv.setItem(10, forSale[4]);
		inv.setItem(11, forSale[5]);
		
		inv.addButton(new ItemButton(5, level) {
			@Override
			public void onClick(InventoryClickEvent e) {
				String name = "§3" + LanguageManager.GUI_LEVEL.getMessage(p) + "§8: §b";
				int level = Integer.parseInt(this.getItem().getItemMeta().getDisplayName().replace(name, ""));
				
				if(e.isLeftClick()) {
					if(level > 0) level--;
				} else if(e.isRightClick()) {
					if(p.getLevel() > level) level++;
					else
						p.sendMessage(LanguageManager.PREFIX.getMessage(p) + LanguageManager.ERROR_NOT_ENOUGH_LEVEL.getMessage(p));
				}
				
				this.setItem(OldItemBuilder.setDisplayName(this.getItem(), name + level));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(8, OldItemBuilder.getColored(Material.WOOL, "§c" + LanguageManager.GUI_CANCEL.getMessage(p), DyeColor.RED)) {
			@Override
			public void onClick(InventoryClickEvent e) {
			}
		}.setClickSound(Sound.ITEM_BREAK.bukkitSound()).setCloseOnClick(true));
		
		inv.addButton(new ItemButton(17, OldItemBuilder.getColored(Material.WOOL, "§a" + LanguageManager.COMMANDS_DEPOSIT.getMessage(p), DyeColor.LIME)) {
			@Override
			public void onClick(InventoryClickEvent e) {
				List<ItemStack> items = inv.getItemsFromRow(3);
				items.addAll(inv.getItemsFromRow(4));
				items.addAll(inv.getItemsFromRow(5));
				
				int amount = 0;
				
				for(ItemStack item : items) {
					if(item != null && EconomyManager.SellItem.isSellItem(item)) {
						amount += item.getAmount() * EconomyManager.SellItem.getSellItem(item).getPrice();
						inv.removeItem(item);
					}
				}
				
				String name = "§3" + LanguageManager.GUI_LEVEL.getMessage(p) + "§8: §b";
				int level = Integer.parseInt(inv.getItem(5).getItemMeta().getDisplayName().replace(name, ""));
				amount += level * EconomyManager.SellItem.LEVEL.getPrice();
				p.setLevel(p.getLevel() - level);
				
				if(amount != 0) {
					clan.setBalance(clan.getBalance() + amount);
					clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage(p).replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.SUCCESS_PLAYER_DEPOSIT.getMessage(p).replace("%player%", p.getName()).replace("%amount%", amount + ""));
				}
				
				p.closeInventory();
			}
		}.setClickSound(Sound.LEVEL_UP.bukkitSound()));
		
		inv.setItem(3, pane);
		inv.setItem(7, pane);
		inv.setItem(12, pane);
		inv.setItem(16, pane);
		inv.setItem(18, pane);
		inv.setItem(19, pane);
		inv.setItem(20, pane);
		inv.setItem(21, pane);
		inv.setItem(22, pane);
		inv.setItem(23, pane);
		inv.setItem(24, pane);
		inv.setItem(25, pane);
		inv.setItem(26, pane);
		
		inv.open(p);
	}
	
}
