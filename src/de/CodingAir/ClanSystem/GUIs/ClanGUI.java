package de.CodingAir.ClanSystem.GUIs;

import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Managers.LanguageManager;
import de.CodingAir.ClanSystem.Managers.LayoutManager;
import de.CodingAir.ClanSystem.Managers.TeleportManager;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Request;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.v1_4.CodingAPI.BungeeCord.ProxiedPlayer;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Anvil.*;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.Interface;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_4.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_4.CodingAPI.Server.Sound;
import de.CodingAir.v1_4.CodingAPI.Tools.Callback;
import de.CodingAir.v1_4.CodingAPI.Tools.ItemBuilder;
import de.CodingAir.v1_4.CodingAPI.Utils.TextAlignment;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public enum ClanGUI {
	CONFIRM, OPTIONS, MAIN, MEMBERS;
	
	public void open(Player p, String option, Callback<Boolean> callback) {
		if(this.equals(CONFIRM)) confirm(p, option, callback);
	}
	
	public void open(Player p) {
		if(this.equals(ClanGUI.OPTIONS)) options(p);
		if(this.equals(ClanGUI.MAIN)) main(p);
		if(this.equals(ClanGUI.MEMBERS)) members(p);
	}
	
	private void main(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		Interface inv = new Interface(p, "§cClan §7- " + ClanSystem.getClanManager().getClanColor(clan.getClanRank()) + clan.getName(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack pane = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		ItemStack clanIcon = clan.getIcon();
		if(clanIcon == null) clanIcon = ItemBuilder.getItem(Material.NETHER_STAR);
		ItemBuilder.setDisplayName(clanIcon, "§7Clan§8: " + ClanSystem.getClanManager().getClanColor(clan.getClanRank()) + clan.getName());
		
		inv.setItem(0, ItemBuilder.removeStandardLore(clanIcon));
		
		inv.setItem(1, pane);
		
		int slot = 3;
		
		if(Options.CLAN_BASES.getBoolean()){
			inv.addButton(new ItemButton(slot, ItemBuilder.getItem(Material.BEACON, "§3" + LanguageManager.GUI_BASE.getMessage())) {
				@Override
				public void onClick(InventoryClickEvent e) {
					if(clan.getBase() == null) {
						p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_NO_CLAN_BASE.getMessage());
						return;
					}
					
					if(clan.getHomeServer() == null || clan.getHomeServer().isEmpty()) {
						clan.setHomeServer(ClanSystem.SERVER);
					}
					
					if(!clan.rightServer()) {
						p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_WRONG_SERVER.getMessage().replace("%server%", clan.getHomeServer()));
						return;
					}
					
					p.closeInventory();
					TeleportManager.teleport(p, clan.getBase());
				}
			}.setClickSound(Sound.CLICK.bukkitSound()));
			
			slot++;
		}
		
		inv.addButton(new ItemButton(slot, ItemBuilder.getItem(Material.BOOK, "§3" + LanguageManager.GUI_MEMBERS.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				MEMBERS.open(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		if(Options.CLAN_BASES.getBoolean()) slot++;
		else slot += 2;
		
		inv.addButton(new ItemButton(slot, ItemBuilder.removeStandardLore(ItemBuilder.getItem(Material.IRON_SWORD, "§3" + LanguageManager.GUI_ALLIANCES.getMessage()))) {
			@Override
			public void onClick(InventoryClickEvent e) {
				alliances(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(7, pane);
		
		inv.addButton(new ItemButton(8, ItemBuilder.getItem(Material.REDSTONE, "§3" + LanguageManager.GUI_OPTIONS.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				clanOptions(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.open(p);
	}
	
	private void members(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.GUI_MEMBERS.getMessage(), 27, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack pane = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				MAIN.open(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(1, pane);
		inv.setItem(9, pane);
		inv.setItem(10, pane);
		inv.setItem(19, pane);
		
		inv.addButton(new ItemButton(18, ItemBuilder.getItem(Material.GLOWSTONE_DUST, LanguageManager.GUI_INVITE_MEMBERS.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(clan.getSize() >= Options.CLAN_SIZE.getInt()) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CLAN_ALREADY_FULL.getMessage());
					return;
				}
				
				p.closeInventory();
				
				AnvilGUI.openAnvil(ClanSystem.getInstance(), p, new AnvilListener() {
					@Override
					public void onClick(AnvilClickEvent e) {
						e.setCancelled(true);
						e.setClose(false);
						
						if(e.getSlot().equals(AnvilSlot.NONE)) return;
						playSound(p);
						
						String input = e.getInput();
						
						if(input == null || input.isEmpty()) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_ENTER_A_NAME.getMessage());
							return;
						}
						
						Player other = Bukkit.getPlayer(input);
						ProxiedPlayer proxy = ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer(input);
						
						if(other == null && proxy == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_PLAYER_IS_OFFLINE.getMessage());
							return;
						}
						
						UUID uniqueId = (other == null ? proxy.getUniqueId() : ClanSystem.getUUID(other));
						
						if(ClanSystem.getClanManager().getClan(uniqueId) != null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_THIS_PLAYER_IS_ALREADY_IN_A_CLAN.getMessage());
							return;
						}
						
						if(ClanSystem.getClanManager().hasInvite(uniqueId, clan)) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_PLAYER_HAS_ALREADY_A_INVITATION.getMessage());
							return;
						}
						
						if(other == null && Options.BUNGEECORD.getBoolean()) {
							ClanSystem.getInstance().getBungeeCordManager().request(new Request(Request.Type.INVITE_PLAYER, clan.getName(), proxy.getName()));
							return;
						}
						
						ClanSystem.getClanManager().invite(clan, uniqueId);
						
						String msg = LanguageManager.PREFIX.getMessage() + LanguageManager.CLAN_PLAYER_INVITE.getMessage().replace("%clan%", clan.getName());
						
						if(msg.contains("%yes%") && msg.contains("%/yes%") && msg.contains("%no%") && msg.contains("%/no%")) {
							String yes = msg.split("%yes%")[1].split("%/yes%")[0];
							String no = msg.split("%no%")[1].split("%/no%")[0];
							
							TextComponent message = new TextComponent(msg.split("%yes%")[0]);
							
							TextComponent accept = new TextComponent(yes);
							accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_INVITE.getMessage() + " " + LanguageManager.COMMANDS_ACCEPT.getMessage() + " " + clan.getName()));
							accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage()).create()));
							
							TextComponent decline = new TextComponent(no);
							decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_INVITE.getMessage() + " " + LanguageManager.COMMANDS_DECLINE.getMessage() + " " + clan.getName()));
							decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage()).create()));
							
							message.addExtra(accept);
							message.addExtra(msg.split("%/yes%")[1].split("%no%")[0]);
							message.addExtra(decline);
							message.addExtra(msg.split("%/no%")[1]);
							
							other.spigot().sendMessage(message);
						} else {
							other.sendMessage(msg);
						}
						
						clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_PLAYER_INVITE_NOTIFY.getMessage().replace("%player%", other.getName()));
						
						e.setWillDestroy(true);
						p.closeInventory();
						MEMBERS.open(p);
					}
					
					@Override
					public void onClose(AnvilCloseEvent e) {
					}
				}, ItemBuilder.getItem(Material.PAPER, LanguageManager.GUI_NAME.getMessage() + "..."));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		if(ClanSystem.getPlayer(UUID.fromString(clan.getLeader_uuid())) != null)
			inv.addItem(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_LEADER.getString() + ClanSystem.getPlayer(UUID.fromString(clan.getLeader_uuid())).getName()));
		else if(ClanSystem.isOnProxy(UUID.fromString(clan.getLeader_uuid())))
			inv.addItem(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_LEADER.getString() + clan.getLeader()));
		else
			inv.addItem(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_LEADER.getString() + clan.getLeader()));
		
		clan.getTrusted().forEach((name, id) -> {
			if(ClanSystem.getPlayer(UUID.fromString(id)) != null || ClanSystem.isOnProxy(UUID.fromString(id))){
				String memberName = (ClanSystem.getPlayer(UUID.fromString(id)) == null ? name : ClanSystem.getPlayer(UUID.fromString(id)).getName());
				inv.addButton(new ItemButton(inv.firstEmpty(), ItemBuilder.setLore(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_TRUSTED.getString() + memberName), "", LanguageManager.GUI_MORE_OPTIONS.getMessage())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if(id.equals(ClanSystem.getUUID(p).toString())) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CANNOT_INTERACT_WITH_YOURSELF.getMessage());
							return;
						}
						
						p.closeInventory();
						
						member(p, UUID.fromString(id), name);
					}
				}.setClickSound(Sound.CLICK.bukkitSound()));
			}
			else
				inv.addButton(new ItemButton(inv.firstEmpty(), ItemBuilder.setLore(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_TRUSTED.getString() + name), "", LanguageManager.GUI_MORE_OPTIONS.getMessage())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if(id.equals(ClanSystem.getUUID(p).toString())) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CANNOT_INTERACT_WITH_YOURSELF.getMessage());
							return;
						}
						
						p.closeInventory();
						
						member(p, UUID.fromString(id), name);
					}
				}.setClickSound(Sound.CLICK.bukkitSound()));
		});
		
		clan.getMembers().forEach((name, id) -> {
			if(ClanSystem.getPlayer(UUID.fromString(id)) != null || ClanSystem.isOnProxy(UUID.fromString(id))){
				String memberName = (ClanSystem.getPlayer(UUID.fromString(id)) == null ? name : ClanSystem.getPlayer(UUID.fromString(id)).getName());
				inv.addButton(new ItemButton(inv.firstEmpty(), ItemBuilder.setLore(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_MEMBER.getString() + memberName), "", LanguageManager.GUI_MORE_OPTIONS.getMessage())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if(id.equals(ClanSystem.getUUID(p).toString())) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CANNOT_INTERACT_WITH_YOURSELF.getMessage());
							return;
						}
						
						p.closeInventory();
						
						member(p, UUID.fromString(id), name);
					}
				}.setClickSound(Sound.CLICK.bukkitSound()));
			}
			else
				inv.addButton(new ItemButton(inv.firstEmpty(), ItemBuilder.setLore(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_MEMBER.getString() + name), "", LanguageManager.GUI_MORE_OPTIONS.getMessage())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if(id.equals(ClanSystem.getUUID(p).toString())) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CANNOT_INTERACT_WITH_YOURSELF.getMessage());
							return;
						}
						
						p.closeInventory();
						
						member(p, UUID.fromString(id), name);
					}
				}.setClickSound(Sound.CLICK.bukkitSound()));
		});
		
		inv.open(p);
	}
	
	private void member(Player p, UUID other, String name) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		boolean trusted = clan.isTrusted(other);
		boolean online = ClanSystem.getPlayer(other) != null;
		
		name = (online ? ClanSystem.getPlayer(other).getName() : name);
		
		final String officialName = name;
		
		Interface inv = new Interface(p, "§cClan §7- " + ClanSystem.getClanManager().getColor((clan.isTrusted(other) ? 1 : 2)) + officialName, 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack pane = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		ItemStack item;
		if(online || ClanSystem.isOnProxy(other))
			item = ItemBuilder.getHead(ClanSystem.getGameProfile(p), (trusted ? Options.CLAN_RANK_COLOR_TRUSTED.getString() : Options.CLAN_RANK_COLOR_MEMBER.getString()) + officialName);
		else
			item = ItemBuilder.getHead(Skull.Skeleton, (trusted ? Options.CLAN_RANK_COLOR_TRUSTED.getString() : Options.CLAN_RANK_COLOR_MEMBER.getString()) + officialName);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				MEMBERS.open(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(1, pane);
		
		inv.setItem(2, item);
		
		
		ItemStack promote = ItemBuilder.getColored(Material.WOOL, (clan.isTrusted(other) ? "§7" : "§a") + LanguageManager.GUI_PROMOTE.getMessage(), (clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.LIME));
		ItemStack demote = ItemBuilder.getColored(Material.WOOL, (!clan.isTrusted(other) ? "§7" : "§c") + LanguageManager.GUI_DEMOTE.getMessage(), (!clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.RED));
		
		inv.addButton(new ItemButton(5, promote) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanPromote() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				if(clan.isTrusted(other)) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_ALREADY_TRUSTED.getMessage().replace("%rank_color%", ClanSystem.getClanManager().getColor(1)));
					return;
				}
				
				clan.setTrusted(other, true);
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.SUCCESS_PROMOTE.getMessage().replace("%player%", officialName).replace("%rank_color%", ClanSystem.getClanManager().getColor(1)));
				
				inv.setTitle("§cClan §7- " + ClanSystem.getClanManager().getColor((clan.isTrusted(other) ? 1 : 2)) + officialName);
				inv.setItem(2, ItemBuilder.setDisplayName(item, (clan.isTrusted(other) ? Options.CLAN_RANK_COLOR_TRUSTED.getString() : Options.CLAN_RANK_COLOR_MEMBER.getString()) + officialName));
				this.getInterface().getButtonAt(6).setItem(ItemBuilder.getColored(Material.WOOL, (!clan.isTrusted(other) ? "§7" : "§c") + LanguageManager.GUI_DEMOTE.getMessage(), (!clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.RED)));
				this.setItem(ItemBuilder.getColored(Material.WOOL, (clan.isTrusted(other) ? "§7" : "§a") + LanguageManager.GUI_PROMOTE.getMessage(), (clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.LIME)));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(6, demote) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanDemote() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				if(!clan.isTrusted(other)) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_ALREADY_MEMBER.getMessage().replace("%rank_color%", ClanSystem.getClanManager().getColor(2)));
					return;
				}
				
				clan.setTrusted(other, false);
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.SUCCESS_DEMOTE.getMessage().replace("%player%", officialName).replace("%rank_color%", ClanSystem.getClanManager().getColor(2)));
				
				inv.setTitle("§cClan §7- " + ClanSystem.getClanManager().getColor((clan.isTrusted(other) ? 1 : 2)) + officialName);
				inv.setItem(2, ItemBuilder.setDisplayName(item, (clan.isTrusted(other) ? Options.CLAN_RANK_COLOR_TRUSTED.getString() : Options.CLAN_RANK_COLOR_MEMBER.getString()) + officialName));
				this.getInterface().getButtonAt(5).setItem(ItemBuilder.getColored(Material.WOOL, (clan.isTrusted(other) ? "§7" : "§a") + LanguageManager.GUI_PROMOTE.getMessage(), (clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.LIME)));
				this.setItem(ItemBuilder.getColored(Material.WOOL, (!clan.isTrusted(other) ? "§7" : "§c") + LanguageManager.GUI_DEMOTE.getMessage(), (!clan.isTrusted(other) ? DyeColor.SILVER : DyeColor.RED)));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(8, ItemBuilder.getItem(Material.TNT, LanguageManager.GUI_KICK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanKick() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				Callback<Boolean> callback = new Callback<Boolean>() {
					@Override
					public void accept(Boolean kicked) {
						if(kicked) {
							clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_KICK.getMessage().replace("%player%", officialName).replace("%stuff%", p.getName()).replace("%staff%", p.getName()));
							clan.kick(other);
							
							p.closeInventory();
							MEMBERS.open(p);
						} else {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_PLAYER_NOT_KICKED.getMessage().replace("%player%", officialName));
							p.closeInventory();
							member(p, other, officialName);
						}
					}
				};
				
				p.closeInventory();
				confirm(p, LanguageManager.GUI_KICK_APPLY.getMessage().replace("%player%", officialName), callback);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.open(p);
	}
	
	private void alliances(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.GUI_ALLIANCES.getMessage(), 27, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack pane = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				MAIN.open(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(1, pane);
		inv.setItem(9, pane);
		inv.setItem(10, pane);
		inv.setItem(19, pane);
		
		inv.addButton(new ItemButton(18, ItemBuilder.getItem(Material.GLOWSTONE_DUST, LanguageManager.GUI_FOUND_ALLIANCES.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanAlliance() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				if(clan.getAlliances().size() >= Options.CLAN_MAX_ALLIANCES.getInt()) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CLAN_MAX_ALLIANCES.getMessage());
					return;
				}
				
				AnvilGUI.openAnvil(ClanSystem.getInstance(), p, new AnvilListener() {
					@Override
					public void onClick(AnvilClickEvent e) {
						e.setCancelled(true);
						e.setClose(false);
						
						if(e.getSlot().equals(AnvilSlot.NONE)) return;
						playSound(p);
						
						String input = e.getInput();
						
						if(input == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_ENTER_A_NAME.getMessage());
							return;
						}
						
						Clan target = ClanSystem.getClanManager().getClan(input);
						
						if(target == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CLAN_NOT_EXISTS.getMessage());
							return;
						}
						
						if(target.getName().equals(clan.getName())) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_OWN_CLAN.getMessage());
							return;
						}
						
						if(clan.hasAllianceWith(target)) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_ALREADY_IN_ALLIANCE.getMessage().replace("%clan%", target.getName()));
							return;
						}
						
						Player targetLeader = Bukkit.getPlayer(target.getLeader());
						ProxiedPlayer proxy = ClanSystem.getInstance().getBungeeCordManager().getProxiedPlayer(target.getLeader());
						
						if(targetLeader == null && proxy == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_LEADER_NOT_ONLINE.getMessage());
							return;
						}
						
						if(targetLeader == null && Options.BUNGEECORD.getBoolean()) {
							ClanSystem.getInstance().getBungeeCordManager().request(new Request(Request.Type.INVITE_CLAN, clan.getName(), target.getName(), proxy.getName()));
							return;
						}
						
						String msg = LanguageManager.PREFIX.getMessage() + LanguageManager.CLAN_ALLIANCE_TARGET.getMessage().replace("%clan%", clan.getName());
						
						if(msg.contains("%yes%") && msg.contains("%/yes%") && msg.contains("%no%") && msg.contains("%/no%")) {
							String yes = msg.split("%yes%")[1].split("%/yes%")[0];
							String no = msg.split("%no%")[1].split("%/no%")[0];
							
							TextComponent message = new TextComponent(msg.split("%yes%")[0]);
							
							TextComponent accept = new TextComponent(yes);
							accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_ALLIANCE.getMessage() + " " + LanguageManager.COMMANDS_ACCEPT.getMessage() + " " + clan.getName()));
							accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage()).create()));
							
							TextComponent decline = new TextComponent(no);
							decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_ALLIANCE.getMessage() + " " + LanguageManager.COMMANDS_DECLINE.getMessage() + " " + clan.getName()));
							decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage()).create()));
							
							message.addExtra(accept);
							message.addExtra(msg.split("%/yes%")[1].split("%no%")[0]);
							message.addExtra(decline);
							message.addExtra(msg.split("%/no%")[1]);
							
							targetLeader.spigot().sendMessage(message);
						} else {
							targetLeader.sendMessage(msg);
						}
						
						ClanSystem.getClanManager().alliance(clan, target);
						clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_ALLIANCE_CLAN.getMessage().replace("%clan%", target.getName()));
						
						e.setWillDestroy(true);
						p.closeInventory();
						alliances(p);
					}
					
					@Override
					public void onClose(AnvilCloseEvent e) {
						
					}
				}, ItemBuilder.getItem(Material.PAPER, LanguageManager.GUI_CLAN.getMessage() + "..."));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		List<Clan> alliances = clan.getClansWithAlliance();
		
		alliances.forEach(other -> {
			ItemStack clanIcon = other.getIcon();
			if(clanIcon == null) clanIcon = ClanSystem.MAIN_ICON();
			ItemBuilder.setDisplayName(clanIcon, ClanSystem.getClanManager().getClanColor(other.getClanRank()) + other.getName());
			
			inv.addButton(new ItemButton(inv.firstEmpty(), ItemBuilder.removeStandardLore(ItemBuilder.setLore(clanIcon, "", LanguageManager.GUI_MORE_OPTIONS.getMessage()))) {
				@Override
				public void onClick(InventoryClickEvent e) {
					alliance(p, other);
				}
			}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		});
		
		inv.open(p);
	}
	
	private void alliance(Player p, Clan other) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null || other == null) return;
		
		ItemStack clanIcon = other.getIcon();
		if(clanIcon == null) clanIcon = ClanSystem.MAIN_ICON();
		ItemBuilder.setDisplayName(clanIcon, ClanSystem.getClanManager().getClanColor(other.getClanRank()) + other.getName());
		
		Interface inv = new Interface(p, "§cClan §7- " + ClanSystem.getClanManager().getClanColor(other.getClanRank()) + other.getName(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				alliances(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(1, ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK));
		inv.setItem(2, ItemBuilder.removeStandardLore(clanIcon));
		inv.setItem(3, ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK));
		
		inv.addButton(new ItemButton(6, ItemBuilder.getColored(Material.WOOL, "§c" + LanguageManager.COMMANDS_NEUTRAL.getMessage(), DyeColor.RED)) {
			@Override
			public void onClick(InventoryClickEvent e) {
				CONFIRM.open(p, LanguageManager.GUI_NEUTRAL_APPLY.getMessage(), new Callback<Boolean>() {
					@Override
					public void accept(Boolean neutral) {
						if(neutral) {
							clan.removeAlliance(other);
							other.removeAlliance(clan);
							
							clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.SUCCESS_ALLIANCE_REMOVED.getMessage().replace("%clan%", other.getName()));
							other.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(other.getClanRank())).replace("%clanname%", other.getName()) + LanguageManager.SUCCESS_ALLIANCE_REMOVED.getMessage().replace("%clan%", clan.getName()));
							alliances(p);
						} else {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CLAN_NOT_NEUTRALIZED.getMessage());
							alliance(p, other);
						}
					}
				});
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.open(p);
	}
	
	private void confirm(Player p, String middle, Callback<Boolean> callback) {
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.GUI_CONFIRM.getMessage(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		inv.addButton(new ItemButton(2, ItemBuilder.getColored(ItemBuilder.getItem(Material.WOOL, LanguageManager.GUI_YES.getMessage()), DyeColor.LIME)) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(callback != null) callback.accept(true);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(4, ItemBuilder.setText(ItemBuilder.getItem(Material.NETHER_STAR), TextAlignment.LEFT, TextAlignment.lineBreak(middle, 100)));
		
		inv.addButton(new ItemButton(6, ItemBuilder.getColored(ItemBuilder.getItem(Material.WOOL, LanguageManager.GUI_NO.getMessage()), DyeColor.RED)) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(callback != null) callback.accept(false);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.open(p);
	}
	
	private void clanOptions(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- " + ClanSystem.getClanManager().getClanColor(clan.getClanRank()) + clan.getName(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				MAIN.open(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		inv.setItem(1, ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK));
		
		String enabled = LanguageManager.GUI_ENABLED.getMessage();
		String disabled = LanguageManager.GUI_DISABLED.getMessage();
		String on = LanguageManager.ON.getMessage().toLowerCase();
		String off = LanguageManager.OFF.getMessage().toLowerCase();
		String state = LanguageManager.GUI_STATE.getMessage();
		
		ItemStack privateChat = ItemBuilder.setLore(ItemBuilder.getItem(Material.PAPER, "§3§n" + LanguageManager.GUI_PRIVATE_CHAT.getMessage()), "", "§7" + state + "§8: " + (clan.isChat() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", "" + LanguageManager.GUI_PRIVATE_CHAT.getMessage()).replace("%state%", (!clan.isChat() ? "§a" + on : "§c" + off)));
		ItemStack clanIcon = ItemBuilder.setLore(ItemBuilder.getItem(Material.ITEM_FRAME, "§3§n" + LanguageManager.GUI_CLAN_ICON.getMessage()), "", LanguageManager.GUI_SET_ICON.getMessage());
		ItemStack setLeader = ItemBuilder.removeStandardLore(ItemBuilder.setLore(ItemBuilder.getItem(Material.GOLD_HELMET, "§3§n" + LanguageManager.GUI_LEADER.getMessage()), "", LanguageManager.GUI_SET_LEADER.getMessage()));
		ItemStack delete = ItemBuilder.setLore(ItemBuilder.getItem(Material.BARRIER, "§3§n" + LanguageManager.COMMANDS_DELETE.getMessage()), "", LanguageManager.GUI_DELETE.getMessage());
		
		inv.addButton(new ItemButton(3, privateChat) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanToggleChat() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				clan.setChat(!clan.isChat());
				
				this.setItem(ItemBuilder.setLore(ItemBuilder.getItem(Material.PAPER, "§3§n" + LanguageManager.GUI_PRIVATE_CHAT.getMessage()), "", "§7" + state + "§8: " + (clan.isChat() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", "" + LanguageManager.GUI_PRIVATE_CHAT.getMessage()).replace("%state%", (!clan.isChat() ? "§a" + on : "§c" + off))));
				
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.GUI_CLAN_TOGGLE.getMessage().replace("%player%", p.getName()).replace("%state%", (clan.isChat() ? "§a" + on : "§c" + off)).replace("%option%", LanguageManager.GUI_PRIVATE_CHAT.getMessage()));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(4, ItemBuilder.removeStandardLore(clanIcon)) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p) && (!ClanSystem.getClanManager().trustedCanSetIcon() || !clan.isTrusted(p))) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				ItemStack current = p.getInventory().getItemInHand();
				
				if(current == null || current.getType().equals(Material.AIR)) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_NEEDED_ITEM_IN_HAND.getMessage());
					return;
				}
				
				clan.setIcon(current);
				clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clanname%", clan.getName()).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())) + LanguageManager.GUI_CLAN_CONFIGURED.getMessage().replace("%player%", p.getName()).replace("%option%", LanguageManager.GUI_CLAN_ICON.getMessage()));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(5, setLeader) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p)) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_PERMISSION.getMessage());
					return;
				}
				
				p.closeInventory();
				setLeader(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(6, delete) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if(!clan.isLeader(p)) {
					p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.NO_LEADER.getMessage());
					return;
				}
				
				p.closeInventory();
				
				ClanGUI.CONFIRM.open(p, LanguageManager.GUI_DELETE_APPLY.getMessage(), new Callback<Boolean>() {
					@Override
					public void accept(Boolean deleted) {
						if(deleted) {
							clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_DELETED_BY_LEADER.getMessage(), p);
							clan.kickAll();
							ClanSystem.getClanManager().removeClan(clan);
							
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.SUCCESS_CLAN_DELETED.getMessage().replace("%clanname%", clan.getName()));
						} else {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_CLAN_NOT_DELETED.getMessage());
						}
					}
				});
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.setItem(8, ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK));
		
		inv.open(p);
	}
	
	private void setLeader(Player p) {
		Clan clan = ClanSystem.getClanManager().getClan(p);
		if(clan == null) return;
		
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.GUI_LEADER.getMessage(), 27, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		ItemStack pane = ItemBuilder.getColored(Material.STAINED_GLASS_PANE, "§0", DyeColor.BLACK);
		
		inv.addButton(new ItemButton(0, ItemBuilder.setDisplayName(Skull.ArrowLeft.getItemStack(), "§c" + LanguageManager.GUI_BACK.getMessage())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				clanOptions(p);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.setItem(1, pane);
		inv.setItem(9, pane);
		inv.setItem(10, pane);
		inv.setItem(18, pane);
		inv.setItem(19, pane);
		
		if(ClanSystem.getPlayer(UUID.fromString(clan.getLeader_uuid())) != null)
			inv.addItem(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_LEADER.getString() + ClanSystem.getPlayer(UUID.fromString(clan.getLeader_uuid())).getName()));
		else if(ClanSystem.isOnProxy(UUID.fromString(clan.getLeader_uuid())))
			inv.addItem(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_LEADER.getString() + clan.getLeader()));
		else
			inv.addItem(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_LEADER.getString() + clan.getLeader()));
		
		clan.getTrusted().forEach((name, id) -> {
			ItemStack item;
			if(ClanSystem.getPlayer(UUID.fromString(id)) != null || ClanSystem.isOnProxy(UUID.fromString(id))){
				String memberName = (ClanSystem.getPlayer(UUID.fromString(id)) == null ? name : ClanSystem.getPlayer(UUID.fromString(id)).getName());
				item = ItemBuilder.setLore(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_TRUSTED.getString() + memberName), "", LanguageManager.GUI_CLICK_FOR_NEW_LEADER.getMessage());
			}
			else
				item = ItemBuilder.setLore(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_TRUSTED.getString() + name), "", LanguageManager.GUI_MORE_OPTIONS.getMessage());
			
			inv.addButton(new ItemButton(inv.firstEmpty(), item) {
				@Override
				public void onClick(InventoryClickEvent e) {
					CONFIRM.open(p, LanguageManager.GUI_LEADER_APPLY.getMessage().replace("%player%", name), new Callback<Boolean>() {
						@Override
						public void accept(Boolean accepted) {
							if(accepted) {
								clan.kick(UUID.fromString(id));
								clan.setLeader(name);
								clan.setLeader_uuid(id);
								clan.add(p);
								clan.setTrusted(p, true);
								clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.SUCCESS_NEW_LEADER.getMessage().replace("%player%", name));
								options(p);
							} else {
								p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_LEADER_NOT_SET.getMessage());
								setLeader(p);
							}
						}
					});
				}
			}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		});
		
		clan.getMembers().forEach((name, id) -> {
			ItemStack item;
			if(ClanSystem.getPlayer(UUID.fromString(id)) != null || ClanSystem.isOnProxy(UUID.fromString(id))) {
				String memberName = (ClanSystem.getPlayer(UUID.fromString(id)) == null ? name : ClanSystem.getPlayer(UUID.fromString(id)).getName());
				item = ItemBuilder.setLore(ItemBuilder.getHead(ClanSystem.getGameProfile(p), Options.CLAN_RANK_COLOR_MEMBER.getString() + memberName), "", LanguageManager.GUI_CLICK_FOR_NEW_LEADER.getMessage());
			}
			else
				item = ItemBuilder.setLore(ItemBuilder.getHead(Skull.Skeleton, Options.CLAN_RANK_COLOR_MEMBER.getString() + name), "", LanguageManager.GUI_MORE_OPTIONS.getMessage());
			
			inv.addButton(new ItemButton(inv.firstEmpty(), item) {
				@Override
				public void onClick(InventoryClickEvent e) {
					CONFIRM.open(p, LanguageManager.GUI_LEADER_APPLY.getMessage().replace("%player%", name), new Callback<Boolean>() {
						@Override
						public void accept(Boolean accepted) {
							if(accepted) {
								clan.kick(p);
								clan.setLeader(name);
								clan.setLeader_uuid(id);
								clan.add(p);
								clan.setTrusted(p, true);
								clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage().replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.SUCCESS_NEW_LEADER.getMessage().replace("%player%", name));
							} else {
								p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.ERROR_LEADER_NOT_SET.getMessage());
								
							}
						}
					});
				}
			}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		});
		
		inv.open(p);
	}
	
	private void options(Player p) {
		Interface inv = new Interface(p, "§cClan §7- §c" + LanguageManager.GUI_OPTIONS.getMessage(), 9, ClanSystem.getInstance());
		inv.setEditableItems(false);
		
		String enabled = LanguageManager.GUI_ENABLED.getMessage();
		String disabled = LanguageManager.GUI_DISABLED.getMessage();
		String on = LanguageManager.ON.getMessage().toLowerCase();
		String off = LanguageManager.OFF.getMessage().toLowerCase();
		String state = LanguageManager.GUI_STATE.getMessage();
		
		ItemStack globalchat = ItemBuilder.setLore(ItemBuilder.getItem(Material.PAPER, "§3§n" + LanguageManager.GUI_GLOBALCHAT.getMessage()), "", "§7" + state + "§8: " + (Options.GLOBALCHAT_ENABLED.getBoolean() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", "" + LanguageManager.GUI_GLOBALCHAT.getMessage()).replace("%state%", (!Options.GLOBALCHAT_ENABLED.getBoolean() ? "§a" + on : "§c" + off)));
		ItemStack tags = ItemBuilder.setLore(ItemBuilder.getItem(Material.NAME_TAG, "§3§n" + LanguageManager.GUI_PREFIX_AND_SUFFIX.getMessage()), "", "§7" + state + "§8: " + (Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", LanguageManager.GUI_PREFIX_AND_SUFFIX.getMessage()).replace("%state%", (!Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() ? "§a" + on : "§c" + off)));
		ItemStack perms = ItemBuilder.setLore(ItemBuilder.getItem(Material.BLAZE_ROD, "§3§n" + LanguageManager.GUI_TRUSTED_PERMISSIONS.getMessage()), "", LanguageManager.GUI_CONFIGURE.getMessage().replace("%option%", "" + LanguageManager.GUI_TRUSTED_PERMISSIONS.getMessage()));
		ItemStack nameLength = ItemBuilder.setLore(ItemBuilder.getItem(Material.BLAZE_ROD, "§3§n" + LanguageManager.GUI_CLAN_NAME_LENGTH.getMessage()), "", "§7" + state + "§8: §b" + Options.CLAN_NAME_LENGTH.getInt() + " " + LanguageManager.GUI_CHARACTERS.getMessage(), "", LanguageManager.GUI_CONFIGURE.getMessage().replace("%option%", "" + LanguageManager.GUI_CLAN_NAME_LENGTH.getMessage()));
		
		inv.addButton(new ItemButton(2, globalchat) {
			@Override
			public void onClick(InventoryClickEvent e) {
				Options.GLOBALCHAT_ENABLED.set(!Options.GLOBALCHAT_ENABLED.getBoolean());
				this.setItem(ItemBuilder.setLore(ItemBuilder.getItem(Material.PAPER, "§3§n" + LanguageManager.GUI_GLOBALCHAT.getMessage()), "", "§7" + state + "§8: " + (Options.GLOBALCHAT_ENABLED.getBoolean() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", "" + LanguageManager.GUI_GLOBALCHAT.getMessage()).replace("%state%", (!Options.GLOBALCHAT_ENABLED.getBoolean() ? "§a" + on : "§c" + off))));
				
				p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_TOGGLED.getMessage().replace("%state%", (Options.GLOBALCHAT_ENABLED.getBoolean() ? "§a" + on : "§c" + off)).replace("%option%", LanguageManager.GUI_GLOBALCHAT.getMessage()));
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(4, tags) {
			@Override
			public void onClick(InventoryClickEvent e) {
				Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.set(!Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean());
				this.setItem(ItemBuilder.setLore(ItemBuilder.getItem(Material.NAME_TAG, "§3§n" + LanguageManager.GUI_PREFIX_AND_SUFFIX.getMessage()), "", "§7" + state + "§8: " + (Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() ? "§a" + enabled : "§c" + disabled), "", LanguageManager.GUI_TOGGLE.getMessage().replace("%option%", LanguageManager.GUI_PREFIX_AND_SUFFIX.getMessage()).replace("%state%", (!Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() ? "§a" + on : "§c" + off))));
				
				p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_TOGGLED.getMessage().replace("%state%", (Options.PLAYER_LAYOUT_PREFIX_AND_SUFFIX_ENABLED.getBoolean() ? "§a" + on : "§c" + off)).replace("%option%", LanguageManager.GUI_PREFIX_AND_SUFFIX.getMessage()));
				
				LayoutManager.onUpdate(true);
			}
		}.setClickSound(Sound.CLICK.bukkitSound()));
		
		inv.addButton(new ItemButton(6, nameLength) {
			@Override
			public void onClick(InventoryClickEvent e) {
				
				AnvilGUI.openAnvil(ClanSystem.getInstance(), p, new AnvilListener() {
					@Override
					public void onClick(AnvilClickEvent e) {
						e.setCancelled(true);
						e.setClose(false);
						
						if(e.getSlot().equals(AnvilSlot.NONE)) return;
						playSound(p);
						
						String input = e.getInput();
						
						if(input == null) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_ENTER_A_NUMBER.getMessage());
							return;
						}
						
						int count;
						
						try {
							count = Integer.parseInt(input);
						} catch(NumberFormatException ex) {
							p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_ENTER_A_NUMBER.getMessage());
							return;
						}
						
						Options.CLAN_NAME_LENGTH.set(count);
						p.sendMessage(LanguageManager.PREFIX.getMessage() + LanguageManager.GUI_CONFIGURED.getMessage().replace("%option%", "" + LanguageManager.GUI_CLAN_NAME_LENGTH.getMessage()));
						p.closeInventory();
						ClanGUI.OPTIONS.open(p);
					}
					
					@Override
					public void onClose(AnvilCloseEvent e) {
						
					}
				}, ItemBuilder.getItem(Material.PAPER, LanguageManager.GUI_LENGTH.getMessage() + "..."));
				
			}
		}.setClickSound(Sound.CLICK.bukkitSound()).setCloseOnClick(true));
		
		inv.open(p);
	}
}
