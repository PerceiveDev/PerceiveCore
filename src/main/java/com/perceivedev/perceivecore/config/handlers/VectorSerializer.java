
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.Vector;

import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * Adds the ability for {@link ConfigManager} to serialize and deserialize
 * objects of type {@link Vector}
 * 
 * @author Rayzr
 *
 */
public class VectorSerializer implements SerializationProxy<Vector> {

    @Override
    public Map<String, Object> serialize(Vector obj) {

        Map<String, Object> map = new HashMap<>();

        map.put("x", obj.getX());
        map.put("y", obj.getY());
        map.put("z", obj.getZ());

        return map;
    }

    @Override
    public Vector deserialize(Map<String, Object> map) {

        double x = (double) map.get("x");
        double y = (double) map.get("y");
        double z = (double) map.get("z");

        return new Vector(x, y, z);
    }

}
