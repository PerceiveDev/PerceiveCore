/**
 * 
 */
package com.perceivedev.perceivecore.packet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.perceivedev.perceivecore.packet.ConverterMap.Pair;
import com.perceivedev.perceivecore.util.Converter;

/**
 * @author Rayzr
 *
 */
public class ConverterMap extends HashMap<Pair<Class<?>, Class<?>>, Converter<?, ?>> {

    /**
     * 
     */
    private static final long serialVersionUID = -2147216278888452139L;

    /**
     * Attempts to add a converter. This will return false if there was already
     * a converter for that class pair. This ignores the ordering of {@code a}
     * and {@code b}
     * 
     * @param a the first class
     * @param b the second class
     * @param converter the converter to add
     * @return Whether or not it actually added anything
     */
    public <A, B> boolean addConverter(Class<?> a, Class<?> b, Converter<A, B> converter) {
        if (hasConverter(a, b)) {
            return false;
        }

        put(new Pair<Class<?>, Class<?>>(a, b), converter);

        return true;
    }

    /**
     * Attempts to remove a converter. This ignores the ordering of {@code a}
     * and {@code b}
     * 
     * @param a the first class
     * @param b the second class
     * @param converter the converter itself
     * @return Whether or not it actually removed anything
     */
    public <A, B> boolean removeConverter(Class<?> a, Class<?> b, Converter<A, B> converter) {
        if (!hasConverter(a, b)) {
            return false;
        }

        if (!remove(new Pair<Class<?>, Class<?>>(a, b), converter) && !remove(new Pair<Class<?>, Class<?>>(a, b), converter)) {
            return false;
        }

        return true;
    }

    /**
     * Attempts to remove a converter. This ignores the ordering of {@code a}
     * and {@code b}
     * 
     * @param a the first class
     * @param b the second class
     * @return Whether or not it actually removed anything
     */
    public <A, B> boolean removeConverter(Class<?> a, Class<?> b) {
        if (!hasConverter(a, b)) {
            return false;
        }

        if (remove(new Pair<Class<?>, Class<?>>(a, b)) == null && remove(new Pair<Class<?>, Class<?>>(a, b)) == null) {
            return false;
        }

        return true;
    }

    /**
     * Returns whether or not a converter is provided for the given class pair.
     * This ignores the ordering of {@code a} and {@code b}
     * 
     * @param a the first class
     * @param b the second class
     * @return Whether or not a converter exists for the given pair of classes
     */
    public boolean hasConverter(Class<?> a, Class<?> b) {
        return containsKey(new Pair<Class<?>, Class<?>>(a, b)) || containsKey(new Pair<Class<?>, Class<?>>(b, a));
    }

    /**
     * Better than an {@link Entry} ;)
     * 
     * @author Rayzr
     *
     * @param <K>
     * @param <V>
     */
    public class Pair<K extends Object, V extends Object> implements Serializable, ConfigurationSerializable {

        /**
         * 
         */
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

}
