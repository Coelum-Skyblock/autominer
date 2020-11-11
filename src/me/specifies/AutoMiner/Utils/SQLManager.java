package me.specifies.AutoMiner.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.specifies.AutoMiner.AutoMiner;

public class SQLManager {
	
	private final String fileName;
	private final File dataFolder;
	
	private Connection connection;
	
	public SQLManager(String fileName, File dataFolder) {
		this.fileName = fileName;
		this.dataFolder = dataFolder;
	}
	
	/**
	 * Function to establish a connection to the SQLite database.
	 * @throws SQLException There was an error in either establishing a connection or creating the default tables.
	 * @throws ClassNotFoundException The proper drivers aren't installed on the system.
	 * @throws IOException The plugin was unable to create the database file.
	 */
	public void setup() throws SQLException, ClassNotFoundException, IOException {
		// if the connection is already in memory, or isn't closed, we don't want to run these operations
		if(connection != null && !connection.isClosed()) return;
		
		File sqlite;
		
		// This bit of code is designed to mitigate user error. If the user doesn't include .db to end the file in the config, we append it for them.
		if(!this.fileName.substring(this.fileName.length() - 3).equalsIgnoreCase(".db")) sqlite = new File(this.dataFolder, this.fileName + ".db");
		else sqlite = new File(this.dataFolder, this.fileName);
		
		// create the db if it doesn't exist.
		if(!(sqlite.exists())) sqlite.createNewFile();
		
		// make sure the system is running the sqlite driver
		Class.forName("org.sqlite.JDBC");
		
		// establish a connection
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + File.separator + this.fileName);
		
		// create our base tables, if they don't exist.
		Statement stmt = this.connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS autominer(uuid TEXT, lastLogout TEXT, islandID TEXT)");
	}
	
	/**
	 * Function to reset the internal SQLManager's connection. 
	 * This is to prevent the database to locking because of the inherent way we manage the manager.
	 */
	public void reset() {
		try {
			this.connection.close();
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + File.separator + this.fileName);
		} catch(SQLException err) {
			err.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, "[AutoMiner] Could not refresh the database connection. Shutting down.");
			Bukkit.getPluginManager().disablePlugin(AutoMiner.getInstance());
		}
	}
	
	/**
	 * Function to check if a player is in the autominer database.
	 * @param uuid UniqueID of the player in question
	 * @return boolean A boolean that indicates if the player exists or not.
	 */
	public boolean checkIfPlayerExists(String uuid) {
		
		String query = "SELECT uuid FROM autominer WHERE uuid = ?";
		int count = 0;
		
		try {
			PreparedStatement stmt = this.format(query, new String[] {uuid});	
			
			ResultSet set = stmt.executeQuery();
			
			while(set.next()) count++;
			
		} catch(SQLException err) {
			err.printStackTrace();
		}
		
		if(count > 0) return true;
		else return false;
		
		
	}
	
	/**
	 * Function to get the last logout time of a player
	 * @param uuid UniqueID of the player to query
	 * @return result The timestamp grabbed. Will be a blank string if nothing was found.
	 * @throws SQLException Any exception that occurred during the process
	 */
	public String getTimestamp(String uuid) throws SQLException {
		
		String query = "SELECT lastLogout, uuid FROM autominer WHERE uuid = ?";
		
		PreparedStatement stmt = this.format(query, new String[] {uuid});
		
		ResultSet results = stmt.executeQuery();
		
		String result = "";
		
		while(results.next()) {
			if(results.getString("uuid").equalsIgnoreCase(uuid)) {
				result = results.getString("lastLogout");
				break;
			}
		}
		
		return result;
		
		
	}
	
	/**
	 * Function to get the island level of a player
	 * @param uuid UniqueID of the player to query.
	 * @return result The ID in the database.
	 * @throws SQLException An error with executing the SQL query.
	 */
	public String getID(String uuid) throws SQLException {
		
		String query = "SELECT islandID, uuid FROM autominer WHERE uuid = ?";
		
		PreparedStatement stmt = this.format(query, new String[] {uuid});
		
		ResultSet results = stmt.executeQuery();
		
		String result = "";
		
		while(results.next()) {
			if(results.getString("uuid").equalsIgnoreCase(uuid)) {
				result = results.getString("islandID");
				break;
			}
		}
		
		return result;
		
	}
	
	/**
	 * Function to set the level of a player in the database.
	 * @param uuid UniqueID of the player to update.
	 * @param id IslandID to set.
	 */
	public void setID(String uuid, String id) {
		
		String query = "UPDATE autominer SET islandID = ? WHERE uuid = ?";
		
		try {
			PreparedStatement stmt = this.format(query, new String[] {id, uuid});
			
			stmt.executeUpdate();
		} catch(SQLException err) {
			err.printStackTrace();
		}
		
	}
	
	/**
	 * Function to update the player's timestamp in the database.
	 * @param uuid UniqueID of the timestamp to update.
	 * @param timestamp The new timestamp to insert into the database.
	 */
	public void setTimestamp(String uuid, String timestamp) throws SQLException {
		
		String query = "UPDATE autominer SET lastLogout = ? WHERE uuid = ?";
		

		PreparedStatement stmt = this.format(query, new String[] {timestamp, uuid});
			
		stmt.executeUpdate();

		
	}
	
	/**
	 * Function to add a player to the database.
	 * @param uuid UniqueID of the player to create.
	 * @throws SQLException The server was unable to insert the player into the database. Print the stack trace in the parent call.
	 */
	public void createPlayer(String uuid) throws SQLException {
		
		String query = "INSERT INTO autominer(uuid, lastLogout, islandID) VALUES (?, ?, ?)";
		
		PreparedStatement stmt = this.format(query, new String[] {uuid, "0", ""});
		
		stmt.executeUpdate();
		
	}
	
	
	/**
	 * Function designed to ease the code required to format strings with data. Draws heavily from my experience with nodejs' SQLite / MySQL libraries
	 * @param query The query string that needs to be format.
	 * @param format The array of formatted. The first array corresponds with the first ? in the query strings, and so forth.
	 * @return PreparedStatement The formatted statement that can then further be manipulated.
	 * @throws SQLException There was an error in formatting. 9/10 it's going to be an invalid SQL syntax.
	 */
	private PreparedStatement format(String query, String[] format) throws SQLException {
		
		PreparedStatement stmt = this.connection.prepareStatement(query);
		
		for(int i = 1; i <= format.length; i++) stmt.setString(i, format[i-1]);
			
		return stmt;
		
	}

}
