package com.perceivedev.perceivecore.config.handlers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * A {@link SerializationProxy} for Enums
 */
@SuppressWarnings("rawtypes")
public class EnumSerializer implements SerializationProxy<Enum> {

    @Override
    public Map<String, Object> serialize(Enum object) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("class", object.getClass().getName());
        map.put("constant", object.name());

        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum deserialize(Map<String, Object> data) {
        try {
            Class<? extends Enum> clazz = (Class<? extends Enum>) Class.forName((String) data.get("class"));

            return Enum.valueOf(clazz, (String) data.get("constant"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
