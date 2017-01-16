package com.perceivedev.bukkitpluginutilities.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.perceivedev.bukkitpluginutilities.config.SerializationManager;
import com.perceivedev.bukkitpluginutilities.config.SerializationProxy;


/**
 * Adds the ability for {@link SerializationManager} to serialize and
 * deserialize objects of type {@link Location}
 *
 * @author Rayzr
 */
public class LocationSerializer implements SerializationProxy<Location> {

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
        if (world == null) {
            return null;
        }

        try {

            double x = Double.valueOf(map.get("x").toString());
            double y = Double.valueOf(map.get("y").toString());
            double z = Double.valueOf(map.get("z").toString());
            float yaw = Float.valueOf(map.get("yaw").toString());
            float pitch = Float.valueOf(map.get("pitch").toString());

            return new Location(world, x, y, z, yaw, pitch);

        } catch (NumberFormatException e) {
            return null;
        }

    }

}
