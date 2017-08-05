package de.CodingAir.ClanSystem.Utils.ClanWars;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class ProxyArena {
	private String name;
	private String server;
	private boolean isEmpty;
	
	public ProxyArena(String name, String server, boolean isEmpty) {
		this.name = name;
		this.server = server;
		this.isEmpty = isEmpty;
	}
	
	public String getName() {
		return name;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public void setEmpty(boolean empty) {
		isEmpty = empty;
	}
}
