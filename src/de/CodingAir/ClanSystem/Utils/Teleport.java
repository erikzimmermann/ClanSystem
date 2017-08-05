package de.CodingAir.ClanSystem.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleport {
	private Player player;
	private Location location;
	private int timeLeft;
	private boolean canMove;
	
	public Teleport(Player player, Location location, int timeLeft, boolean canMove) {
		this.player = player;
		this.location = location;
		this.timeLeft = timeLeft;
		this.canMove = canMove;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}
	
	public boolean canMove() {
		return canMove;
	}
}
