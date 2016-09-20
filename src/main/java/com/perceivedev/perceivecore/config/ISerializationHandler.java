package com.perceivedev.perceivecore.config;

import java.util.Map;
import java.util.Objects;

public interface ISerializationHandler <T extends Object> {

    /**
     * DO NOT EVER MODIFY OR USE THIS. THIS IS ESSENTIAL FOR SERIALIZATION TO
     * WORK.
     * <p>
     * <p>
     * <b><i>{@literal @I Al Istannen THEN PROVIDE PROPER JAVADOC! THIS METHOD IS UGLY}</i></b>
     *
     * @param obj
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    default Map<String, Object> _serialize(Object obj) {

        return serialize((T) obj);

    }

    /**
     * Serializes an object.
     *
     * @param obj The object to Serialize
     *
     * @return The Serialized representation
     */
    Map<String, Object> serialize(T obj);

    /**
     * Deserializes an object.
     *
     * @param map The Serialized representation
     *
     * @return The deserialized Object.
     */
    T deserialize(Map<String, Object> map);


    // ==== TODO: These methods DO NOT BELONG HERE ====

    /**
     * Parses a Value (its toString()) to an integer
     *
     * @param map The Map to get the key from
     * @param key The key of the entry
     *
     * @return The value parsed to an int or 0 in case of a failure.
     *
     * @throws NullPointerException if map or key are null
     */
    default int parseToInt(Map<String, Object> map, String key) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(key);

        if (!map.containsKey(key) || map.get(key) == null) {
            return 0;
        }
        try {
            // use ParseInteger. It looks nicer and is one less object being needlessly created.
            // Look here: http://stackoverflow.com/questions/7355024/integer-valueof-vs-integer-parseint
            return Integer.parseInt(map.get(key).toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses a Value (its toString()) to a double
     *
     * @param map The Map to get the key from
     * @param key The key of the entry
     *
     * @return The value parsed to a double or 0 in case of a failure.
     *
     * @throws NullPointerException if map or key are null
     */
    default double parseToDouble(Map<String, Object> map, String key) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(key);

        if (!map.containsKey(key) || map.get(key) == null) {
            return 0;
        }

        try {
            return Double.parseDouble(map.get(key).toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses a Value (its toString()) to a double
     *
     * @param map The Map to get the key from
     * @param key The key of the entry
     *
     * @return The value parsed to a double or 0 in case of a failure.
     *
     * @throws NullPointerException if map or key are null
     */
    default float parseToFloat(Map<String, Object> map, String key) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(key);

        if (!map.containsKey(key) || map.get(key) == null) {
            return 0;
        }

        try {
            return Float.parseFloat(map.get(key).toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
