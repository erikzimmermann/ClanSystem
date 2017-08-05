package de.CodingAir.ClanSystem.Utils;

public class Invite {
	private Object handler;
	private Object target;
	private int expire;
	private boolean expired = false;
	
	public Invite(Object handler, Object target, int expire) {
		this.handler = handler;
		this.target = target;
		this.expire = expire;
	}
	
	public Object getHandler() {
		return handler;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public int getExpire() {
		return expire;
	}
	
	public void setExpire(int expire) {
		this.expire = expire;
	}
	
	public boolean isExpired() {
		return expired;
	}
	
	public void setExpired(boolean expired) {
		this.expired = expired;
	}
}
