package com.andrewyunt.megatw_base.utilities;

import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The general utilities class for methods without a category / methods yet to
 * be categorized into another class.
 * 
 * @author Andrew Yunt
 * @author md_5
 * @author blablubbabc
 * @author Quackster
 * @author XlordalX
 */
public class Utils {

	protected static Field mapStringToClassField, mapClassToStringField, mapClassToIdField,
		mapStringToIdField, mapIdToClassField;

	static {
		try {
			mapStringToClassField = EntityTypes.class.getDeclaredField("c");
			mapClassToStringField = EntityTypes.class.getDeclaredField("d");
			mapIdToClassField = EntityTypes.class.getDeclaredField("e");
			mapClassToIdField = EntityTypes.class.getDeclaredField("f");
			mapStringToIdField = EntityTypes.class.getDeclaredField("g");

			mapStringToClassField.setAccessible(true);
			mapClassToStringField.setAccessible(true);
			mapIdToClassField.setAccessible(true);
			mapClassToIdField.setAccessible(true);
			mapStringToIdField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Jogy34
	 *  
	 * @see {@link https://bukkit.org/threads/tutorial-1-7-creating-a-custom-entity.212849/}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addCustomEntity(Class entityClass, String name, int id) {
		
		if (mapStringToClassField == null || mapStringToIdField == null 
				|| mapClassToStringField == null || mapClassToIdField == null)
			return;
		else
			try {
				Map mapStringToClass = (Map) mapStringToClassField.get(null);
				Map mapStringToId = (Map) mapStringToIdField.get(null);
				Map mapClasstoString = (Map) mapClassToStringField.get(null);
				Map mapClassToId = (Map) mapClassToIdField.get(null);
				
				mapStringToClass.put(name, entityClass);
				mapStringToId.put(name, Integer.valueOf(id));
				mapClasstoString.put(entityClass, name);
				mapClassToId.put(entityClass, Integer.valueOf(id));
				
				mapStringToClassField.set(null, mapStringToClass);
				mapStringToIdField.set(null, mapStringToId);
				mapClassToStringField.set(null, mapClasstoString);
				mapClassToIdField.set(null, mapClassToId);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * @author XlordalX
	 */
	public static Object getPrivateField(String fieldName, @SuppressWarnings("rawtypes") Class clazz, Object object) {

		Field field;
		Object o = null;

		try {
			field = clazz.getDeclaredField(fieldName);

			field.setAccessible(true);

			o = field.get(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return o;
	}

	public static Location deserializeLocation(ConfigurationSection section) {

		return new Location(Bukkit.getWorld(section.getString("w")), section.getDouble("x"), section.getDouble("y"),
				section.getDouble("z"));
	}

	public static Map<String, Object> serializeLocation(Location loc) {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("w", loc.getWorld().getName());
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());

		return map;
	}

	public static List<String> colorizeList(List<String> list, ChatColor color) {

		return list.stream().map(line -> color + line).collect(Collectors.toList());
	}

	public static List<org.bukkit.entity.Entity> getNearbyEntities(Location l, int distance) {

		return l.getWorld().getEntities().stream()
				.filter(e -> l.distanceSquared(e.getLocation()) <= distance * distance).collect(Collectors.toList());
	}

	/**
	 * Rotates the given vector around the y axis. This modifies the given
	 * vector.
	 *
	 * @author blablubbabc
	 * @param vector
	 *            the vector to rotate
	 * @param angleD
	 *            the angle of the rotation in degrees
	 * @return the given vector rotated
	 */
	public static Vector rotateYAxis(Vector vector, double angleD) {

		vector = vector.clone();

		// Validate.notNull(vector);
		if (angleD == 0.0D)
			return vector;

		double angleR = Math.toRadians(angleD);
		double x = vector.getX();
		double z = vector.getZ();
		double cos = Math.cos(angleR);
		double sin = Math.sin(angleR);

		vector.setX(x * cos + z * (-sin));
		vector.setZ(x * sin + z * cos);

		return vector;
	}

	public static Inventory fromChest(Inventory inv) {

		Inventory newInv = Bukkit.createInventory(null, 36);

		for (int i = 0; i <= 26; i++) {
			ItemStack is = inv.getItem(i);

			if (is == null)
				continue;

			newInv.setItem(i + 9, is.clone());
		}

		for (int i = 27; i <= 35; i++) {
			ItemStack is = inv.getItem(i);

			if (is == null)
				continue;

			newInv.setItem(i - 27, is.clone());
		}

		return newInv;
	}

	public static Inventory toChest(Inventory inv) {

		Inventory newInv = Bukkit.createInventory(null, 36);

		for (int i = 0; i <= 8; i++) {
			ItemStack is = inv.getItem(i);

			if (is == null)
				continue;

			newInv.setItem(i + 27, is.clone());
		}

		for (int i = 9; i <= 34; i++) {
			ItemStack is = inv.getItem(i);

			if (is == null)
				continue;

			newInv.setItem(i - 9, is.clone());
		}

		return newInv;
	}

	public static int getHighestEntry(ConfigurationSection section) {

		int highest = 0;

		if (section == null)
			return 1;

		Set<String> keys = section.getKeys(false);

		if (keys.size() == 0)
			return 0;

		for (String key : section.getKeys(false)) {
			int num = Integer.valueOf(key);

			if (highest < num)
				highest = num;
		}

		return highest;
	}

	public static String getNumberSuffix(int num) {

		num = num % 100;

		if (num >= 11 && num <= 13)
			return "th";

		switch (num % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	public static ItemStack addGlow(ItemStack item) {

		net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;

		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}

		if (tag == null)
			tag = nmsStack.getTag();

		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);

		return CraftItemStack.asCraftMirror(nmsStack);
	}

	/**
	 * @author Quackster
	 */
	public static Set<Block> getBlocksBetweenTwoPoints(Location loc1, Location loc2) {

		Set<Block> blocks = new HashSet<Block>();

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

		for (int x = bottomBlockX; x <= topBlockX; x++)
			for (int z = bottomBlockZ; z <= topBlockZ; z++)
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);

					blocks.add(block);
				}

		return blocks;
	}
}