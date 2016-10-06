package com.perceivedev.perceivecore.other;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Allows DisableListeners to be added
 */
public class DisableManager {

    private Collection<DisableListener> listeners = new ArrayList<>();

    /**
     * Adds a disable listener
     *
     * @param listener The {@link DisableListener} to add
     */
    public void addListener(DisableListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a Listener
     *
     * @param listener The {@link DisableListener} to remove
     */
    public void removeListener(DisableListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calls onDisable for all Listeners.
     * <p>
     * Should ideally not be visible from the outside, but it is in a different package than PerceiveCore.class.
     * Hope they will add super-packages sometime
     */
    public void disable() {
        listeners.forEach(DisableListener::onDisable);
    }
}
