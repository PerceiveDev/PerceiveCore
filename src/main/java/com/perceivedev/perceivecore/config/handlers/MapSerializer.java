
package com.perceivedev.perceivecore.config.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.perceivedev.perceivecore.config.SerializationManager;
import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * Adds the ability for {@link SerializationManager} to serialize and deserialize
 * objects of type {@link Map}
 */
@SuppressWarnings("rawtypes")
public class MapSerializer implements SerializationProxy<Map> {

    @Override
    public Map<String, Object> serialize(Map inputMap) {
        if (inputMap.isEmpty()) {
            return new HashMap<>();
        }

        // check if they are serializable keys and values
        {
            @SuppressWarnings("rawtypes")
            Entry testEntry = (Entry) inputMap.entrySet().iterator().next();

            if (!SerializationManager.isSerializable(testEntry.getKey().getClass())) {
                throw new IllegalArgumentException("Not serializable key: " + testEntry.getKey().getClass().getName());
            }
            if (!SerializationManager.isSerializable(testEntry.getValue().getClass())) {
                throw new IllegalArgumentException("Not serializable value: " + testEntry.getValue().getClass().getName());
            }
        }

        Map<String, Object> output = new HashMap<>();

        @SuppressWarnings("unchecked")
        Set<Entry<?, ?>> entrySet = inputMap.entrySet();

        List<Object> keys = new ArrayList<>(inputMap.size());
        List<Object> values = new ArrayList<>(inputMap.size());

        for (Entry<?, ?> entry : entrySet) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }

        output.put("keys", keys);
        output.put("values", values);

        return output;
    }

    @Override
    public Map deserialize(Map<String, Object> map) {
        Map<Object, Object> output = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Object> keys = (List<Object>) map.get("keys");
        @SuppressWarnings("unchecked")
        List<Object> values = (List<Object>) map.get("values");

        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("Keys and values size not equal."
                      + " Keys: " + keys.size()
                      + " Values: " + values.size());
        }

        for (int i = 0, keysSize = keys.size(); i < keysSize; i++) {
            Object key = keys.get(i);
            output.put(key, values.get(i));
        }

        return output;
    }
}
