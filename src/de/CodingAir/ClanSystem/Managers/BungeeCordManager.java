package de.CodingAir.ClanSystem.Managers;

import com.google.common.collect.Iterables;
import de.CodingAir.ClanSystem.ClanAPI;
import de.CodingAir.ClanSystem.ClanSystem;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Request;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.ClanSystem.Utils.Options;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxiedPlayer;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxyJoinEvent;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.ProxyQuitEvent;
import de.CodingAir.v1_6.CodingAPI.Server.Reflections.IReflection;
import de.CodingAir.v1_6.CodingAPI.Sockets.EchoClient;
import de.CodingAir.v1_6.CodingAPI.Sockets.SocketMessenger;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class BungeeCordManager {
	private EchoClient echoClient;
	private boolean connected = false;
	private boolean notified = false;
	private boolean alive = false;
	private int notAliveTicks = 0;
	
	private HashMap<String, UUID> uniqueIds = new HashMap<>();
	private List<ProxiedPlayer> online = new ArrayList<>();
	
	private HashMap<Integer, Callback<String>> callbacks = new HashMap<>();
	private int count = 0;
	private int received = 0;
	
	public void startClient(boolean debug, boolean onEnable, Callback<Boolean> callback) {
		if(!Options.BUNGEECORD.getBoolean()) {
			callback.accept(false);
			return;
		}
		
		echoClient = new EchoClient("localhost", ClanAPI.ECHO_PORT) {
			@Override
			public void onFail(Exception ex) {
				if(onEnable) {
					if(!connected) {
						if(!notified) {
							if(debug) ClanSystem.log("Could not connect to the EchoServer.");
							callback.accept(false);
						}
						
						notified = true;
					}
				} else {
					if(debug) ClanSystem.log("Could not connect to the EchoServer.");
					callback.accept(false);
				}
				
				try {
					if(echoClient.getEchoSocket() != null) echoClient.getEchoSocket().close();
				} catch(IOException e) {
				}
			}
		};
		
		if(debug) ClanSystem.log("Starting EchoClient on " + ClanAPI.ECHO_PORT + "...");
		
		echoClient.connect(ClanSystem.getInstance(), new SocketMessenger() {
			@Override
			public void onReceive(String update) {
				if(update.equals("Verification@BungeeCord")) {
					if(debug && !echoClient.isFailed()) ClanSystem.log("EchoClient started successfully.");
					connected = true;
					alive = true;
					callback.accept(true);
					return;
				}
				
				if(!connected) {
					echoClient.onFail(new IllegalArgumentException("Invalid EchoServer at port " + ClanAPI.ECHO_PORT));
					return;
				}
				
				if(update.equals("KEEP_ALIVE")) {
					alive = true;
					notAliveTicks = 0;
					return;
				}
				
				if(update.contains("|")) {
					Callback<String> callback = callbacks.get(received);
					callbacks.remove(received);
					received++;
					
					try {
						Request.Type type = Request.Type.valueOf(update.split("\\|")[0]);
						String info = update.split("\\|")[1];
						
						if(type != null) {
							if(type.equals(Request.Type.INVITE_CLAN)) {
								Clan clan = ClanSystem.getClanManager().getClan(info.split(" ")[0]);
								Clan target = ClanSystem.getClanManager().getClan(info.split(" ")[1]);
								
								if(clan != null && target != null) {
									Player targetLeader = Bukkit.getPlayer(target.getLeader());
									
									if(targetLeader != null) {
										String msg = LanguageManager.PREFIX.getMessage(null) + LanguageManager.CLAN_ALLIANCE_TARGET.getMessage(null).replace("%clan%", clan.getName());
										
										if(msg.contains("%yes%") && msg.contains("%/yes%") && msg.contains("%no%") && msg.contains("%/no%")) {
											String yes = msg.split("%yes%")[1].split("%/yes%")[0];
											String no = msg.split("%no%")[1].split("%/no%")[0];
											
											TextComponent message = new TextComponent(msg.split("%yes%")[0]);
											
											TextComponent accept = new TextComponent(yes);
											accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_ALLIANCE.getMessage(null) + " " + LanguageManager.COMMANDS_ACCEPT.getMessage(null) + " " + clan.getName()));
											accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage(null)).create()));
											
											TextComponent decline = new TextComponent(no);
											decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_ALLIANCE.getMessage(null) + " " + LanguageManager.COMMANDS_DECLINE.getMessage(null) + " " + clan.getName()));
											decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage(null)).create()));
											
											message.addExtra(accept);
											message.addExtra(msg.split("%/yes%")[1].split("%no%")[0]);
											message.addExtra(decline);
											message.addExtra(msg.split("%/no%")[1]);
											
											targetLeader.spigot().sendMessage(message);
										} else {
											targetLeader.sendMessage(msg);
										}
										
										ClanSystem.getClanManager().alliance(clan, target);
										clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage(null).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_ALLIANCE_CLAN.getMessage(null).replace("%clan%", target.getName()));
									}
								}
								
							} else if(type.equals(Request.Type.INVITE_PLAYER)) {
								Clan clan = ClanSystem.getClanManager().getClan(info.split(" ")[0]);
								Player target = Bukkit.getPlayer(info.split(" ")[1]);
								
								if(clan != null && target != null) {
									String msg = LanguageManager.PREFIX.getMessage(target) + LanguageManager.CLAN_PLAYER_INVITE.getMessage(target).replace("%clan%", clan.getName());
									
									if(msg.contains("%yes%") && msg.contains("%/yes%") && msg.contains("%no%") && msg.contains("%/no%")) {
										String yes = msg.split("%yes%")[1].split("%/yes%")[0];
										String no = msg.split("%no%")[1].split("%/no%")[0];
										
										TextComponent message = new TextComponent(msg.split("%yes%")[0]);
										
										TextComponent accept = new TextComponent(yes);
										accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_INVITE.getMessage(null) + " " + LanguageManager.COMMANDS_ACCEPT.getMessage(null) + " " + clan.getName()));
										accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage(null)).create()));
										
										TextComponent decline = new TextComponent(no);
										decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan " + LanguageManager.COMMANDS_INVITE.getMessage(null) + " " + LanguageManager.COMMANDS_DECLINE.getMessage(null) + " " + clan.getName()));
										decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LanguageManager.GUI_CLICK.getMessage(null)).create()));
										
										message.addExtra(accept);
										message.addExtra(msg.split("%/yes%")[1].split("%no%")[0]);
										message.addExtra(decline);
										message.addExtra(msg.split("%/no%")[1]);
										
										target.spigot().sendMessage(message);
									} else {
										target.sendMessage(msg);
									}
									
									ClanSystem.getClanManager().invite(clan, ClanSystem.getUUID(target));
									clan.broadcast(LanguageManager.CLAN_PREFIX.getMessage(null).replace("%clan_color%", ClanSystem.getClanManager().getClanColor(clan.getClanRank())).replace("%clanname%", clan.getName()) + LanguageManager.CLAN_PLAYER_INVITE_NOTIFY.getMessage(null).replace("%player%", target.getName()));
								}
								
							} else {
								if(callback != null)
									callback.accept((info == null || info.equalsIgnoreCase("null") ? null : info));
							}
						}
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
					return;
				}
				
				Update data = Update.fromString(update.split(" "));
				
				if(data.getType().equals(Update.Type.CLAN)) {
					Clan clan = ClanSystem.getClanManager().getClan(data.getInfo());
					
					if(clan != null) {
						if(data.getField() != null && !data.getField().equalsIgnoreCase("null")) {
							IReflection.FieldAccessor f = IReflection.getField(Clan.class, data.getField());
							f.set(clan, data.getValue());
							LayoutManager.onUpdate();
						}
					} else {
						try {
							JSONObject json = (JSONObject) new JSONParser().parse((String) data.getValue());
							clan = new Clan(json);
							ClanSystem.getClanManager().getClans().add(clan);
							LayoutManager.onUpdate();
						} catch(ParseException e) {
							e.printStackTrace();
						}
					}
				} else if(data.getType().equals(Update.Type.PLAYER)) {
					if(data.getInfo().equals("PLAYERS")) {
						List<String> information = (List<String>) data.getValue();
						
						String id = information.get(0);
						String name = information.get(1);
						String server = information.get(2);
						
						if(id == null || id.isEmpty() || id.equals("null")) return;
						if(name == null || name.isEmpty() || name.equals("null")) return;
						if(server == null || server.isEmpty() || server.equals("null")) return;
						
						ProxiedPlayer player = new ProxiedPlayer(server, UUID.fromString(id), name) {
							@Override
							public void sendMessage(String message) {
								request(new Request(Request.Type.SEND_MESSAGE, name, message));
							}
						};
						
						setOnline(player, true);
						//System.out.println("Player '" + player.getName() + "' was registered on '" + player.getServer() + "'.");
					} else if(data.getInfo().equals("JOIN")) {
						List<String> information = (List<String>) data.getValue();
						
						String id = information.get(0);
						String name = information.get(1);
						String server = information.get(2);
						
						if(id == null || id.isEmpty() || id.equals("null")) return;
						if(name == null || name.isEmpty() || name.equals("null")) return;
						if(server == null || server.isEmpty() || server.equals("null")) return;
						
						ProxiedPlayer player = new ProxiedPlayer(server, UUID.fromString(id), name) {
							@Override
							public void sendMessage(String message) {
								request(new Request(Request.Type.SEND_MESSAGE, name, message));
							}
						};
						
						ProxyJoinEvent event = new ProxyJoinEvent(player, server);
						Bukkit.getPluginManager().callEvent(event);
						
					} else if(data.getInfo().equals("QUIT")) {
						List<String> information = (List<String>) data.getValue();
						
						String id = information.get(0);
						String name = information.get(1);
						String server = information.get(2);
						
						if(id == null || id.isEmpty() || id.equals("null")) return;
						if(name == null || name.isEmpty() || name.equals("null")) return;
						if(server == null || server.isEmpty() || server.equals("null")) return;
						
						ProxiedPlayer player = new ProxiedPlayer(server, UUID.fromString(id), name) {
							@Override
							public void sendMessage(String message) {
								request(new Request(Request.Type.SEND_MESSAGE, name, message));
							}
						};
						
						ProxyQuitEvent event = new ProxyQuitEvent(player, server);
						Bukkit.getPluginManager().callEvent(event);
					} else if(data.getInfo().equals("REGISTER")) {
						List<String> information = (List<String>) data.getValue();
						
						String name = information.get(0);
						UUID uuid = UUID.fromString(information.get(1));
						
						uniqueIds.remove(name);
						uniqueIds.put(name, uuid);
					}
					
				} else if(data.getType().equals(Update.Type.ACTION)) {
					if(data.getInfo().equals("UPDATE_LAYOUT")) LayoutManager.onUpdate();
					if(data.getInfo().equals("DELETE")) {
						Clan clan = ClanSystem.getClanManager().getClan((String) data.getEncoding().decode((String) data.getValue()));
						if(clan != null) ClanSystem.getClanManager().removeClan(clan);
					}
				}
			}
			
			@Override
			public void onSend(String message) {
				
			}
			
			@Override
			public void onFail() {
				super.onFail();
			}
		});
	}
	
	public void onEnable(Callback<Boolean> callback) {
		if(!Options.BUNGEECORD.getBoolean() || !this.isConnected()) {
			callback.accept(true);
			startConnectionHelper();
			return;
		}
		
		Callback<Boolean> ready = new Callback<Boolean>() {
			@Override
			public void accept(Boolean object) {
				callback.accept(object);
				startConnectionHelper();
			}
		};
		
		refactorData(ready, false);
	}
	
	private void startConnectionHelper() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(ClanSystem.getInstance(), new Runnable() {
			boolean trying = false;
			int tests = 0;
			
			@Override
			public void run() {
				if(isConnected()) {
					if(notAliveTicks == 7) {
						alive = false;
						notAliveTicks = 0;
					} else notAliveTicks++;
					return;
				}
				
				if(!trying) {
					trying = true;
					
					if(tests == 0)
						ClanSystem.getInstance().getLogger().log(Level.WARNING, "EchoClient not connected anymore. Restarting...");
					
					startClient(false, false, new Callback<Boolean>() {
						@Override
						public void accept(Boolean connected) {
							if(connected) {
								ClanSystem.getInstance().getLogger().log(Level.INFO, "EchoClient started correctly.");
								refactorData(null, true);
								tests = 0;
							} else {
								if(tests == 0) {
									ClanSystem.getInstance().getLogger().log(Level.WARNING, "EchoClient could not connect to the EchoServer.");
									ClanSystem.getInstance().getLogger().log(Level.INFO, "EchoClient tries restarting in background...");
								}
								tests++;
							}
							
							trying = false;
						}
					});
				}
			}
		}, 20L, 20L);
	}
	
	public void refactorData(Callback<Boolean> isReady, boolean notify) {
		if(notify) ClanSystem.getInstance().getLogger().log(Level.WARNING, "Refactoring data...");
		
		Callback<Boolean> ready = new Callback<Boolean>() {
			@Override
			public void accept(Boolean got) {
				int refactorings = (got ? 1 : 0);
				
				int size = Bukkit.getOnlinePlayers().size();
				
				if(size == 0) {
					if(isReady != null) isReady.accept(true);
					if(notify)
						ClanSystem.getInstance().getLogger().log(Level.INFO, "Refactoring completed. [" + (refactorings) + "]");
					return;
				}
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(!isRegistered(p)) {
						refactorings++;
						register(p, new Callback<Boolean>() {
							@Override
							public void accept(Boolean object) {
								if(uniqueIds.size() == size && isReady != null) isReady.accept(true);
							}
						});
					}
				}
				
				if(notify)
					ClanSystem.getInstance().getLogger().log(Level.INFO, "Refactoring completed. [" + (refactorings) + "]");
			}
		};
		
		if(ClanSystem.SERVER == null) {
			ClanSystem.getInstance().getBungeeCordManager().getCurrentServer(new Callback<String>() {
				@Override
				public void accept(String server) {
					if(server != null && !server.equalsIgnoreCase("null")) {
						ClanSystem.SERVER = server;
						ClanSystem.getInstance().getBungeeCordManager().request(new Request(Request.Type.PLAYERS, ClanSystem.SERVER));
						ready.accept(true);
					} else {
						ready.accept(false);
					}
				}
			});
		} else {
			ready.accept(false);
		}
	}
	
	public void onDisable() {
		if(!Options.BUNGEECORD.getBoolean()) return;
		
		if(this.echoClient == null || this.echoClient.isFailed() || this.echoClient.getEchoSocket() == null || !this.echoClient.getEchoSocket().isConnected())
			return;
		
		try {
			sendMessage("ACTION|CLOSE");
			
			if(this.echoClient.getEchoSocket().isClosed()) return;
			
			this.echoClient.getEchoSocket().setKeepAlive(false);
			this.echoClient.getEchoSocket().close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void register(Player p) {
		if(this.isRegistered(p)) return;
		
		getUniqueId(p, new Callback<String>() {
			@Override
			public void accept(String uuid) {
				if(uuid != null) {
					uniqueIds.remove(p.getName());
					uniqueIds.put(p.getName(), UUID.fromString(uuid));
					Update update = new Update(Update.Type.PLAYER, "REGISTER", null, Update.Encoding.LIST, Arrays.asList(p.getName(), uuid));
					synchronize(update);
				}
			}
		});
	}
	
	public void register(Player p, Callback<Boolean> ready) {
		if(this.isRegistered(p)) return;
		
		getUniqueId(p, new Callback<String>() {
			@Override
			public void accept(String uuid) {
				if(uuid != null) {
					try {
						UUID uniqueId = UUID.fromString(uuid);
						
						uniqueIds.remove(p.getName());
						uniqueIds.put(p.getName(), uniqueId);
						Update update = new Update(Update.Type.PLAYER, "REGISTER", null, Update.Encoding.LIST, Arrays.asList(p.getName(), uuid));
						synchronize(update);
					} catch(IllegalArgumentException ex) {
						ClanSystem.getInstance().getLogger().log(Level.WARNING, "Could not load the UUID of '" + p.getName() + "'.");
					}
				}
				
				ready.accept(true);
			}
		});
	}
	
	public boolean isRegistered(Player p) {
		for(String key : this.uniqueIds.keySet()) {
			if(key.equals(p.getName()) && this.uniqueIds.get(key) != null) return true;
		}
		
		return false;
	}
	
	public void unregister(Player p) {
		uniqueIds.remove(p.getName());
	}
	
	public void getCurrentServer(Callback<String> callback) {
		if(ClanSystem.SERVER != null && !ClanSystem.SERVER.isEmpty()) {
			callback.accept(null);
			return;
		}
		
		Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if(p == null) {
			callback.accept(null);
			return;
		}
		
		Request request = new Request(Request.Type.GET_SERVER, p.getName());
		if(!request(request, callback)) callback.accept(null);
	}
	
	public void getUniqueId(Player p, Callback<String> callback) {
		if(ClanSystem.getInstance().getServer().getOnlineMode()) {
			callback.accept(p.getUniqueId().toString());
			return;
		} else if(!Options.BUNGEECORD.getBoolean()) {
			callback.accept(p.getUniqueId().toString());
			return;
		}
		
		request(new Request(Request.Type.UUID, p.getName()), callback);
	}
	
	public boolean synchronize(Update update) {
		if(!Options.BUNGEECORD.getBoolean()) return false;
		
		return sendMessage("UPDATE|" + update.toString());
	}
	
	public void request(Request request) {
		request(request, null);
	}
	
	public boolean request(Request request, Callback<String> callback) {
		if(callback != null) {
			this.callbacks.put(count, callback);
			count++;
		}
		
		return sendMessage(request.toString());
	}
	
	private boolean sendMessage(String message) {
		if(this.echoClient == null || this.echoClient.getMessenger() == null || this.echoClient.getEchoSocket() == null)
			return false;
		
		if(this.isConnected()) {
			if(this.echoClient.getEchoSocket().isConnected()) this.echoClient.getMessenger().send(message);
		}
		
		return this.echoClient.getEchoSocket().isConnected();
	}
	
	public HashMap<String, UUID> getUniqueIds() {
		return uniqueIds;
	}
	
	public void setOnline(ProxiedPlayer p, boolean online) {
		if(online) {
			this.online.add(p);
		} else {
			ProxiedPlayer original = null;
			for(ProxiedPlayer player : this.online) {
				if(player.getName().equals(p.getName())) original = player;
			}
			
			if(original != null) this.online.remove(original);
		}
	}
	
	public boolean isOnline(UUID uniqueId) {
		for(ProxiedPlayer player : this.online) {
			if(player.getUniqueId().toString().equals(uniqueId.toString())) return true;
		}
		
		return false;
	}
	
	public boolean isOnline(String name) {
		for(ProxiedPlayer player : this.online) {
			if(player.getName().equals(name)) return true;
		}
		
		return false;
	}
	
	public ProxiedPlayer getProxiedPlayer(UUID uniqueId) {
		for(ProxiedPlayer player : this.online) {
			if(player.getUniqueId().toString().equals(uniqueId.toString())) return player;
		}
		
		return null;
	}
	
	public ProxiedPlayer getProxiedPlayer(String name) {
		for(ProxiedPlayer player : this.online) {
			if(player.getName().equals(name)) return player;
		}
		
		return null;
	}
	
	public List<ProxiedPlayer> getProxiedPlayers() {
		return online;
	}
	
	public boolean isConnected() {
		if(this.echoClient == null || this.echoClient.getMessenger() == null || this.echoClient.getEchoSocket() == null)
			return false;
		return this.echoClient != null && this.echoClient.getMessenger().isAlive() && this.echoClient.getEchoSocket().isConnected() && !this.echoClient.isFailed() && this.echoClient.getEchoSocket().isBound() && alive;
	}
}
