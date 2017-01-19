package com.perceivedev.perceivecore.utilities.snapshots.implementation.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.perceivedev.perceivecore.utilities.snapshots.SnapshotProperty;


/**
 * Contains all Potion effects of a player
 */
public class EntityPotionEffectProperty extends SnapshotProperty<LivingEntity> {

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
