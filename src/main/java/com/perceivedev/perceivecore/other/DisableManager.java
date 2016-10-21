package com.perceivedev.perceivecore.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Allows DisableListeners to be added
 */
public class DisableManager {

    // FIXME: 21.10.2016 Not thread safe

    private Collection<DisableListener> listeners    = new ArrayList<>();
    private Set<DisableListener>        weakListener = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Adds a disable listener
     *
     * @param listener The {@link DisableListener} to add
     */
    public void addListener(DisableListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds a disable listener, using a WeakReference
     *
     * @param listener The {@link DisableListener} to add
     */
    public void addWeakListener(DisableListener listener) {
        weakListener.add(listener);
    }

    /**
     * Removes a Listener
     *
     * @param listener The {@link DisableListener} to remove
     */
    public void removeListener(DisableListener listener) {
        listeners.remove(listener);
        weakListener.remove(listener);
    }

    /**
     * Calls onDisable for all Listeners.
     * <p>
     * Should ideally not be visible from the outside, but it is in a different package than PerceiveCore.class.
     * Hope they will add super-packages sometime
     */
    public void disable() {
        listeners.forEach(DisableListener::onDisable);
        weakListener.forEach(DisableListener::onDisable);
    }
}
