package com.perceivedev.bukkitpluginutilities.config;

import java.util.HashMap;
import java.util.Map;

/**
 * A Proxy to serialize another class
 *
 * @param <T> The type of the class to proxy
 */
public interface SimpleSerializationProxy <T> extends SerializationProxy<T> {

    /**
     * Do <i>NOT</i> override this method. This is used internally.
     *
     * @see SerializationProxy#deserialize(Map)
     */
    @Override
    default Map<String, Object> serialize(T object) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("value", serializeSimple(object));
        return map;
    }

    /**
     * Do <i>NOT</i> override this method. This is used internally.
     *
     * @see SerializationProxy#deserialize(Map)
     */
    @Override
    default T deserialize(Map<String, Object> data) {
        return deserializeSimple(data.get("value"));
    }

    /**
     * Serializes an object
     *
     * @param object The object to serialize
     *
     * @return The serialized form
     */
    Object serializeSimple(T object);

    /**
     * Deserialized an object
     *
     * @param data The data of the object
     *
     * @return The deserialized object
     */
    T deserializeSimple(Object data);

}
