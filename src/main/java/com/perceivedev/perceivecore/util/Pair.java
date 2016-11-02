package com.perceivedev.perceivecore.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.perceivedev.perceivecore.config.SerializationManager;

/**
 * Better than an {@link Map.Entry} ;)
 *
 * @param <K> The key type
 * @param <V> The Value type
 */
public class Pair<K, V> implements Serializable, ConfigurationSerializable {
    private static final long serialVersionUID = 7388136271482352386L;

    private K                 key;
    private V                 value;

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
    @SuppressWarnings("unchecked")
    public Pair(Map<String, Object> map) {
        this.key = (K) deserializePairPart(map.get("key"));
        this.value = (V) deserializePairPart(map.get("value"));
    }

    /** @return the key */
    public K getKey() {
        return key;
    }

    /** @param key the key to set */
    public void setKey(K key) {
        this.key = key;
    }

    /** @return the value */
    public V getValue() {
        return value;
    }

    /** @param value the value to set */
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public Map<String, Object> serialize() {
        if (!SerializationManager.isSerializable(getKey().getClass())) {
            throw new IllegalArgumentException("Key not serializable: " + getKey().getClass().getName());
        }
        if (!SerializationManager.isSerializable(getValue().getClass())) {
            throw new IllegalArgumentException("Value not serializable: " + getValue().getClass().getName());
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", serializePairPart(getKey()));
        map.put("value", serializePairPart(getValue()));
        return map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object serializePairPart(Object object) {
        Object serialized = SerializationManager.serializeOneLevel(object);
        if (serialized instanceof Map) {
            ((Map) serialized).put("classNameToDeserialize", object.getClass().getName());
        }
        return serialized;
    }

    @SuppressWarnings("rawtypes")
    private Object deserializePairPart(Object part) {
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
