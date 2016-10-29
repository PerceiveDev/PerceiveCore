
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * Adds the ability for {@link ConfigManager} to serialize and deserialize
 * objects of type {@link UUID}
 * 
 * @author Rayzr
 *
 */
public class UUIDSerializer implements SerializationProxy<UUID> {

    @Override
    public Map<String, Object> serialize(UUID obj) {

        Map<String, Object> map = new HashMap<>();

        map.put("id", obj.toString());

        return map;
    }

    @Override
    public UUID deserialize(Map<String, Object> map) {

        try {
            return UUID.fromString(map.get("id").toString());
        } catch (IllegalArgumentException e) {
            // Ignore
            return null;
        }

    }

}
