
package com.perceivedev.perceivecore.config.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * Adds the ability for {@link ConfigManager} to serialize and deserialize
 * objects of type {@link Map<?, ?>}
 * 
 * @author Rayzr
 *
 */
@SuppressWarnings("rawtypes")
public class MapSerializer implements SerializationProxy<Map> {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> serialize(Map obj) {

        Map<String, Object> output = new HashMap<String, Object>();

        obj.entrySet().forEach(e -> {

            Entry entry = (Entry) e;
            output.put(entry.getKey().toString(), entry.getValue());

        });

        return output;

    }

    @Override
    public Map deserialize(Map<String, Object> map) {
        return map;
    }

}
