package me.specifies.AutoMiner.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;

import me.specifies.AutoMiner.AutoMiner;
import me.specifies.AutoMiner.Utils.SQLManager;

public class IslandEvents implements Listener {
	
	private AutoMiner plugin;
	public IslandEvents() {
		this.plugin = AutoMiner.getInstance();
	}
	
	@EventHandler
	public void create(IslandCreateEvent e) {
		
		BukkitRunnable run = new BukkitRunnable() {
			public void run() {
				SQLManager manager = plugin.getManager();
				manager.reset();
				
				String result = Integer.toString(e.getIsland().getId());
			
				manager.setID(e.getPlayer().getUniqueId().toString(), result);
			
				
			}
		};
		
		run.runTaskAsynchronously(plugin);
		
	}
	

	public void delete(IslandDeleteEvent e) {
		
		BukkitRunnable run = new BukkitRunnable() {
			
			public void run() {
				SQLManager manager = plugin.getManager();
				
				manager.setID(e.getIsland().getOwner(), "");
			}
			
		};
		
		run.runTaskAsynchronously(plugin);
		
	}
	


}
