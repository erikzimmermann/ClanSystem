package de.CodingAir.ClanSystem.Managers;

import de.CodingAir.ClanSystem.ClanSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public enum FileManager {
	CONFIG(new ConfigFile("Config", "/", ClanSystem.getInstance())),
	LANGUAGE(new ConfigFile("Language", "/", ClanSystem.getInstance())),
	CLANS(new ConfigFile("Clans", "/Memory/", ClanSystem.getInstance())),
	MYSQL(new ConfigFile("MySQL", "/", ClanSystem.getInstance())),
	ECONOMY(new ConfigFile("Economy", "/", ClanSystem.getInstance())),
	DATA(new ConfigFile("Data", "/Memory/", ClanSystem.getInstance()));
	
	private ConfigFile configFile;
	
	private FileManager(ConfigFile file) {
		this.configFile = file;
	}
	
	public ConfigFile getFile() {
		return this.configFile;
	}
	
	
	public static class ConfigFile {
		private Plugin plugin;
		private FileConfiguration config = null;
		private File configFile = null;
		private String name;
		private String path;
		
		public ConfigFile(String name, String path, Plugin plugin) {
			this.name = name;
			this.path = path;
			this.plugin = plugin;
			
			this.saveDefaultConfig();
			
			this.init();
			
			this.config.options().copyDefaults(true);
			this.config.options().copyHeader(true);
			this.saveConfig();
		}
		
		public void init() {
			if(configFile == null) configFile = new File(plugin.getDataFolder(), this.path + this.name + ".yml");
			config = YamlConfiguration.loadConfiguration(configFile);
			
			Reader defConfigStream = null;
			try{
				defConfigStream = new InputStreamReader(plugin.getResource(name + ".yml"), "UTF8");
			} catch(Exception e) {
			}
			
			if(defConfigStream != null){
				YamlConfiguration GGmapsdefConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				config.setDefaults(GGmapsdefConfig);
			}
		}
		
		public void reloadConfig() {
			this.configFile = null;
			this.config = null;
			
			this.init();
		}
		
		public FileConfiguration getConfig() {
			if(config == null){
				reloadConfig();
			}
			
			return config;
		}
		
		public void saveConfig() {
			if(config == null || configFile == null){
				return;
			}
			try{
				getConfig().save(configFile);
				this.reloadConfig();
			} catch(IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
			}
		}
		
		public void saveDefaultConfig() {
			if(configFile == null){
				configFile = new File(plugin.getDataFolder(), this.path + this.name + ".yml");
			}
			
			if(!configFile.exists()){
				try{
					plugin.saveResource(this.name + ".yml", false);
				} catch(IllegalArgumentException ex) {
				}
			}
		}
		
		public String getName() {
			return name;
		}
		
		public String getPath() {
			return path;
		}
	}
}
