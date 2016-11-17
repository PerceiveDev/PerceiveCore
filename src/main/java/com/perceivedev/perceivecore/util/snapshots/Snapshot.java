package com.perceivedev.perceivecore.util.snapshots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.perceivedev.perceivecore.config.ConfigSerializable;

/**
 * A snapshot
 *
 * @param <T> The type of the object which the snapshot is for
 */
public class Snapshot<T> implements ConfigSerializable {

    private List<SnapshotProperty<? super T>> snapshotProperties = new ArrayList<>();

    /**
     * For serializing
     */
    private Snapshot() {
    }

    /**
     * Creates a Snapshot for the given Target and the given properties
     *
     * @param target The target to snapshot
     * @param properties The properties to snapshot
     */
    public Snapshot(T target, Collection<SnapshotProperty<? super T>> properties) {
        Objects.requireNonNull(target, "target can not be null!");
        Objects.requireNonNull(properties, "properties can not be null!");

        properties.stream()
                .map(property -> property.createForTarget(target))
                .sequential()
                .forEach(snapshotProperties::add);
    }

    /**
     * Creates a Snapshot for the given Target and the given properties
     *
     * @param target The target to snapshot
     * @param properties The properties to snapshot
     * 
     * @see #Snapshot(Object, Collection)
     */
    public Snapshot(T target, SnapshotProperty<? super T>... properties) {
        this(target, Arrays.asList(properties));
    }

    /**
     * Restores this state for the given target
     *
     * @param target The target to restoreFor it to
     */
    public void restore(T target) {
        Objects.requireNonNull(target, "target can not be null!");

        snapshotProperties.forEach(property -> property.restoreFor(target));
    }

    /**
     * Updates its inner state to the one of the given target
     *
     * @param target The target to update its state from
     */
    public void update(T target) {
        Objects.requireNonNull(target, "target can not be null!");

        snapshotProperties.forEach(property -> property.update(target));
    }
}
