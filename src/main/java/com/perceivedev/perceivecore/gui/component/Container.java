/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * A simple interface which just represents something that can contain items.
 * 
 * @author Rayzr
 */
public interface Container {

    /**
     * @param inventory the inventory to reference when retrieving the items
     * @return The contents of this ItemArea
     */
    ItemStack[] getContents(Rect pos, Inventory inventory);

    /**
     * @param inventory the inventory to set them in
     * @param contents the contents to set
     */
    void setContents(Rect pos, Inventory inventory, ItemStack[] contents);

}
