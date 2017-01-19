package com.perceivedev.perceivecore.utilities.disable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * Allows DisableListeners to be added
 * <p>
 * Is bound to a plugin
 */
public class DisableManager implements Listener {

    private final Collection<DisableListener> listeners = new ArrayList<>();
    private final Set<DisableListener> weakListener = Collections.newSetFromMap(new WeakHashMap<>());

    @SuppressWarnings("unused")
    private final Plugin plugin;

    /**
     * @param plugin The owning plugin
     */
    @SuppressWarnings("unused")
    public DisableManager(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Adds a disable listener
     *
     * @param listener The {@link DisableListener} to add
     */
    @SuppressWarnings("unused")
    public synchronized void addListener(@Nonnull DisableListener listener) {
        Objects.requireNonNull(listener, "listener can not be null");

        listeners.add(listener);
    }

    /**
     * Adds a disable listener, using a WeakReference
     *
     * @param listener The {@link DisableListener} to add
     */
    @SuppressWarnings("unused")
    public synchronized void addWeakListener(@Nonnull DisableListener listener) {
        Objects.requireNonNull(listener, "listener can not be null");

        weakListener.add(listener);
    }

    /**
     * Removes a Listener
     *
     * @param listener The {@link DisableListener} to remove
     */
    @SuppressWarnings("unused")
    public synchronized void removeListener(@Nonnull DisableListener listener) {
        Objects.requireNonNull(listener, "listener can not be null");

        listeners.remove(listener);
        weakListener.remove(listener);
    }

    /**
     * Calls onDisable for all Listeners.
     * <p>
     * Should ideally not be visible from the outside, but it is in a different
     * package than PerceiveCore.class. Hope they will add super-packages
     * sometime
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public void disable() {
        synchronized (listeners) {
            listeners.forEach(DisableListener::onDisable);
        }
        synchronized (weakListener) {
            weakListener.forEach(DisableListener::onDisable);
        }
    }

    // ================= EVENTS =================

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disable();
        }
    }
}
