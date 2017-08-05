package de.CodingAir.ClanSystem.GUIs;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.EconomyManager;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.Interface;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_4.CodingAPI.Server.Environment;
import de.CodingAir.v1_4.CodingAPI.Server.Sound;
import de.CodingAir.v1_4.CodingAPI.Tools.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WithdrawGUI {
	
	public static void openInterface(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.COMMANDS_WITHDRAW.getMessage(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack clanIcon = clan.getIcon();
		if(clanIcon == null) clanIcon = ItemBuilder.getItem(Material.NETHER_STAR);
		ItemBuilder.setDisplayName(clanIcon, "§7Clan§8: " + ClanSystem.getClanManager().getClanColor(clan.getClanRank()) + clan.getName());
		ItemBuilder.setLore(clanIcon, "", "§3" + LanguageManager.GUI_MONEY.getMessage() + "§8: §b" + clan.getBalance() + LanguageManager.GUI_CURRENCY.getMessage());
		
		inv.setItem(0, ItemBuilder.removeStandardLore(clanIcon));
		inv.setItem(1, ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK));
		
		int slot = 2;
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.COAL, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.COAL.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.WOOD, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.WOOD.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.IRON_INGOT, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.IRON.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.GOLD_INGOT, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.GOLD.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.DIAMOND, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.DIAMOND.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.EMERALD, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.EMERALD.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(slot++, ItemBuilder.getItem(Material.EXP_BOTTLE, "§3" + LanguageManager.GUI_COSTS.getMessage() + "§8: §b" + EconomyManager.SellItem.LEVEL.getCosts() + LanguageManager.GUI_CURRENCY.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				buy(p, inv, this.getItem().getType(), e.isShiftClick());
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.open(p);
	}
	
	private static void buy(Player p, Interface inv, Material material, boolean shift) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		
		EconomyManager.SellItem sellItem;
		
		System.out.println(material.name());
		
		switch(material) {
			case COAL:
				sellItem = EconomyManager.SellItem.COAL;
				break;
			case WOOD:
				sellItem = EconomyManager.SellItem.WOOD;
				break;
			case IRON_INGOT:
				sellItem = EconomyManager.SellItem.IRON;
				break;
			case GOLD_INGOT:
				sellItem = EconomyManager.SellItem.GOLD;
				break;
			case DIAMOND:
				sellItem = EconomyManager.SellItem.DIAMOND;
				break;
			case EMERALD:
				sellItem = EconomyManager.SellItem.EMERALD;
				break;
			case EXP_BOTTLE:
				sellItem = EconomyManager.SellItem.LEVEL;
				break;
			default:
				sellItem = EconomyManager.SellItem.LEVEL;
		}
		
		int costs = sellItem.getCosts();
		
		ItemStack item = ItemBuilder.getItem(material);
		
		if(shift) {
			item.setAmount((clan.getBalance() / costs > 64 ? 64 : clan.getBalance() / costs));
			costs *= item.getAmount();
		}
		
		if(clan.getBalance() < costs || item.getAmount() == 0) {
			p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_NOT_ENOUGH_MONEY.getMessage());
			return;
		}
		
		clan.setBalance(clan.getBalance() - costs);
		
		ItemStack clanIcon = clan.getIcon();
		if(clanIcon == null) clanIcon = ItemBuilder.getItem(Material.NETHER_STAR);
		ItemBuilder.setDisplayName(clanIcon, "§7Clan§8: " + ClanSystem.getClanManager().getClanColor(clan.getClanRank()) + clan.getName());
		ItemBuilder.setLore(clanIcon, "", "§3" + LanguageManager.GUI_MONEY.getMessage() + "§8: §b" + clan.getBalance() + LanguageManager.GUI_CURRENCY.getMessage());
		
		inv.setItem(0, ItemBuilder.removeStandardLore(clanIcon));
		
		if(item.getType().equals(Material.EXP_BOTTLE)){
			p.setLevel(p.getLevel() + item.getAmount());
		} else {
			if(p.getInventory().firstEmpty() == -1) {
				Environment.dropItem(item, p);
			} else {
				p.getInventory().addItem(item);
				p.updateInventory();
			}
		}
		
		String name = (sellItem.getMaterial() == null ? LanguageManager.GUI_LEVEL.getMessage() : sellItem.getMaterial().name().substring(0, 1) + sellItem.getMaterial().name().toLowerCase().substring(1, sellItem.getMaterial().name().length()));
		
		clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) +
				LanguageManager.SUCCESS_PLAYER_WITHDRAW.getMessage().replace("%player%", p.getName()).replace("%amount%", item.getAmount() + "").replace("%item%", name).replace("%costs%", costs + ""));
	}
}
