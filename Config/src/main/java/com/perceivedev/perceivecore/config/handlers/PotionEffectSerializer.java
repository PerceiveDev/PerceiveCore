package com.perceivedev.perceivecore.config.handlers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.perceivedev.perceivecore.config.SerializationProxy;

/**
 * A serializer for {@link PotionEffect}s
 */
public class PotionEffectSerializer implements SerializationProxy<PotionEffect> {

    @Override
    public Map<String, Object> serialize(PotionEffect effect) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("type", effect.getType().getName());
        map.put("duration", effect.getDuration());
        map.put("amplifier", effect.getAmplifier());
        if (effect.getColor() != null) {
            map.put("colour", effect.getColor().asRGB());
        }
        map.put("ambient", effect.isAmbient());
        map.put("particles", effect.hasParticles());

        return map;
    }

    @Override
    public PotionEffect deserialize(Map<String, Object> data) {
        PotionEffectType type = PotionEffectType.getByName((String) data.get("type"));
        int duration = ((Number) data.get("duration")).intValue();
        int amplifier = ((Number) data.get("amplifier")).intValue();

        Color colour = null;
        if (data.containsKey("colour")) {
            colour = Color.fromRGB(Integer.parseInt((String) data.get("colour")));
        }

        boolean ambient = (boolean) data.get("ambient");
        boolean particles = (boolean) data.get("particles");

        if (colour != null) {
            return new PotionEffect(type, duration, amplifier, ambient, particles, colour);
        }

        return new PotionEffect(type, duration, amplifier, ambient, particles);
    }
}
