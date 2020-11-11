package me.specifies.AutoMiner.Events;

import java.sql.SQLException;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.specifies.AutoMiner.AutoMiner;
import me.specifies.AutoMiner.Utils.SQLManager;

public class AddLogoutData implements Listener {
	
	private AutoMiner plugin;
	public AddLogoutData() {
		this.plugin = AutoMiner.getInstance();
	}
	
	@EventHandler
	public void leave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		BukkitRunnable run = new BukkitRunnable() {
			
			public void run() {
				
				SQLManager manager = plugin.getManager();
				manager.reset();
					
				Date date = new Date();
				
				try {
					if(manager.getTimestamp(p.getUniqueId().toString()).equals("-1")) manager.setTimestamp(p.getUniqueId().toString(), Long.toString(date.getTime() / 1000));
				} catch (SQLException err) {
					// TODO Auto-generated catch block
					err.printStackTrace();
				}
					

				
				
			}
			
		};
		
		run.runTaskAsynchronously(plugin);
		
		
		
	}
	

}
