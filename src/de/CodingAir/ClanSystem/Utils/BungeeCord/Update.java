package de.CodingAir.ClanSystem.Utils.BungeeCord;

import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Update {
	public enum Type {CLAN, PLAYER, ACTION}
	
	public enum Encoding {
		STRING, INT, BOOLEAN, LIST, MAP, LOCATION, CUSTOM_LOCATION, ITEMSTACK;
		
		public Object decode(String code) {
			if(this.equals(STRING)) return code;
			if(this.equals(INT)) return Integer.parseInt(code);
			if(this.equals(BOOLEAN)) return Boolean.parseBoolean(code);
			if(this.equals(MAP)) return Update.stringToMap(code);
			if(this.equals(LIST)) return Update.stringToList(code);
			if(this.equals(LOCATION)) return Update.stringToLoc(code);
			if(this.equals(CUSTOM_LOCATION)) return de.CodingAir.v1_6.CodingAPI.Tools.Location.getByJSONString(code);
			if(this.equals(ITEMSTACK)) return OldItemBuilder.translateSimple(code);
			return null;
		}
		
		public String encode(Object code) {
			if(this.equals(STRING)) return (String) code;
			if(this.equals(INT)) return code + "";
			if(this.equals(BOOLEAN)) return code + "";
			if(this.equals(MAP)) return Update.mapToString((HashMap<String, String>) code);
			if(this.equals(LIST)) return Update.listToString((List<String>) code);
			if(this.equals(LOCATION)) return Update.locToString((Location) code);
			if(this.equals(CUSTOM_LOCATION)) return ((de.CodingAir.v1_6.CodingAPI.Tools.Location) code).toJSONString();
			if(this.equals(ITEMSTACK)) return OldItemBuilder.translateSimple((ItemStack) code);
			return null;
		}
	}
	
	private Type type;
	private String info;
	private String field;
	private Encoding encoding;
	private Object value;
	
	public Update(Type type, String info, String field, Encoding encoding, Object value) {
		this.type = type;
		this.info = info;
		this.field = field;
		this.encoding = encoding;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getInfo() {
		return info;
	}
	
	public String getField() {
		return field;
	}
	
	public Encoding getEncoding() {
		return encoding;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return (this.type == null ? null : this.type.name()) + " " + this.info + " " + this.field + " " + (this.encoding == null ? null : this.encoding.name()) + " " + (this.encoding == null ? null : encoding.encode(this.value));
	}
	
	public static Update fromString(String[] data) {
		Update.Type type = Update.Type.valueOf(data[0]);
		String info = data[1];
		String field = data[2];
		Update.Encoding encoding = (data[3] == null || data[3].equalsIgnoreCase("null") ? null : Update.Encoding.valueOf(data[3]));
		String value = (data.length == 5 ? data[4] : "");
		
		return new Update(type, info, field, encoding, (encoding != null ? encoding.decode(value) : value));
	}
	
	private static String mapToString(HashMap<String, String> map) {
		String res = "";
		
		for(String key : map.keySet()) {
			res = res + key + "#" + map.get(key) + ";";
		}
		
		if(!res.isEmpty()) res = res.substring(0, res.length() - 1);
		
		return res;
	}
	
	private static HashMap<String, String> stringToMap(String code) {
		HashMap<String, String> map = new HashMap<>();
		
		if(code == null || code.isEmpty()) return map;
		
		String[] codes = code.split(";");
		
		for(String info : codes) {
			String name = info.split("#")[0];
			String uuid = info.split("#")[1];
			
			map.put(name, uuid);
		}
		
		return map;
	}
	
	private static String listToString(List<String> list) {
		String res = "";
		
		for(String key : list) {
			res = res + key + ";";
		}
		
		if(!res.isEmpty()) res = res.substring(0, res.length() - 1);
		
		return res;
	}
	
	private static List<String> stringToList(String code) {
		List<String> list = new ArrayList<>();
		
		if(code == null || code.isEmpty()) return list;
		
		String[] codes = code.split(";");
		
		for(String info : codes) {
			list.add(info);
		}
		
		return list;
	}
	
	private static String locToString(Location l) {
		if(l == null) return null;
		
		String ret;
		
		World w = l.getWorld();
		
		if(w == null) return null;
		
		double x = l.getX();
		double y = l.getY();
		double z = l.getZ();
		double yaw = l.getYaw();
		double pitch = l.getPitch();
		
		DecimalFormat format = new DecimalFormat("0.00");
		
		ret = w.getName() + ";" + format.format(x) + ";" + format.format(y) + ";" + format.format(z) + ";" + format.format(yaw) + ";" + format.format(pitch);
		return ret;
	}
	
	private static Location stringToLoc(String s) {
		if(s == null) return null;
		
		s = s.replaceAll(",", ".");
		
		String[] a = s.split("\\;");
		
		World w = Bukkit.getWorld(a[0]);
		
		float x = Float.parseFloat(a[1]);
		float y = Float.parseFloat(a[2]);
		float z = Float.parseFloat(a[3]);
		float yaw = Float.parseFloat(a[4]);
		float pitch = Float.parseFloat(a[5]);
		
		return new Location(w, (float) x, (float) y, (float) z, (float) yaw, (float) pitch);
	}
}
