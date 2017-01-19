package com.perceivedev.perceivecore.config.handlers;

import java.util.UUID;

import com.perceivedev.perceivecore.config.SerializationManager;
import com.perceivedev.perceivecore.config.SimpleSerializationProxy;


/**
 * Adds the ability for {@link SerializationManager} to serialize and
 * deserialize objects of type {@link UUID}
 *
 * @author Rayzr
 */
public class UUIDSerializer implements SimpleSerializationProxy<UUID> {

    @Override
    public Object serializeSimple(UUID object) {
        return object.toString();
    }

    @Override
    public UUID deserializeSimple(Object data) {
        return UUID.fromString(data.toString());
    }

}
