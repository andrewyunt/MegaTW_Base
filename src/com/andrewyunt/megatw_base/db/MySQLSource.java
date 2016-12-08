/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megatw_base.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megatw_base.MegaTWBase;
import com.andrewyunt.megatw_base.objects.Class;
import com.andrewyunt.megatw_base.objects.GamePlayer;
import com.andrewyunt.megatw_base.objects.Upgradable;
import com.andrewyunt.megatw_base.utilities.BukkitSerialization;

public class MySQLSource extends DataSource {
 
	private String ip, database, user, pass;
	private int port;
	private Connection connection;
	private Statement statement;
 
    @Override
    public boolean connect() {
 
        FileConfiguration config = MegaTWBase.getInstance().getConfig();
 
        ip = config.getString("database-ip");
        port = config.getInt("database-port");
        database = config.getString("database-name");
        user = config.getString("database-user");
        pass = config.getString("database-pass");
        
        try {
			if (connection != null && !connection.isClosed() && statement != null)
			    return true;
			
			synchronized (this) {
				java.lang.Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, user, pass);
			}
		} catch (SQLException | ClassNotFoundException e) {
			return false;
		}
        
        try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			return false;
		}
 
        return true;
    }
    
    @Override
    public void disconnect() {
    	
    	try {
			connection.close();
		} catch (SQLException e) {
		}
	}
 
    @Override
    public void savePlayer(GamePlayer player) {
    	
    	if (!player.isLoaded())
    		return;
    	
        String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
        
        if (MegaTWBase.getInstance().isEnabled()) {
    		BukkitScheduler scheduler = MegaTWBase.getInstance().getServer().getScheduler();
    		scheduler.runTaskAsynchronously(MegaTWBase.getInstance(), () -> {
    			savePlayer(player, uuid);
    			
    			for (Map.Entry<Upgradable, Integer> entry : player.getUpgradeLevels().entrySet()) {
    				Upgradable upgradable = entry.getKey();
    				int level = entry.getValue();
    				
    				setLevel(player, upgradable, level);
    			}
            });
        } else {
        	savePlayer(player, uuid);
        	
    		for (Map.Entry<Upgradable, Integer> entry : player.getUpgradeLevels().entrySet()) {
    			Upgradable upgradable = entry.getKey();
    			int level = entry.getValue();
    			
    			setLevel(player, upgradable, level);
    		}
        }
    }
    
    private void savePlayer(GamePlayer player, String uuid) {
    	
    	com.andrewyunt.megatw_base.objects.Class classType = player.getClassType();
    	
		try {
			statement.executeUpdate(String.format(
					"INSERT INTO `Players` (`uuid`, `class`, `blood_particles`, `coins`, `earned_coins`, `wins`)"
			+ " VALUES ('%s', '%s', %s, %s, %s) ON DUPLICATE KEY UPDATE class = '%2$s',"
			+ " blood_particles = %3$s, coins = %4$s, earned_coins = %5$s, wins = %6$s;",
			uuid,
			classType == null ? "none" : classType.toString(),
			player.hasBloodEffect() ? 1 : 0,
			player.getCoins(),
			player.getEarnedCoins()));
			player.getWins();
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while saving %s.", player.getName()));
		}
    }
 
    @Override
    public void loadPlayer(GamePlayer player) {
    	
    	String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

		BukkitScheduler scheduler = MegaTWBase.getInstance().getServer().getScheduler();
		scheduler.runTaskAsynchronously(MegaTWBase.getInstance(), () -> {
			ResultSet resultSet = null;
			
			try {
				resultSet = statement.executeQuery("SELECT * FROM `Players` WHERE `uuid` = '" + uuid + "';");
			} catch (SQLException e) {
				return; // player does not exist, so don't load their data
			}
			
			try {
				while (resultSet.next()) {
					String classStr = resultSet.getString("class");
					
					if (!classStr.equals("none"))
						player.setClassType(com.andrewyunt.megatw_base.objects.Class.valueOf(classStr));

					player.setBloodEffect(resultSet.getInt("blood_particles") == 1);
					player.setCoins(resultSet.getInt("coins"));
					player.setEarnedCoins(resultSet.getInt("earned_coins"));
					player.setWins(resultSet.getInt("wins"));
				}
            } catch (SQLException e) {
                e.printStackTrace();
                MegaTWBase.getInstance().getLogger().severe(String.format(
                        "An error occured while loading %s.", player.getName()));
                return;
            }
			
			player.setLoaded(true);
        });
    }
    
	@Override
	public void saveLayout(GamePlayer player, com.andrewyunt.megatw_base.objects.Class classType, Inventory inv) {
		
		String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
		
		BukkitScheduler scheduler = MegaTWBase.getInstance().getServer().getScheduler();
        scheduler.runTaskAsynchronously(MegaTWBase.getInstance(), () -> {
            try {
                statement.executeUpdate(String.format(
                        "INSERT INTO `Layouts` (`uuid`, `layout`, `level`, `inventory`)"
                                + " VALUES ('%s', '%s', %s, '%s', '%s') ON DUPLICATE KEY UPDATE `layout` = '%2$s',"
                                + "`inventory` = '%4$s';",
                        uuid,
                        classType.toString(),
                        player.getLevel(classType),
                        BukkitSerialization.toBase64(inv)));
            } catch (SQLException e) {
                MegaTWBase.getInstance().getLogger().severe(String.format(
                        "An error occured while saving %s.", player.getName()));
            }
        });
	}
	
	@Override
	public Inventory loadLayout(GamePlayer player, com.andrewyunt.megatw_base.objects.Class classType) {
		
		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		String version = MegaTWBase.getInstance().getDescription().getVersion();
		
        ResultSet resultSet = null;
        
		try {
			resultSet = statement.executeQuery("SELECT * FROM `Layouts` WHERE `uuid` = '" + uuid + "' AND"
					+ " layout = '" + classType.toString() + "' AND `level` = " + player.getLevel(classType) + ";");
		} catch (SQLException e) {
			return null; // layout doesn't exist
		}
		
        try {
    		while (resultSet.next()) {
    			String layoutStr = resultSet.getString("inventory");
    			return BukkitSerialization.fromBase64(layoutStr);
    		}
		} catch (SQLException | IOException e) {
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s's %s layout.", player.getName(), 
					player.getClassType().getName()));
		}
        
		return null;
	}
	
	@Override
	public void setLevel(GamePlayer player, Upgradable upgradable, int level) {
		
		String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
		
		try {
			statement.executeUpdate(String.format(
					"INSERT INTO `Upgrades` (`uuid`, `upgradable`, `level`)"
			+ " VALUES ('%s', '%s', '%s') ON DUPLICATE KEY UPDATE `level` = '%3$s';",
			uuid,
			upgradable.toString(),
			level));
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while saving %s.", player.getName()));
		}
	}
	
	@Override
	public int getLevel(GamePlayer player, Upgradable upgradable) {
		
        String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

        ResultSet resultSet = null;
        
		try {
			resultSet = statement.executeQuery("SELECT * FROM `Upgrades` WHERE `uuid` = '" + uuid
					+ "' AND `upgradable` = '" + upgradable.toString() + "';");
		} catch (SQLException e) {
			return 1;
		}
 
        try {
    		while (resultSet.next())
    			return resultSet.getInt("level");
		} catch (SQLException e) {
			e.printStackTrace();
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s's %s upgradable.", player.getName(), upgradable.getName()));
		}
        
		return 1;
	}
	
	@Override
	public void setKills(GamePlayer player, boolean weekly, boolean finalKill, com.andrewyunt.megatw_base.objects.Class classType, int count) {
		
		String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
		
		try {
			statement.executeUpdate(String.format(
					"INSERT INTO `Kills` (`uuid`, `reset_weekly`, `final`, `class`, `count`)"
			+ " VALUES ('%s', %s, %s, '%s', %s) ON DUPLICATE KEY UPDATE `kills` = '%5$s';",
			uuid,
			weekly ? 1 : 0,
			finalKill ? 1 : 0,
			classType == null ? "ALL" : classType.toString(),
			count));
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while saving %s's kills.", player.getName()));
		}
	}

	@Override
	public int getKills(GamePlayer player, boolean weekly, boolean finalKill, com.andrewyunt.megatw_base.objects.Class classType) {
		
		String uuid = MegaTWBase.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
		
		ResultSet resultSet = null;
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM `Kills` WHERE `uuid` = '" + uuid
					+ "'" + (weekly ? " AND `reset_weekly` = 1" : "") 
					+ (finalKill ? " AND `final` = 1" : "")
					+ " AND `class` = '" + classType == null ? "ALL" : classType.toString() + "';");
		} catch (SQLException e) {
			return 1;
		}
		
		try {
			while (resultSet.next())
				return resultSet.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
			MegaTWBase.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s's kills.", player.getName()));
		}
		
		return 1;
	}
	
	@Override
	public Map<Integer, Map.Entry<OfflinePlayer, Integer>> getMostKills(boolean weekly, boolean finalKill, Class classType) {
		
		Map<Integer, Map.Entry<OfflinePlayer, Integer>> mostKills =  new HashMap<Integer, Map.Entry<OfflinePlayer, Integer>>();
		
		ResultSet resultSet = null;
		
		try {
			resultSet = statement.executeQuery("SELECT `uuid`, `kills` FROM `Players` WHERE"
					+ " `reset_weekly` = " + String.valueOf(weekly ? 1 : 0)
					+ " AND `final` = " + String.valueOf(finalKill ? 1 : 0)
					+ " AND `class` = '" + classType == null ? "ALL" : classType.toString()
					+ " ORDER BY `kills` DESC LIMIT 5;");
		} catch (SQLException e) {
			return null;
		}
		
		try {
			int place = 1;
			
			while (resultSet.next()) {
				OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(UUID.fromString(resultSet.getString("uuid")));
				
				mostKills.put(place, new AbstractMap.SimpleEntry(op, resultSet.getInt("kills")));
				
				place++;
			}
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe("An error occured while getting players with the most kills.");
		}
		
		return mostKills;
	}
	
	@Override
	public void createPlayersTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Players`"
	            + "  (`uuid`             CHAR(36) PRIMARY KEY NOT NULL,"
	            + "   `class`            CHAR(20) NOT NULL,"
	            + "   `blood_particles`  INT,"
	            + "   `coins`            INT,"
	            + "   `earned_coins`     INT,"
	            + "   `wins`            INT);";
	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe( "An error occured while creating the Players table.");
		}
	}
	
	@Override
	public void createLayoutsTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Layouts`"
	            + "  (`uuid`             CHAR(36) NOT NULL,"
	            + "   `layout`           CHAR(20) NOT NULL,"
	            + "   `level`            INT NOT NULL,"
	            + "   `inventory`        VARCHAR(8000) NOT NULL,"
	            + "   PRIMARY KEY (`uuid`, `layout`, `level`));";

	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe( "An error occured while creating the Layouts table.");
		}
	}
	
	@Override
	public void createUpgradesTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Upgrades`"
	            + "  (`uuid`             CHAR(36) NOT NULL,"
	            + "   `upgradable`       CHAR(20) NOT NULL,"
	            + "   `level`            INT NOT NULL,"
	            + "   PRIMARY KEY (`uuid`, `upgradable`));";
	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe( "An error occured while creating the Upgrades table.");
		}
	}
	
	@Override
	public void createKillsTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Kills`"
	            + "  (`uuid`             CHAR(36) NOT NULL,"
	            + "   `reset_weekly`     BOOLEAN NOT NULL,"
	            + "   `final`            BOOLEAN NOT NULL,"
	            + "   `class`            CHAR NOT NULL,"
	            + "   `count`            INT NOT NULL,"
	            + "   PRIMARY KEY (`uuid`, `reset_weekly`, `final`, `class`));";
	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaTWBase.getInstance().getLogger().severe( "An error occured while creating the Upgrades table.");
		}
	}
}