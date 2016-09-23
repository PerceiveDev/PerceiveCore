/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Rayzr
 *
 */
public interface Container {

    /**
     * @param inventory
     *            the inventory to reference when retrieving the items
     * @return The contents of this ItemArea
     */
    public ItemStack[] getContents(Inventory inventory);

    /**
     * @param inventory
     *            the inventory to set them in
     * @param contents
     *            the contents to set
     */
    public void setContents(Inventory inventory, ItemStack[] contents);

}
