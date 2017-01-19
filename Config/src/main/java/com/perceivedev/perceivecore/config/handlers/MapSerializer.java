package com.perceivedev.perceivecore.config.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.perceivedev.perceivecore.config.SerializationManager;
import com.perceivedev.perceivecore.config.SerializationProxy;
import com.perceivedev.perceivecore.config.util.Pair;


/**
 * Adds the ability for {@link SerializationManager} to serialize and
 * deserialize objects of type {@link Map}
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
            Entry testEntry = (Entry) inputMap.entrySet().iterator().next();

            if (!SerializationManager.isSerializable(testEntry.getKey().getClass())) {
                throw new IllegalArgumentException("Not serializable key: " + testEntry.getKey().getClass().getName());
            }
            if (!SerializationManager.isSerializable(testEntry.getValue().getClass())) {
                throw new IllegalArgumentException("Not serializable value: " + testEntry.getValue()
                        .getClass()
                        .getName());
            }
        }

        Map<String, Object> output = new HashMap<>();

        @SuppressWarnings("unchecked")
        Set<Entry<?, ?>> entrySet = inputMap.entrySet();

        List<Map<String, Object>> pairs = new ArrayList<>(inputMap.size());

        pairs.addAll(entrySet.stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        entry.getValue()))
                .map(Pair::serialize)
                .collect(Collectors.toList()));

        output.put("pairs", pairs);
        return output;
    }

    @Override
    public Map<Object, Object> deserialize(Map<String, Object> map) {
        Map<Object, Object> output = new HashMap<>();

        if (map.isEmpty()) {
            return output;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> serializedPairs = (List<Map<String, Object>>) map.get("pairs");

        for (Map<String, Object> serializedPair : serializedPairs) {
            Pair<Object, Object> pair = new Pair<>(serializedPair);
            output.put(pair.getKey(), pair.getValue());
        }

        return output;
    }
}
