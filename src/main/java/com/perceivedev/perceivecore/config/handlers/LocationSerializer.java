
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.perceivedev.perceivecore.config.ConfigManager;
import com.perceivedev.perceivecore.config.ISerializationHandler;

/**
 * Adds the ability for {@link ConfigManager} to serialize and deserialize
 * objects of type {@link Location}
 * 
 * @author Rayzr
 *
 */
public class LocationSerializer implements ISerializationHandler<Location> {

	@Override
	public Map<String, Object> serialize(Location obj) {

		Map<String, Object> map = new HashMap<>();

		map.put("world", obj.getWorld().getUID().toString());
		map.put("x", obj.getX());
		map.put("y", obj.getY());
		map.put("z", obj.getZ());
		map.put("yaw", obj.getYaw());
		map.put("pitch", obj.getPitch());

		return map;
	}

	@Override
	public Location deserialize(Map<String, Object> map) {

		World world = Bukkit.getWorld(UUID.fromString((String) map.get("world")));
		if (world == null) { return null; }

		double x = d(map, "x");
		double y = d(map, "y");
		double z = d(map, "z");
		float yaw = f(map, "yaw");
		float pitch = f(map, "pitch");

		return new Location(world, x, y, z, yaw, pitch);

	}

}
