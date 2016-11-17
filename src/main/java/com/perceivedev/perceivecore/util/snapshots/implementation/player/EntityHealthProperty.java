package com.perceivedev.perceivecore.util.snapshots.implementation.player;

import org.bukkit.entity.LivingEntity;

import com.perceivedev.perceivecore.util.snapshots.SnapshotProperty;

/**
 * Allows for the saving and loading of entity health
 */
public class EntityHealthProperty extends SnapshotProperty<LivingEntity> {

    private double health = -1;

    @Override
    public void restoreFor(LivingEntity target) {
        throwUninitializedIfTrue(health < 0);
        target.setHealth(health);
    }

    @Override
    public SnapshotProperty<LivingEntity> update(LivingEntity target) {
        health = target.getHealth();
        return this;
    }

    @Override
    public SnapshotProperty<LivingEntity> createForTarget(LivingEntity target) {
        return new EntityHealthProperty().update(target);
    }
}
