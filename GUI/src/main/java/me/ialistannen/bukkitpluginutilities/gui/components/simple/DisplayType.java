package me.ialistannen.bukkitpluginutilities.gui.components.simple;


import me.ialistannen.bukkitpluginutilities.utilities.item.DisplayColor;
import me.ialistannen.bukkitpluginutilities.utilities.item.ItemFactory;

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
