
package com.perceivedev.perceivecore.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflection {

	private static String	VERSION;

	private static Class<?>	CRAFT_PLAYER;
	private static Class<?>	ENTITY_PLAYER;
	private static Class<?>	PLAYER_CONNECTION;

	private static Method	GET_HANDLE;
	private static Method	SEND_PACKET;

	private static Field	F_PLAYER_CONNECTION;

	static {

		try {

			CRAFT_PLAYER = getOBC("entity.CraftPlayer");
			ENTITY_PLAYER = getNMS("EntityPlayer");
			PLAYER_CONNECTION = getNMS("PlayerConnection");

			GET_HANDLE = getMethod(CRAFT_PLAYER, "getHandle");
			SEND_PACKET = getMethod(PLAYER_CONNECTION, "sendPacket", getPacket(""));

			F_PLAYER_CONNECTION = ENTITY_PLAYER.getDeclaredField("playerConnection");

		} catch (Exception e) {

			System.err.println("Failed to load Reflection class");
			e.printStackTrace();

		}

	}

	public static List<Field> getFieldsWithAnnotation(Class<? extends Object> clazz, Class<? extends Annotation> annotation) {

		List<Field> fields = new ArrayList<>();

		Class<? extends Object> clazz2 = clazz;

		while (clazz2 != null && Object.class.isAssignableFrom(clazz2)) {

			for (Field field : clazz2.getDeclaredFields()) {

				if (field.isAnnotationPresent(annotation)) {

					fields.add(field);

				}

			}

			clazz2 = clazz2.getSuperclass();

		}

		return fields;

	}

	public static void setValue(Field f, Object o, Object v) {

		try {
			boolean accessible = f.isAccessible();
			f.setAccessible(true);
			f.set(o, v);
			f.setAccessible(accessible);
		} catch (Exception e) {
			System.err.println("Failed to set value of field '" + f.getName() + "' in class '" + o.getClass().getCanonicalName() + "'");
			e.printStackTrace();
		}

	}

	public static String getVersion() {

		if (VERSION == null) {

			String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");

			VERSION = split[split.length - 1];

		}

		return VERSION;

	}

	public static Class<?> getNMS(String name) {

		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find NMS class '" + name + "'");
			e.printStackTrace();
			return null;
		}

	}

	public static Class<?> getOBC(String name) {

		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find OBC class '" + name + "'");
			e.printStackTrace();
			return null;
		}

	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {

		try {
			return clazz.getMethod(name, params);
		} catch (Exception e) {
			System.err.println("Failed to find method '" + name + "' in class '" + clazz.getCanonicalName() + "'");
			e.printStackTrace();
			return null;
		}

	}

	public static Object getHandle(Player player) {

		try {
			return GET_HANDLE.invoke(player);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static Class<?> getPacket(String name) {

		return getNMS("Packet" + name);

	}

	/**
	 * Taken from ActionBarUtil by mine-care (a.k.a. fillpant)
	 * 
	 * @param packet
	 * @param pl
	 */
	public static void sendPacket(Object packet, Player... pl) {

		for (Player p : pl) {
			Object entityPlayer = getHandle(p);
			try {
				Object playerConn = F_PLAYER_CONNECTION.get(entityPlayer);
				SEND_PACKET.invoke(playerConn, packet);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean hasInterface(Object o, Class<?> interfacee) {

		return hasInterface(o.getClass(), interfacee);

	}

	public static boolean hasInterface(Class<?> c, Class<?> interfacee) {

		Class<?>[] i = c.getInterfaces();
		for (Class<?> intf : i) {
			if (intf.equals(interfacee)) { return true; }
		}
		return false;

	}

	public static boolean isVersion(String string) {
		return VERSION.startsWith("v1_" + string + "_");
	}

}
