package me.specifies.AutoMiner.Events;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.specifies.AutoMiner.AutoMiner;
import me.specifies.AutoMiner.Utils.SQLManager;

public class CreateDefaultData implements Listener {
	
	private AutoMiner plugin;
	public CreateDefaultData() {
		this.plugin = AutoMiner.getInstance();
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		

		// Run all SQL executions on separate threads
		BukkitRunnable run = new BukkitRunnable() {
			
			@Override
			public void run() {
				SQLManager manager = plugin.getManager();
				manager.reset();
				
				boolean exists = manager.checkIfPlayerExists(p.getUniqueId().toString());
				
				if(exists) return;
				
				try {
					manager.createPlayer(p.getUniqueId().toString());
					System.out.println("[AutoMiner] New player added to the database.");
				} catch(SQLException err) {
					err.printStackTrace();
				}
				
			}
			
		};
		
		run.runTaskAsynchronously(plugin);
	}
	

}
