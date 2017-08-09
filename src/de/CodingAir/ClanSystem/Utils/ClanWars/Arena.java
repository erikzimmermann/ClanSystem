package de.CodingAir.ClanSystem.Utils.ClanWars;

import de.CodingAir.ClanSystem.Utils.Clan;
import de.CodingAir.v1_6.CodingAPI.Tools.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author CodingAir
 * @verions: 1.0.0
 **/

public class Arena {
	private String name;
	private Location[] spawns = new Location[2];
	private Clan[] fighting = new Clan[2];
	
	public Arena(String name, Location[] spawns) {
		this.name = name;
		this.spawns = spawns;
	}
	
	public Arena(String jsonCode) {
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonCode);
			
			this.name = (String) json.get("Name");
			this.spawns[0] = Location.getByJSONString((String) json.get("Spawn_0"));
			this.spawns[1] = Location.getByJSONString((String) json.get("Spawn_1"));
			
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Location[] getSpawns() {
		return spawns;
	}
	
	public Clan[] getFighting() {
		return fighting;
	}
	
	public boolean isEmpty() {
		return this.fighting[0] == null && this.fighting[1] == null;
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		
		json.put("Name", this.name);
		json.put("Spawn_0", this.spawns[0].toJSONString());
		json.put("Spawn_1", this.spawns[1].toJSONString());
		
		return json.toString();
	}
}
