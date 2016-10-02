package com.perceivedev.perceivecore.guisystem;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A Pane to add Components to
 */
public interface Pane {

    /**
     * Reacts to a click
     *
     * @param event The event. Distributes it to the place it belongs.
     */
    void onClick(InventoryClickEvent event);

    /**
     * Adds a component
     *
     * @param component The component to add
     *
     * @return True if the component was added
     */
    boolean addComponent(Component component);

    /**
     * Removes a component
     *
     * @param component The component to remove
     */
    void removeComponent(Component component);

    /**
     * Checks if it contains a component
     *
     * @param component The component to search
     *
     * @return True if it contains the component
     */
    boolean containsComponent(Component component);

    /**
     * Returns the size of this pane
     *
     * @return The size of this pane
     */
    Dimension getSize();

    /**
     * Returns all children of this pane
     *
     * @return All the children, Not modifiable.
     */
    Collection<Component> getChildrenUnmodifiable();

    /**
     * Renders the components in an inventory
     *
     * @param inventory The inventory to modify
     * @param player The player to render for
     */
    void render(Inventory inventory, Player player);

    /**
     * Converts a slot to a grid value
     *
     * @param slot The slot
     *
     * @return The x (0) and y (1) coordinate
     */
    default int[] slotToGrid(int slot) {
        int y = slot / getSize().getWidth();
        int x = slot % getSize().getWidth();
        return new int[] { x, y };
    }

    /**
     * Converts a grid value to a slot
     *
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return The resulting slot index
     */
    default int gridToSlot(int x, int y) {
        return getSize().getWidth() * y + x;
    }
}
