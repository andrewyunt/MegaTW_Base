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

import static com.andrewyunt.megatw_base.objects.Ability.EXPLODE;
import static com.andrewyunt.megatw_base.objects.Ability.EXPLOSIVE_ARROW;
import static com.andrewyunt.megatw_base.objects.Ability.HEAL;
import static com.andrewyunt.megatw_base.objects.Ability.LIGHTNING;
import static com.andrewyunt.megatw_base.objects.Ability.TORNADO;
import static com.andrewyunt.megatw_base.objects.Ability.WITHER_HEADS;
import static com.andrewyunt.megatw_base.objects.Skill.BOOMERANG;
import static com.andrewyunt.megatw_base.objects.Skill.FLURRY;
import static com.andrewyunt.megatw_base.objects.Skill.FORTUNE;
import static com.andrewyunt.megatw_base.objects.Skill.FURNACE;
import static com.andrewyunt.megatw_base.objects.Skill.HASTE;
import static com.andrewyunt.megatw_base.objects.Skill.POWERFUL_WEAKNESS;
import static com.andrewyunt.megatw_base.objects.Skill.RECHARGE;
import static com.andrewyunt.megatw_base.objects.Skill.RESIST;
import static com.andrewyunt.megatw_base.objects.Skill.SALVAGING;
import static com.andrewyunt.megatw_base.objects.Skill.SOUL_SUCKER;
import static com.andrewyunt.megatw_base.objects.Skill.SUPPORT;
import static com.andrewyunt.megatw_base.objects.Skill.SWIFTNESS;
import static com.andrewyunt.megatw_base.objects.Skill.SWIFT_BACKUP;
import static com.andrewyunt.megatw_base.objects.Skill.TNT;
import static com.andrewyunt.megatw_base.objects.Skill.TREASURE_HUNTER;
import static com.andrewyunt.megatw_base.objects.Skill.UNDEAD;
import static com.andrewyunt.megatw_base.objects.Skill.WEAKENING_ARROW;
import static com.andrewyunt.megatw_base.objects.Skill.WEAKENING_SWING;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.andrewyunt.megatw_base.MegaTWBase;

/**
 * The enumeration used for player's selected class types.
 * 
 * @author Andrew Yunt
 * @author MaccariTA
 * @author OriginalStrafe
 */
public enum Class implements Upgradable {
	
	ZOMBIE("Zombie", HEAL, RESIST, SWIFTNESS, HASTE, 4, false),
	SKELETON("Skeleton", EXPLOSIVE_ARROW, WEAKENING_ARROW, BOOMERANG, FORTUNE, 20, false),
	CREEPER("Creeper", EXPLODE, POWERFUL_WEAKNESS, SUPPORT, TNT, 10, false),
	HEROBRINE("Herobrine", LIGHTNING, RECHARGE, FLURRY, TREASURE_HUNTER, 10, false),
	WITHER_MINION("Wither Minion", WITHER_HEADS, SOUL_SUCKER, UNDEAD, FURNACE, 5, true),
	SPIRIT_WARRIOR("Spirit Warrior", TORNADO, WEAKENING_SWING, SWIFT_BACKUP, SALVAGING, 5, true);
	
	private final String name;
	private final Ability ability;
	private final Skill skillOne;
	private final Skill skillTwo;
	private final Skill gatheringTalent;
	private final int energyPerClick;
	private final boolean hero;
	
	Class(String name, Ability ability, Skill skillOne, Skill skillTwo, Skill gatheringTalent,
			int energyPerClick, boolean hero) {
		
		this.name = name;
		this.ability = ability;
		this.skillOne = skillOne;
		this.skillTwo = skillTwo;
		this.gatheringTalent = gatheringTalent;
		this.energyPerClick = energyPerClick;
		this.hero = hero;
	}
	
	@Override
	public String getName() {
		
		return name;
	}
	
	public Ability getAbility() {
		
		return ability;
	}
	
	public Skill getSkillOne() {
		
		return skillOne;
	}
	
	public Skill getSkillTwo() {
		
		return skillTwo;
	}
	
	public Skill getGatheringTalent() {
		
		return gatheringTalent;
	}
	
	public int getEnergyPerClick() {
		
		return energyPerClick;
	}
	
	public boolean isHero() {
		
		return hero;
	}
	
	public void giveKitItems(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		PlayerInventory playerInv = bp.getInventory();
		int kitLevel = player.getLevel(this);
		
		// Clear the inventory
		playerInv.clear();
		playerInv.setHelmet(new ItemStack(Material.AIR, 1));
		playerInv.setChestplate(new ItemStack(Material.AIR, 1));
		playerInv.setLeggings(new ItemStack(Material.AIR, 1));
		playerInv.setBoots(new ItemStack(Material.AIR, 1));
		
		// Set inventory contents
		playerInv.setContents(getKitInventoryItems(player, true).getContents());
		
		// Local variables
		ItemStack helmet;
		ItemStack chestplate;
		ItemStack leggings;
		ItemStack boots;
		
		// Add items to inventory
		if (this == ZOMBIE) {

			switch (kitLevel) {
			case 1:
				playerInv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			case 2:
			    chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
			    playerInv.setChestplate(chestplate);				
			    break;
			case 3:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
			    playerInv.setChestplate(chestplate);	
				break;
			case 4:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
			    playerInv.setChestplate(chestplate);	
				break;
			case 5:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
			    playerInv.setChestplate(chestplate);	
				break;
			case 6:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			    playerInv.setChestplate(chestplate);	
				break;
			case 7:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			    playerInv.setChestplate(chestplate);		
				break;
			case 8:
			    chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			    playerInv.setChestplate(chestplate);
				break;
			case 9:
			    chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
			    chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			    playerInv.setChestplate(chestplate);	
				break;
			default:
				break;
			}

		} else if (this == SKELETON) {
			
			switch (kitLevel) {
			case 7:
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
				playerInv.setHelmet(helmet);
				break;
			case 8:
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
				playerInv.setHelmet(helmet);
				break;
			case 9:
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
				playerInv.setHelmet(helmet);
				break;
			default:
				break;
			}

		} else if (this == HEROBRINE) {
			
			switch (kitLevel) {
			case 2:
				playerInv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
				break;
			case 3:
				helmet = new ItemStack(Material.CHAINMAIL_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 4:
				helmet = new ItemStack(Material.CHAINMAIL_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 5:
				helmet = new ItemStack(Material.CHAINMAIL_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 6:
				helmet = new ItemStack(Material.CHAINMAIL_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 7:
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 8:
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setHelmet(helmet);
				break;
			case 9:
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				playerInv.setHelmet(helmet);
				break;
			default:
				break;
			}
			
		} else if (this == CREEPER) {
			
			switch (kitLevel) {
			case 1:
				leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);
				playerInv.setLeggings(leggings);
				break;
			case 2:
				leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				playerInv.setLeggings(leggings);
				break;
			case 3:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				playerInv.setLeggings(leggings);
				break;
			case 4:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				playerInv.setLeggings(leggings);
				break;
			case 5:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				playerInv.setLeggings(leggings);
				break;
			case 6:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 3);
				playerInv.setLeggings(leggings);
				break;
			case 7:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
				playerInv.setLeggings(leggings);
				break;
			case 8:
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
				playerInv.setLeggings(leggings);
				break;
			case 9:
				leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				playerInv.setLeggings(leggings);
				break;
			default:
				break;
			}

		} else if (this == SPIRIT_WARRIOR) {
			
			switch (kitLevel) {
			case 6:
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
				playerInv.setBoots(boots);
				break;
			case 7:
				boots = new ItemStack(Material.DIAMOND_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
				playerInv.setBoots(boots);
				break;
			case 8:
				boots = new ItemStack(Material.DIAMOND_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 2);
				playerInv.setBoots(boots);
				break;
			case 9:
				boots = new ItemStack(Material.DIAMOND_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 2);
				playerInv.setBoots(boots);
				break;
			default:
				break;
			}

		} else if (this == WITHER_MINION) {
			
			if (kitLevel == 9) {
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				playerInv.setHelmet(helmet);
			}
		}
	}
	
	public Inventory getKitInventoryItems(GamePlayer player, boolean loadFromDB) {
		
		Inventory inv = Bukkit.createInventory(null, 36);
		int kitLevel = player.getLevel(this);
		
		// Health potion
		ItemStack potH = new ItemStack(Material.POTION, 1);
		PotionMeta pmH = (PotionMeta) potH.getItemMeta();
		PotionEffect effectH = new PotionEffect(PotionEffectType.HEAL, 1, 2, false);
		List<String> lstH = new ArrayList<String>();
		lstH.add(ChatColor.RESET + "HEAL 8" + ChatColor.RED + "\u2764");
		pmH.setDisplayName(ChatColor.RESET + "" + ChatColor.DARK_RED + "Health Potion");
		pmH.setLore(lstH);
		pmH.setMainEffect(PotionEffectType.HEAL);
		pmH.addCustomEffect(effectH, true);
		potH.setItemMeta(pmH);
		ItemStack potH2 = new ItemStack(Material.POTION, 2);
		potH2.setItemMeta(pmH);

		// Speed potion
		ItemStack potS = new ItemStack(Material.POTION, 1);
		PotionMeta pmS = (PotionMeta) potS.getItemMeta();
		PotionEffect effectS = new PotionEffect(PotionEffectType.SPEED, (15 * 20), 1, false);
		List<String> lstS = new ArrayList<String>();
		lstS.add(ChatColor.RESET + "Duration: " + ChatColor.GRAY + "15s");
		pmS.setLore(lstS);
		pmS.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + "Speed Potion");
		pmS.setMainEffect(PotionEffectType.SPEED);
		pmS.addCustomEffect(effectS, true);
		potS.setItemMeta(pmS);
		ItemStack potS2 = new ItemStack(Material.POTION, 2);
		potS2.setItemMeta(pmS);
		ItemStack potS3 = new ItemStack(Material.POTION, 3);
		potS3.setItemMeta(pmS);
		
		// Local variables
		ItemStack bow;
		ItemStack sword;
		
		// Add items to inventory
		if (this == ZOMBIE) {
			
			inv.setItem(1, new ItemStack(Material.STONE_PICKAXE));
			
			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
			case 2:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));			
			    break;
			case 3:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));	
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));	
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				break;
			default:
				break;
			}

		} else if (this == SKELETON) {

			inv.setItem(1, new ItemStack(Material.STONE_PICKAXE));
				
			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.BOW, 1));
				inv.setItem(6, new ItemStack(Material.ARROW, 30));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.BOW, 1));
				inv.setItem(6, new ItemStack(Material.ARROW, 35));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.BOW, 1));
				inv.setItem(6, new ItemStack(Material.ARROW, 40));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.BOW, 1));
				inv.setItem(6, new ItemStack(Material.ARROW, 45));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.BOW, 1));
				inv.setItem(6, new ItemStack(Material.ARROW, 50));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS);
				break;
			case 6:
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(0, bow);
				inv.setItem(6, new ItemStack(Material.ARROW, 55));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 7:
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(0, bow);
				inv.setItem(6, new ItemStack(Material.ARROW, 60));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 8:
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(0, bow);
				inv.setItem(6, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			case 9:
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
				inv.setItem(0, bow);
				inv.setItem(6, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			default:
				break;
			}

		} else if (this == HEROBRINE) {

			inv.setItem(1, new ItemStack(Material.STONE_PICKAXE));
			
			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS);
				break;
			case 8:
				sword = new ItemStack(Material.STONE_SWORD);
				sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				sword.addEnchantment(Enchantment.DURABILITY, 5);
				inv.setItem(0, sword);
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			case 9:
				sword = new ItemStack(Material.IRON_SWORD);
				sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				sword.addEnchantment(Enchantment.DURABILITY, 5);
				inv.setItem(0, sword);
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			default:
				break;
			}
			
		} else if (this == CREEPER) {

			inv.setItem(1, new ItemStack(Material.STONE_PICKAXE));
			
			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS);
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			default:
				break;
			}

		} else if (this == SPIRIT_WARRIOR) {

			inv.setItem(1, new ItemStack(Material.STONE_SWORD));
			
			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS);
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS2);
				break;
			default:
				break;
			}

		} else if (this == WITHER_MINION) {

			inv.setItem(0, new ItemStack(Material.STONE_PICKAXE));

			switch (kitLevel) {
			case 1:
				inv.setItem(0, new ItemStack(Material.WOOD_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.STONE_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 2));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS);
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH);
				inv.setItem(2, potS2);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS3);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 3));
				inv.setItem(3, potH2);
				inv.setItem(2, potS3);
				break;
			default:
				break;
			}
		}
		
		if (loadFromDB) {
			Inventory loadedInv = MegaTWBase.getInstance().getDataSource().loadLayout(player, this);
		
			if (loadedInv != null)
				inv.setContents(loadedInv.getContents());
		}
		
		return inv;
	}
}