package com.perceivedev.perceivecore.util.snapshots.implementation.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.perceivedev.perceivecore.config.SerializationManager;
import com.perceivedev.perceivecore.config.SerializationProxy;
import com.perceivedev.perceivecore.util.snapshots.SnapshotProperty;

/**
 * Contains all Potion effects of a player
 */
public class EntityPotionEffectProperty extends SnapshotProperty<LivingEntity> {

    static {
        SerializationManager.addSerializationProxy(PotionEffect.class, new SerializationProxy<PotionEffect>() {
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
        });
    }

    private List<PotionEffect> potionEffects = new ArrayList<>();

    @Override
    public void restoreFor(LivingEntity target) {
        Objects.requireNonNull(target, "target cannot be null!");

        target.getActivePotionEffects().stream()
                .map(PotionEffect::getType)
                .forEach(target::removePotionEffect);

        potionEffects.forEach(target::addPotionEffect);
    }

    @Override
    public SnapshotProperty<LivingEntity> update(LivingEntity target) {
        Objects.requireNonNull(target, "target cannot be null!");

        potionEffects.clear();

        potionEffects.addAll(target.getActivePotionEffects());
        return this;
    }

    @Override
    public SnapshotProperty<LivingEntity> createForTarget(LivingEntity target) {
        return new EntityPotionEffectProperty().update(target);
    }
}
