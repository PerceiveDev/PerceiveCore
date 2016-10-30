package com.perceivedev.perceivecore.packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.perceivedev.perceivecore.PerceiveCore;

/**
 * Manages PacketListeners and stuff
 */
public class PacketManager implements Listener {

    private static PacketManager instance;

    private final Map<UUID, PacketInjector> injectorMap = new HashMap<>();

    {
        PerceiveCore.getInstance().getDisableManager()
                  .addListener(() -> {
                      shutdown();
                      instance = null;
                  });
    }

    /**
     * Instantiates a new PacketManager
     *
     * @param plugin The plugin to instantiate it as
     */
    private PacketManager(PerceiveCore plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Adds a packet listener
     *
     * @param listener The {@link PacketListener} to add
     * @param player The Player to listen for
     *
     * @throws NullPointerException if any parameter is null
     */
    public void addListener(PacketListener listener, Player player) {
        Objects.requireNonNull(listener, "listener can not be null");
        Objects.requireNonNull(player, "player can not be null");

        // no modifications during checks or the result may be wrong! (it changes depending on the current state)
        synchronized (injectorMap) {
            if (injectorMap.containsKey(player.getUniqueId())) {
                injectorMap.get(player.getUniqueId()).addPacketListener(listener);
            } else {
                PacketInjector injector = new PacketInjector(player);
                injector.addPacketListener(listener);
                injectorMap.put(player.getUniqueId(), injector);
            }
        }
    }

    /**
     * Removes the Listener for a player
     *
     * @param listener The listener to remove
     * @param player The player to remove it for
     *
     * @throws NullPointerException if any parameter is null
     */
    @SuppressWarnings("unused")
    public void removeListener(PacketListener listener, Player player) {
        Objects.requireNonNull(listener, "listener can not be null");
        Objects.requireNonNull(player, "player can not be null");

        // no modifications during checks or the result may be wrong! (it changes depending on the current state)
        synchronized (injectorMap) {
            if (!injectorMap.containsKey(player.getUniqueId())) {
                return;
            }
            PacketInjector injector = injectorMap.get(player.getUniqueId());
            injector.removePacketListener(listener);
            if (injector.getListenerAmount() < 1) {
                injector.detach();
                injectorMap.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Removes <b>all</b> listeners from a player
     *
     * @param uuid The {@link UUID} of the Player to remove all listeners for
     *
     * @throws NullPointerException if uuid is null
     */
    public void removeAllListeners(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid can not be null");

        // no modifications during checks or the result may be wrong! (it changes depending on the current state)
        synchronized (injectorMap) {
            if (injectorMap.containsKey(uuid)) {
                injectorMap.get(uuid).detach();
                injectorMap.remove(uuid);
            }
        }
    }

    /**
     * <i>Removes <b>ALL</b> listeners</i>
     * <p>
     * Use with caution or not at all.
     */
    public void shutdown() {
        synchronized (injectorMap) {
            // clone to avoid concurrent modification
            Set<UUID> keys = new HashSet<>(injectorMap.keySet());
            keys.forEach(this::removeAllListeners);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        // clean up
        removeAllListeners(event.getPlayer().getUniqueId());
    }

    /**
     * Returns the Manager instance
     *
     * @return An instance of the PacketManager
     */
    public static PacketManager getInstance() {
        if (instance == null) {
            instance = new PacketManager(PerceiveCore.getInstance());
        }
        return instance;
    }
}
