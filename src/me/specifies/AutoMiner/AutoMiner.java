package me.specifies.AutoMiner;


import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.specifies.AutoMiner.Commands.AutoMinerCommand;
import me.specifies.AutoMiner.Events.AddLogoutData;
import me.specifies.AutoMiner.Events.AlertOfRewards;
import me.specifies.AutoMiner.Events.CreateDefaultData;
import me.specifies.AutoMiner.Events.IslandEvents;
import me.specifies.AutoMiner.Utils.SQLManager;
import net.md_5.bungee.api.ChatColor;


public class AutoMiner extends JavaPlugin {
	
	private static AutoMiner instance;
	
	private final SQLManager manager = new SQLManager(this.getConfig().getString("sql-lite-file"), this.getDataFolder());
	
	public void onEnable() { 
		
		instance = this;
		
		this.saveDefaultConfig();
		
		registerEvents();
		registerCommands();
		
		try {
			this.manager.setup();
			Bukkit.getLogger().info("[AutoMiner] A connection has been established to the SQLite database.");
		} catch (IOException err) {
			Bukkit.getLogger().log(Level.WARNING, "Unable to create database file. Disabling AutoMiner. Stack Trace:");
			err.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		} catch(ClassNotFoundException err) {
			Bukkit.getLogger().log(Level.WARNING, "There was no SQLite driver found. This plugin will not work until the proper drivers are installed.");
			Bukkit.getPluginManager().disablePlugin(this);
		} catch(SQLException err) {
			err.printStackTrace();
		}
		

	
		
	}
	
	public void onDisable() {
		
		instance = null;
	}
	
	public static AutoMiner getInstance() {
		return instance;
	}
	
	public SQLManager getManager() {
		return manager;
	}
	
	public String color(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}
	
	private void registerCommands() {
		getCommand("autominer").setExecutor(new AutoMinerCommand());
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new CreateDefaultData(), this);
		pm.registerEvents(new IslandEvents(), this);
		pm.registerEvents(new AddLogoutData(), this);
		pm.registerEvents(new AlertOfRewards(), this);
	}
	

	
	

}
