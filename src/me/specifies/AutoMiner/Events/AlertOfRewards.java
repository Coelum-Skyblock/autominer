package me.specifies.AutoMiner.Events;

import java.sql.SQLException;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.specifies.AutoMiner.AutoMiner;
import me.specifies.AutoMiner.Utils.SQLManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AlertOfRewards implements Listener {

	private AutoMiner plugin;
	public AlertOfRewards() {
		this.plugin = AutoMiner.getInstance();
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		BukkitRunnable run = new BukkitRunnable() {
			
			public void run() {
				SQLManager manager = plugin.getManager();
				
				manager.reset();
				
				try {
					long timestamp = Long.parseLong(manager.getTimestamp(p.getUniqueId().toString()));
					Date now = new Date();
					
					long currentTimestamp = now.getTime() / 1000;
					
					long difference = currentTimestamp - timestamp;
					
					Date calculated = new Date(difference * 1000);
					
					int minutes = calculated.getMinutes();
					
					if(minutes >= 1) {
						
						
						TextComponent serializedMessage = new TextComponent(plugin.color("&bYou have rewards to claim from your AutoMiner. Click"));
						
						TextComponent here = new TextComponent(plugin.color(" &c&l[HERE] "));
						here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/am"));
						
						TextComponent ending = new TextComponent (plugin.color(" &bto claim them."));
						
						serializedMessage.addExtra(here);
						serializedMessage.addExtra(ending);
						
						// delay this message to allow the dmotd to be sent, etc
						new BukkitRunnable() {
							public void run() {
								p.spigot().sendMessage(serializedMessage);
							}
						}.runTaskLater(plugin, 40);
						
					}
					
				} catch(SQLException err) {
					err.printStackTrace();
				}
				

				
			}
		};
		
		run.runTaskAsynchronously(plugin);
		
	}
	
}
