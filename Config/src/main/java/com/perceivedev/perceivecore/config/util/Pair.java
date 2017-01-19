package com.perceivedev.perceivecore.config.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.perceivedev.perceivecore.config.SerializationManager;


/**
 * Better than an {@link Map.Entry} ;) - NO [I Al Istannen]
 *
 * @param <K> The key type
 * @param <V> The Value type
 */
public class Pair <K, V> implements ConfigurationSerializable {

    private K key;
    private V value;

    /**
     * @param key The key
     * @param value The value
     */
    @SuppressWarnings("unused")
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a parameter from a data map (used by
     * {@link ConfigurationSerializable})
     *
     * @param map the data map
     */
    @SuppressWarnings("unused")
    public Pair(Map<String, Object> map) {
        {
            @SuppressWarnings("unchecked")
            K key = (K) deserializePairPart(map.get("key"));
            this.key = key;
        }
        {
            @SuppressWarnings("unchecked")
            V value = (V) deserializePairPart(map.get("value"));
            this.value = value;
        }
    }

    /**
     * @return the key
     */
    @SuppressWarnings("WeakerAccess")
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    @SuppressWarnings("unused")
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    @SuppressWarnings("WeakerAccess")
    public V getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    @SuppressWarnings("unused")
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public Map<String, Object> serialize() {
        if (key != null) {
            if (!SerializationManager.isSerializable(getKey().getClass())) {
                throw new IllegalArgumentException("Key not serializable: " + getKey().getClass().getName());
            }
        }

        if (value != null) {
            if (!SerializationManager.isSerializable(getValue().getClass())) {
                throw new IllegalArgumentException("Value not serializable: " + getValue().getClass().getName());
            }
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", serializePairPart(getKey()));
        map.put("value", serializePairPart(getValue()));
        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object serializePairPart(Object object) {
        if (object == null) {
            // hacky
            return "NULL";
        }

        Object serialized = SerializationManager.serializeOneLevel(object);
        if (serialized instanceof Map) {
            ((Map) serialized).put("classNameToDeserialize", object.getClass().getName());
        }
        return serialized;
    }

    @SuppressWarnings("rawtypes")
    private Object deserializePairPart(Object part) {
        if (part.equals("NULL")) {
            return null;
        }
        if (part instanceof Map) {
            String className = (String) ((Map) part).get("classNameToDeserialize");
            ((Map) part).remove("classNameToDeserialize");
            try {
                Class<?> type = Class.forName(className);
                return SerializationManager.deserializeOneLevel(part, type);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
        return SerializationManager.deserializeOneLevel(part, part.getClass());
    }

}
