package de.CodingAir.ClanSystem.Utils.BungeeCord;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class Request {
	public enum Type {PLAYERS, GET_SERVER, UUID, SEND_MESSAGE, INVITE_CLAN, INVITE_PLAYER}
	
	private Type type;
	private String[] info;
	
	public Request(Type type, String... info) {
		this.type = type;
		this.info = info;
	}
	
	public Type getType() {
		return type;
	}
	
	public String[] getInfo() {
		return info;
	}
	
	@Override
	public String toString() {
		String info = "";
		for(String s : this.info) {
			info += s + " ";
		}
		
		return this.type.name() + "|" + (info.isEmpty() ? "" : info.substring(0, info.length() - 1));
	}
}
