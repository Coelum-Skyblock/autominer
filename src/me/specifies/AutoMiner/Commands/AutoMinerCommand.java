package me.specifies.AutoMiner.Commands;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;

import me.specifies.AutoMiner.AutoMiner;
import me.specifies.AutoMiner.Utils.DateUtilities;
import me.specifies.AutoMiner.Utils.SQLManager;

public class AutoMinerCommand implements CommandExecutor {
	
	private AutoMiner plugin;
	public AutoMinerCommand() {
		this.plugin = AutoMiner.getInstance();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			// run it on another thread
			BukkitRunnable run = new BukkitRunnable() {
				public void run() {
					// grab our sql manager and reset the connection
					SQLManager manager = plugin.getManager();
					manager.reset();
					
					try {
						// get the last logout time, and the island id of the player
						String timestampQuery = manager.getTimestamp(p.getUniqueId().toString());
						String island = manager.getID(p.getUniqueId().toString());
						
						// if the timestamp is empty, or equals 0 the player doesn't exist in the db or hasn't logged out since creation.
						if(timestampQuery.equals("") || timestampQuery.equals("0")) {
							p.sendMessage(plugin.color("&cCould not retrieve information on your last login time."));
							return;
						}
						
						// if the timestamp equals -1, they've already claimed their rewards
						if(timestampQuery.equals("-1")) {
							p.sendMessage(plugin.color("&cYou have already claimed your rewards."));
							return;
						}
						
						// if there's no island, instruct the player that they must create an island
						if(island.equals("")) {
							p.sendMessage(plugin.color("&cYou must create an island in order to use this command."));
							return;
						}
						
						
						// get the minute different between now and the time they logged off
						DateUtilities du = new DateUtilities();
						
						int minutes = du.getMinuteDifference(timestampQuery);
						
						
						if(minutes >= 1) {
							// get the island from our dependency plugin
							Island is = IridiumSkyblock.getIslandManager().getIslandViaId(Integer.parseInt(island));
							
							// get the island level
							int level = (int) is.getValue();
							
							// set the modifier based on the increase and modifier itself
							double modifier = 1 + ((level / plugin.getConfig().getInt("multiplier-increase")) * plugin.getConfig().getDouble("modifier"));
							
							// get our rates, multiply them by modifier, then by the minutes past
							int diamonds = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.diamonds")) * minutes);
							int cobble = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.cobblestone")) * minutes);
							int ironore = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.iron_ore")) * minutes);
							int goldore = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.gold_ore")) * minutes);
							int coal = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.coal")) * minutes);
							int redstone = (int) Math.floor((modifier * plugin.getConfig().getInt("rates.redstone")) * minutes);
							
							try {
								// set the value to -1
								manager.setTimestamp(p.getUniqueId().toString(), "-1");
								
								// add the items to the player's inventory. Might add a system that drops the items that couldn't be added on the ground.
								add(p, new ItemStack(Material.DIAMOND, diamonds));
								add(p, new ItemStack(Material.COBBLESTONE, cobble));
								add(p, new ItemStack(Material.IRON_ORE, ironore));
								add(p, new ItemStack(Material.GOLD_ORE, goldore));
								add(p, new ItemStack(Material.COAL, coal));
								add(p, new ItemStack(Material.REDSTONE, redstone));
								
								// send the player a message telling them what items they earned while offline
								p.sendMessage(plugin.color("&8&m--------&8[&c&lRewards Claimed&8]&m--------"));
								p.sendMessage(plugin.color("&6&lIsland Level Modifier&7: &e" + new DecimalFormat("#.##").format(modifier - 1)));
								p.sendMessage(plugin.color("&6&lDiamonds&7: &e" + Integer.toString(diamonds)));
								p.sendMessage(plugin.color("&6&lCobblestone&7: &e" + Integer.toString(cobble)));
								p.sendMessage(plugin.color("&6&lIron Ore&7: &e" + Integer.toString(ironore)));
								p.sendMessage(plugin.color("&6&lGold Ore &7: &e" + Integer.toString(goldore)));
								p.sendMessage(plugin.color("&6&lCoal&7: &e" + Integer.toString(coal)));
								p.sendMessage(plugin.color("&6&lRedstone&7: &e" + Integer.toString(redstone)));
								p.sendMessage(plugin.color("&8&m---------------------------------"));
								
								
							} catch(SQLException err) {
								err.printStackTrace();
							}
							
							
							
							
						} else p.sendMessage(plugin.color("&cThere are no rewards to collect."));
						
					} catch(SQLException err) {
						p.sendMessage(plugin.color("&cThere was an internal error with grabbing your playerdata. Please alert the Admins."));
						err.printStackTrace();
					}
					
				}
			};
			
			run.runTaskAsynchronously(plugin);
			
			
		} else sender.sendMessage(plugin.color("&cYou must be a player to use this command."));
		
		return true;
	}
	
	private void add(Player p, ItemStack itemstack) {
		p.getInventory().addItem(itemstack);
	}
}
