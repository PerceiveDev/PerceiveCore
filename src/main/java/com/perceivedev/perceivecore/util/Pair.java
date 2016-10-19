package com.perceivedev.perceivecore.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Better than an {@link Map.Entry} ;)
 *
 * @param <K> The key type
 * @param <V> The Value type
 *
 * @author Rayzr
 */
public class Pair<K, V> implements Serializable, ConfigurationSerializable {

    /**
     *
     */
    private static final long serialVersionUID = 7388136271482352386L;

    private K key;
    private V value;

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
        this.key = (K) map.get("key");
        this.value = (V) map.get("value");
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        map.put("value", value);
        return map;
    }

}
