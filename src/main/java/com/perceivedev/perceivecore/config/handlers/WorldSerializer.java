
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.perceivedev.perceivecore.config.SerializationManager;
import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * Adds the ability for {@link SerializationManager} to serialize and deserialize
 * objects of type {@link World}
 * 
 * @author Rayzr
 *
 */
public class WorldSerializer implements SerializationProxy<World> {

    @Override
    public Map<String, Object> serialize(World obj) {

        Map<String, Object> map = new HashMap<>();

        map.put("uuid", obj.getUID().toString());

        return map;
    }

    @Override
    public World deserialize(Map<String, Object> map) {

        UUID uuid = UUID.fromString((String) map.get("uuid"));

        return Bukkit.getWorld(uuid);
    }

}
