package com.perceivedev.perceivecore.utilities.snapshots.implementation.player;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.perceivedev.perceivecore.utilities.snapshots.SnapshotProperty;

/**
 * Saves the Location of an {@link Entity}
 */
public class EntityLocationProperty extends SnapshotProperty<Entity> {

    private Location location;

    @Override
    public void restoreFor(Entity target) {
        throwUninitializedIfTrue(location == null);
        target.teleport(location);
    }

    @Override
    public SnapshotProperty<Entity> update(Entity target) {
        location = target.getLocation();
        return this;
    }

    @Override
    public SnapshotProperty<Entity> createForTarget(Entity target) {
        return new EntityLocationProperty().update(target);
    }
}
