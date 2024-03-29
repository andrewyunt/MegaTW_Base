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
package com.andrewyunt.megatw_base.objects;

/**
 * The enumeration for abilities, their names, and the method to use them.
 * 
 * @author Andrew Yunt
 */
public enum Skill implements Upgradable {
	
	// Normal skills
	RESIST("Resist"),
	SWIFTNESS("Swiftness"),
	BOOMERANG("Boomerang"),
	WEAKENING_ARROW("Weakening Arrow"),
	RECHARGE("Recharge"),
	FLURRY("Flurry"),
	POWERFUL_WEAKNESS("Powerful Weakness"),
	SUPPORT("Support"),
	WEAKENING_SWING("Weakening Swing"),
	SWIFT_BACKUP("Swift Backup"),
	SOUL_SUCKER("Soul Sucker"),
	UNDEAD("Undead"),
	RETRIEVAL("Retrieval"),
	EMPOWERED("Empowered"),
	
	// Gathering talents
	HASTE("Haste"),
	FORTUNE("Fortune"),
	TNT("TNT"),
	TREASURE_HUNTER("Treasure Hunter"),
	FURNACE("Furnace"),
	SALVAGING("Salvaging"),
	HEAT_OF_HELL("Heat of Hell");
	
	final String name;
	
	Skill(String name) {
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		
		return name;
	}
}