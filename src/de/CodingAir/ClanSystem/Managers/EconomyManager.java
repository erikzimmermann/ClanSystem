package de.CodingAir.ClanSystem.Managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class EconomyManager {
	
	public enum SellItem {
		COAL(Material.COAL), WOOD(Material.WOOD), IRON(Material.IRON_INGOT), GOLD(Material.GOLD_INGOT), DIAMOND(Material.DIAMOND), EMERALD(Material.EMERALD), LEVEL(null);
		
		private Material material;
		
		SellItem(Material material) {
			this.material = material;
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public int getPrice() {
			FileConfiguration config = FileManager.ECONOMY.getFile().getConfig();
			
			switch(this) {
				case COAL:
					return config.getInt("Prices.Deposit.Coal", 1);
				case WOOD:
					return config.getInt("Prices.Deposit.Wood", 1);
				case IRON:
					return config.getInt("Prices.Deposit.Iron_Ingot", 1);
				case GOLD:
					return config.getInt("Prices.Deposit.Gold_Ingot", 1);
				case DIAMOND:
					return config.getInt("Prices.Deposit.Diamond", 1);
				case EMERALD:
					return config.getInt("Prices.Deposit.Emerald", 1);
				case LEVEL:
					return config.getInt("Prices.Deposit.Player_Level", 1);
				default:
					return 1;
			}
		}
		
		public int getCosts() {
			FileConfiguration config = FileManager.ECONOMY.getFile().getConfig();
			
			switch(this) {
				case COAL:
					return config.getInt("Prices.Withdraw.Coal", 1);
				case WOOD:
					return config.getInt("Prices.Withdraw.Wood", 1);
				case IRON:
					return config.getInt("Prices.Withdraw.Iron_Ingot", 1);
				case GOLD:
					return config.getInt("Prices.Withdraw.Gold_Ingot", 1);
				case DIAMOND:
					return config.getInt("Prices.Withdraw.Diamond", 1);
				case EMERALD:
					return config.getInt("Prices.Withdraw.Emerald", 1);
				case LEVEL:
					return config.getInt("Prices.Withdraw.Player_Level", 1);
				default:
					return 0;
			}
		}
		
		public static boolean isSellItem(ItemStack item) {
			if(item == null || item.getType().equals(Material.AIR)) return false;
			
			for(SellItem items : values()) {
				if(items.getMaterial() == null) continue;
				
				if(item.getType().equals(items.getMaterial())) return true;
			}
			
			return false;
		}
		
		public static SellItem getSellItem(ItemStack item) {
			if(item == null || item.getType().equals(Material.AIR)) return null;
			
			for(SellItem items : values()) {
				if(items.getMaterial() == null) continue;
				
				if(item.getType().equals(items.getMaterial())) return items;
			}
			
			return null;
		}
	}
	
}
