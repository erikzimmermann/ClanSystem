package de.CodingAir.ClanSystem.BungeeCord;

import de.CodingAir.ClanSystem.ClanAPI;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Request;
import de.CodingAir.ClanSystem.Utils.BungeeCord.Update;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.Files.FileManager;
import de.CodingAir.v1_6.CodingAPI.Sockets.EchoServer;
import de.CodingAir.v1_6.CodingAPI.Sockets.SocketMessenger;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Level;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class BungeeUpdater extends Plugin {
	private static BungeeUpdater instance;
	private EchoServer echoServer;
	
	private HashMap<String, UUID> uniqueIds = new HashMap<>();
	private FileManager fileManager = new FileManager(this);
	private List<String> connectedServers = new ArrayList<>();
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.fileManager.loadFile("BungeeCord", "/");
		ClanAPI.ECHO_PORT = this.fileManager.getFile("BungeeCord").getConfig().getInt("Synchronization.Server.Port");
		
		BungeeCord.getInstance().getPluginManager().registerListener(this, new Listener());
		
		this.getLogger().log(Level.INFO, "Starting EchoServer on " + ClanAPI.ECHO_PORT + "...");
		
		echoServer = new EchoServer(ClanAPI.ECHO_PORT) {
			@Override
			public SocketMessenger onConnect(Socket socket) {
				getLogger().log(Level.INFO, "Connecting with new client...");
				
				try {
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					SocketMessenger messenger = new SocketMessenger(socket, in, out) {
						@Override
						public void onReceive(String update) {
							if(update.contains("|")) {
								if(update.startsWith("UPDATE")) {
									update = update.replaceFirst("UPDATE\\|", "");
									
									Update data = Update.fromString(update.split(" "));
									
									sendToAll(data.toString(), socket);
									
									return;
								}
								
								if(update.startsWith("ACTION")) {
									update = update.replaceFirst("ACTION\\|", "");
									
									switch(update) {
										case "CLOSE": {
											try {
												this.setAlive(false);
												socket.setKeepAlive(false);
												socket.close();
											} catch(SocketException e) {
												e.printStackTrace();
											} catch(IOException e) {
												e.printStackTrace();
											}
											break;
										}
									}
									
									return;
								}
								
								
								try {
									Request.Type type = Request.Type.valueOf(update.split("\\|")[0]);
									
									if(type != null) {
										String info = update.replaceFirst(type.name() + "\\|", "");
										
										if(type.equals(Request.Type.GET_SERVER)) {
											ProxiedPlayer p = BungeeCord.getInstance().getPlayer(info);
											
											if(p == null) {
												send("GET_SERVER|null");
											} else {
												if(!connectedServers.contains(p.getServer().getInfo().getName())) connectedServers.add(p.getServer().getInfo().getName());
												this.setServer(p.getServer());
												send("GET_SERVER|" + p.getServer().getInfo().getName());
											}
										} else if(type.equals(Request.Type.UUID)) {
											ProxiedPlayer p = BungeeCord.getInstance().getPlayer(info);
											
											if(p == null) {
												send("UUID|null");
											} else {
												send("UUID|" + p.getUniqueId().toString());
											}
										} else if(type.equals(Request.Type.SEND_MESSAGE)) {
											ProxiedPlayer p = BungeeCord.getInstance().getPlayer(info.split(" ")[0]);
											
											if(p == null) {
												send("UUID|null");
											} else {
												String message = info.replaceFirst(p.getName() + " ", "");
												p.sendMessage(new TextComponent(message));
											}
										} else if(type.equals(Request.Type.PLAYERS)) {
											BungeeCord.getInstance().getServers().forEach((name, data) -> {
												if(!data.getName().equalsIgnoreCase(info) && connectedServers.contains(data.getName())) {
													data.getPlayers().forEach(player -> {
														Update toClient = new Update(Update.Type.PLAYER, "PLAYERS", null, Update.Encoding.LIST, Arrays.asList(player.getUniqueId().toString(), player.getName(), player.getServer().getInfo().getName()));
														send(toClient.toString());
													});
												}
											});
										} else if(type.equals(Request.Type.INVITE_PLAYER)) {
											ProxiedPlayer p = BungeeCord.getInstance().getPlayer(info.split(" ")[1]);
											
											for(SocketMessenger client : echoServer.getMessengers()) {
												if(client.getServer() != null && client.getServer().getInfo().getName().equals(p.getServer().getInfo().getName())) {
													client.send(update);
													break;
												}
											}
										} else if(type.equals(Request.Type.INVITE_CLAN)) {
											ProxiedPlayer p = BungeeCord.getInstance().getPlayer(info.split(" ")[2]);
											
											for(SocketMessenger client : echoServer.getMessengers()) {
												if(client.getServer() != null && client.getServer().getInfo().getName().equals(p.getServer().getInfo().getName())) {
													client.send(update);
													break;
												}
											}
										}
										
										return;
									}
								} catch(Exception ex) {
									send("REQUEST|ERROR");
								}
							}
						}
						
						@Override
						public void onSend(String message) {
						}
						
						@Override
						public void onFail() {
							try {
								getLogger().log(Level.SEVERE, "Connection throttled.");
								socket.close();
							} catch(IOException e) {
								e.printStackTrace();
							}
						}
					};
					
					messenger.send("Verification@BungeeCord");
					
					getLogger().log(Level.INFO, "Connected to new client.");
					return messenger;
				} catch(IOException ex) {
					try {
						getLogger().log(Level.WARNING, "Could not connect to the client.");
						socket.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
				
				getLogger().log(Level.WARNING, "Could not connect to the client.");
				return null;
			}
		};
		
		if(echoServer.start(this)) {
			this.getLogger().log(Level.INFO, "EchoServer started successfully.");
		} else {
			this.getLogger().log(Level.INFO, "Could not start EchoServer, please change the port!");
		}
	}
	
	@Override
	public void onDisable() {
		if(this.echoServer != null) {
			this.getLogger().log(Level.INFO, "Stopping EchoServer...");
			
			try {
				this.echoServer.getServerSocket().close();
			} catch(Exception e) {
				
			}
			
			this.getLogger().log(Level.INFO, "EchoServer stopped successfully.");
		}
	}
	
	public EchoServer getEchoServer() {
		return echoServer;
	}
	
	public HashMap<String, UUID> getUniqueIds() {
		return uniqueIds;
	}
	
	public static BungeeUpdater getInstance() {
		return instance;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
}
