package com.perceivedev.perceivecore.config;

import java.util.Map;

/**
 * A Proxy to serialize another class
 *
 * @param <T> The type of the class to proxy
 */
public interface SerializationProxy<T> {

    /**
     * Serializes an object
     *
     * @param object The object to serialize
     *
     * @return The serialized form
     */
    Map<String, Object> serialize(T object);

    /**
     * Deserialized an object
     *
     * @param data The data of the object
     *
     * @return The deserialized object
     */
    T deserialize(Map<String, Object> data);
}
