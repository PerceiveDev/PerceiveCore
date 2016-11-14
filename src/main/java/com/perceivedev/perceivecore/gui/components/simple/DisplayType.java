package com.perceivedev.perceivecore.gui.components.simple;

import com.perceivedev.perceivecore.util.ItemFactory;

/**
 * Displays an item in the given color.
 * <p>
 * There are standard implementations available in
 * {@link StandardDisplayTypes}
 */
@FunctionalInterface
public interface DisplayType {

    /**
     * Colors the item
     *
     * @param color The Color to apply
     *
     * @return The colored item
     */
    ItemFactory getColouredItem(DisplayColor color);
}
