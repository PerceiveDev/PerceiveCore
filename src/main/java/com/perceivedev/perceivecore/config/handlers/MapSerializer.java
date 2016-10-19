
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
import com.perceivedev.perceivecore.util.Pair;

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

        List<Map<String, Object>> pairs = new ArrayList<>(inputMap.size());

        pairs.addAll(entrySet.stream()
                  .map(entry -> new Pair<>(
                            serializePairPart(entry.getKey()),
                            serializePairPart(entry.getValue())
                  ))
                  .map(Pair::serialize)
                  .collect(Collectors.toList())
        );

        System.out.println(pairs);

        output.put("pairs", pairs);
        return output;
    }

    @Override
    public Map deserialize(Map<String, Object> map) {
        Map<Object, Object> output = new HashMap<>();

        if (map.isEmpty()) {
            return output;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> serializedPairs = (List<Map<String, Object>>) map.get("pairs");

        for (Map<String, Object> serializedPair : serializedPairs) {
            Pair<Object, Object> pair = new Pair<>(serializedPair);

            Object keyDeserialized = deserializePairPart(pair.getKey());
            Object valueDeserialized = deserializePairPart(pair.getValue());

            output.put(keyDeserialized, valueDeserialized);
        }

        return output;
    }

    @SuppressWarnings("unchecked")
    private Object serializePairPart(Object object) {
        Object serialized = SerializationManager.serializeOneLevel(object);
        if (serialized instanceof Map) {
            ((Map) serialized).put("classNameToDeserialize", object.getClass().getName());
        }
        return serialized;
    }

    private Object deserializePairPart(Object part) {
        if (part instanceof Map) {
            String className = (String) ((Map) part).get("classNameToDeserialize");
            ((Map) part).remove("classNameToDeserialize");
            try {
                Class<?> type = Class.forName(className);
                System.out.println("Got type: " + type.getName());
                System.out.println(SerializationManager.deserializeOneLevel(part, type));
                return SerializationManager.deserializeOneLevel(part, type);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
        return SerializationManager.deserializeOneLevel(part, part.getClass());
    }
}
