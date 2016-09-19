
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.perceivedev.perceivecore.config.ConfigManager;
import com.perceivedev.perceivecore.config.ISerializationHandler;

/**
 * Adds the ability for {@link ConfigManager} to serialize and deserialize
 * objects of type {@link World}
 * 
 * @author Rayzr
 *
 */
public class WorldSerializer implements ISerializationHandler<World> {

	@Override
	public Map<String, Object> serialize(World obj) {

		Map<String, Object> map = new HashMap<>();

		map.put("uuid", obj.getUID().toString());

		return map;
	}

	@Override
	public World deserialize(Map<String, Object> map) {

		UUID uuid = UUID.fromString((String) map.get("uuid"));

		World world = Bukkit.getWorld(uuid);

		return world;

	}

}
