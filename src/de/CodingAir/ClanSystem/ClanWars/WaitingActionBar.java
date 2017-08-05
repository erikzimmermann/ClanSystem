package de.CodingAir.ClanSystem.ClanWars;

import de.CodingAir.v1_4.CodingAPI.Player.MessageAPI;
import org.bukkit.entity.Player;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class WaitingActionBar {
	private String BACKGROUND_COLOR = "§8";
	private String BAR_COLOR = "§c";
	private String BAR_SEGMENT = "█";
	private int BAR_PIECES = 20;
	private Player p;
	private String text;
	private int current = 0;
	private int length;
	
	public WaitingActionBar(Player p, String text, int length) {
		this.p = p;
		this.text = text;
		this.length = (length > BAR_PIECES ? BAR_PIECES : length);
	}
	
	public void onTick() {
		String bar = "";
		
		int toMuch = current + length - BAR_PIECES;
		
		for(int i = 0; i < this.BAR_PIECES; i++) {
			if(toMuch > 0) {
				bar += this.BAR_COLOR + this.BAR_SEGMENT;
				toMuch--;
				continue;
			}
			
			if(i == this.BAR_PIECES / 2) bar += "§r" + this.text + "§r";
			
			if(i < this.current || i >= this.current + this.length) bar += this.BACKGROUND_COLOR + this.BAR_SEGMENT;
			else if(i >= this.current && i < this.current + this.length) bar += this.BAR_COLOR + this.BAR_SEGMENT;
		}
		
		MessageAPI.sendActionBar(p, bar);
		
		this.current++;
		if(current >= BAR_PIECES) current = 0;
	}
	
	public void end() {
		MessageAPI.sendActionBar(p, "");
	}
}
