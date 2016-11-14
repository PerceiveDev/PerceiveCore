package com.perceivedev.perceivecore.gui.base;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.Gui;
import com.perceivedev.perceivecore.gui.util.Dimension;

/** A Component */
public interface Component {

    /**
     * Renders the component in the Inventory
     *
     * @param inventory The inventory to render in
     * @param player The Player to render for
     * @param offsetX The c offset
     * @param offsetY The y offset
     */
    void render(Inventory inventory, Player player, int offsetX, int offsetY);

    /**
     * Reacts to a click event
     *
     * @param clickEvent The {@link ClickEvent}
     */
    void onClick(ClickEvent clickEvent);

    /**
     * Returns the height of the component
     *
     * @return The height of this Component
     */
    int getHeight();

    /**
     * Returns the width of the component
     *
     * @return The width of this Component
     */
    int getWidth();

    /**
     * Returns the size of this component
     *
     * @return The size of this Component
     */
    Dimension getSize();

    /**
     * Deep clones this component
     *
     * @return A deep clone of this component
     */
    Component deepClone();

    /**
     * Sets the Gui this Component belongs to
     * <p>
     * <b><i>You shall not call this method. Ideally it would only be visible
     * inside the package, but being Java this doesn't work</i></b>
     *
     * @param gui The gui
     */
    void setGui(Gui gui);

    /**
     * Gets the Gui this component belongs to.
     *
     * @return The Gui this component is in. May be null
     */
    Gui getGui();

    /**
     * Converts a slot to Grid coordinates
     *
     * @param slot The slot
     *
     * @return The grid position. [0] ==> X, [1] == Y
     */
    default int[] slotToGrid(int slot) {
        return new int[] { slot % 9, slot / 9 };
    }

    /**
     * Converts a grid position to a slot
     *
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return The resulting slot
     */
    default int gridToSlot(int x, int y) {
        return y * 9 + x;
    }

    /**
     * Iterates over a 2 dimensional range and passes the results to the
     * consumer
     *
     * @param minX The min X (inclusive)
     * @param maxX The max X (exclusive)
     * @param minY The min Y (inclusive)
     * @param maxY The max Y (exclusive)
     * @param consumer The consumer
     */
    default void iterateOver2DRange(int minX, int maxX, int minY, int maxY, BiConsumer<Integer, Integer> consumer) {
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                consumer.accept(x, y);
            }
        }
    }
}
