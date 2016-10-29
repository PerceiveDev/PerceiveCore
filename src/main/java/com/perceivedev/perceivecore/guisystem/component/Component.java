package com.perceivedev.perceivecore.guisystem.component;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A component of a Scene
 */
public interface Component extends Cloneable {

    /**
     * Called if this component is clicked
     *
     * @param clickEvent The ClickEvent
     */
    void onClick(InventoryClickEvent clickEvent);

    /**
     * Returns the size of a component
     *
     * @return The size of this component
     */
    Dimension getSize();

    /**
     * Renders in the given inventory
     *
     * @param inventory The inventory to render into
     * @param player The player to render for
     * @param x The x coordinate of the upper left corner
     * @param y The y coordinate of the upper left corner
     */
    void render(Inventory inventory, Player player, int x, int y);

    /**
     * Converts a slot to a grid value
     *
     * @param invSize The size of the inventory
     * @param slot The slot
     *
     * @return The x (0) and y (1) coordinate
     */
    default int[] slotToGrid(int invSize, int slot) {
        // account for hoppers and other strange stuff
        int y = slot / (invSize >= 9 ? 9 : invSize);
        int x = slot % (invSize >= 9 ? 9 : invSize);
        return new int[] { x, y };
    }

    /**
     * Converts a grid value to a slot
     *
     * @param invSize The size of the inventory
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return The resulting slot index
     */
    default int gridToSlot(int invSize, int x, int y) {
        // account for hoppers and other strange stuff
        return (invSize >= 9 ? 9 : invSize) * y + x;
    }

    /**
     * Iterates over the range, accepts each x and y value
     *
     * @param minX The min x (inclusive)
     * @param maxX The max x (exclusive)
     * @param minY The min y (inclusive)
     * @param maxY The max y (exclusive)
     * @param consumer The consumer. First is x, second is y.
     */
    default void iterateOverRange(int minX, int maxX, int minY, int maxY, BiConsumer<Integer, Integer> consumer) {
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                consumer.accept(x, y);
            }
        }
    }

    /**
     * Clones the component
     *
     * @return A deepClone of this component
     */
    Component deepClone();
}
