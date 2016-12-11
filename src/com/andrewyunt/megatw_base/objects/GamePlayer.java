/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reerved.
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
package com.andrewyunt.megatw_base.objects;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

import com.andrewyunt.megatw_base.MegaTWBase;

/**
 * The class used to store player's information.
 * 
 * @author Andrew Yunt
 */
public abstract class GamePlayer {
	
	protected final String name;
	
	protected Class classType;
	protected DynamicScoreboard dynamicScoreboard = null;
	protected int coins = 0, earnedCoins = 0, kills = 0, wins = 0;
	protected boolean loaded = false, hasBloodEffect = false;
	protected Map<Upgradable, Integer> upgradeLevels = new HashMap<Upgradable, Integer>();
	
	public GamePlayer(String name) {
		
		/* Set variables */
		this.name = name;
		
		/* Load upgradable levels */
		for (Class classType : Class.values()) {
			int level = MegaTWBase.getInstance().getDataSource().getLevel(this, classType);
			upgradeLevels.put(classType, level);
		}
		
		for (Skill skillType : Skill.values()) {
			int level = MegaTWBase.getInstance().getDataSource().getLevel(this, skillType);
			upgradeLevels.put(skillType, level);
		}
		
		for (Ability abilityType : Ability.values()) {
			int level = MegaTWBase.getInstance().getDataSource().getLevel(this, abilityType);
			upgradeLevels.put(abilityType, level);
		}
	}
	
	public String getName() {
		
		return name;
	}
	
	public Player getBukkitPlayer() {
		
		return MegaTWBase.getInstance().getServer().getPlayer(name);
	}
	
	public void setClassType(Class classType) {
		
		this.classType = classType;
		
		updateDynamicScoreboard();
	}
	
	public Class getClassType() {
		
		return classType;
	}
	
	public boolean hasSelectedClass() {
		
		return classType != null;
	}
	
	public void addCoins(int coins) {
		
		setCoins(this.coins + coins);
		
		setEarnedCoins(earnedCoins + coins);
	}
	
	public void removeCoins(int coins) {
		
		setCoins(this.coins - coins);
	}
	
	public void setCoins(int coins) {
		
		this.coins = coins;
	}
	
	public int getCoins() {
		
		return coins;
	}
	
	public void setEarnedCoins(int earnedCoins) {
		
		this.earnedCoins = earnedCoins;
	}
	
	public int getEarnedCoins() {
		
		return earnedCoins;
	}
	
	public void setKills(int kills) {
		
		this.kills = kills;
	}
	
	public int getKills() {
		
		return kills;
	}
	
	public void setWins(int wins) {
		
		this.wins = wins;
	}
	
	public int getWins() {
		
		return wins;
	}
	
	public void setLoaded(boolean loaded) {
		
		this.loaded = loaded;
	}
	
	public boolean isLoaded() {
		
		return loaded;
	}
	
	public void setBloodEffect(boolean hasBloodEffect) {
		
		this.hasBloodEffect = hasBloodEffect;
	}
	
	public boolean hasBloodEffect() {
		
		return hasBloodEffect;
	}
	
	public void setClassLevel(Upgradable upgradable, int level) {
		
		if (upgradeLevels.containsKey(upgradable))
			upgradeLevels.remove(upgradable);
		
		upgradeLevels.put(upgradable, level);
	}
	
	public int getLevel(Upgradable upgradable) {
		
		if (upgradeLevels.containsKey(upgradable))
			return upgradeLevels.get(upgradable);
		
		return 1;
	}
	
	public Map<Upgradable, Integer> getUpgradeLevels() {
		
		return upgradeLevels;
	}
	
	public abstract void updateDynamicScoreboard();
}