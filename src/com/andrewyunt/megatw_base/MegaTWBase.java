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
package com.andrewyunt.megatw_base;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.megatw_base.db.MySQLSource;
import com.andrewyunt.megatw_base.command.BloodToggleCommand;
import com.andrewyunt.megatw_base.db.DataSource;
import com.andrewyunt.megatw_base.menu.ClassSelectorMenu;
import com.andrewyunt.megatw_base.objects.GamePlayer;

/**
 * The main class in the MegaTW plugin.
 * 
 * <p>
 * You can get the instance of this class using the static getInstance() method.
 * </p>
 * 
 * @author Andrew Yunt
 */
public class MegaTWBase extends JavaPlugin implements Listener {
	
	private final Logger logger = getLogger();
	private final Server server = getServer();
	private final PluginManager pm = server.getPluginManager();
	private final ClassSelectorMenu classSelectorMenu = new ClassSelectorMenu();
	private final DataSource dataSource = new MySQLSource();
	private final Map<String, GamePlayer> players = new HashMap<String, GamePlayer>();
	
	private static MegaTWBase instance;
	
	/**
	 * Method is executed while the plugin is being enabled.
	 */
	@Override
	public void onEnable() {
		
		// Set static instance to this
		instance = this;
		
		
		// Check for dependencies
		if (pm.getPlugin("MegaTW_Master") == null && pm.getPlugin("MegaTW_Slave") == null) {
			logger.severe("MegaTW_Base is missing a dependency, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		
		// Save default config to plugin folder
		saveDefaultConfig();
		
		// Connect to the database
		if (!dataSource.connect()) {
			logger.severe("Could not connect to the database, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		
		dataSource.updateDB();
		
		// Set command executors
		getCommand("bloodtoggle").setExecutor(new BloodToggleCommand());
		
		// Register events
		pm.registerEvents(classSelectorMenu, this);
		
		pm.registerEvents(this, this);
	}
	
	/**
	 * Gets the instance of the MegaTW class.
	 * 
	 * @return
	 * 		Instance of the MegaTW class.
	 */
	public static MegaTWBase getInstance() {
		
		return instance;
	}
	
	/**
	 * Gets the plugin's data source.
	 * 
	 * @return
	 * 		An instance that extends the DataSource class.
	 */
	public DataSource getDataSource() {
		
		return dataSource;
	}
	
	/**
	 * @return
	 * 		The instance of the class selector menu.
	 */
	public ClassSelectorMenu getClassSelectorMenu() {
		
		return classSelectorMenu;
	}
	
	public Map<String, GamePlayer> getPlayers() {
		
		return players;
	}
}