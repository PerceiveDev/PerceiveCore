package com.perceivedev.perceivecore.util.snapshots;

import com.perceivedev.perceivecore.config.ConfigSerializable;

/**
 * A Property that can be loaded and saved
 * 
 * @param <T> The type of the class this property can be read from
 */
public abstract class SnapshotProperty<T> implements ConfigSerializable {

    /**
     * Creates a new Property without any value.
     * 
     * Needs to be updated before it can restore anything
     */
    public SnapshotProperty() {
    }

    /**
     * Restores the value of the target to the one saved in here
     * 
     * @param target The target to restore it for
     * 
     * @throws IllegalStateException if the property was never saved
     */
    public abstract void restoreFor(T target);

    /**
     * Updates this property to be the same as the target
     * 
     * @param target The target to update it to
     *
     * @return This property
     */
    public abstract SnapshotProperty<T> update(T target);

    /**
     * Clones this Property to return a new instance, which holds the value for
     * the given target
     * 
     * @param target The target to create a new snapshot property for
     * @return The created {@link SnapshotProperty} for the target
     */
    public abstract SnapshotProperty<T> createForTarget(T target);

    /**
     * Throws an {@link IllegalStateException}
     * 
     * It indicates that this property was never updated, but restore was called
     * 
     * @throws IllegalStateException to indicate that this property was never
     *             updated
     */
    protected void throwUninitializedException() {
        throw new IllegalStateException("This property was never initialized.");
    }

    /**
     * Throws an {@link IllegalStateException}, if the condition is true
     * 
     * It indicates that this property was never updated, but restore was called
     * 
     * @param condition The condition. If this true, an error will be thrown
     * 
     * @throws IllegalStateException to indicate that this property was never
     *             updated
     * @see #throwUninitializedException()
     */
    protected void throwUninitializedIfTrue(boolean condition) {
        if (condition) {
            throwUninitializedException();
        }
    }
}
