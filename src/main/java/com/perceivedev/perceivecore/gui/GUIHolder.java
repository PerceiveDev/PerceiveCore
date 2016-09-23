/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.component.Component;

/**
 * @author Rayzr
 *
 */
public class GUIHolder implements InventoryHolder {

    private GUI	      gui;
    private Inventory inventory;

    /**
     * @param name
     *            the name of the inventory
     * @param rows
     */
    public GUIHolder(GUI gui) {
	this.gui = gui;
	inventory = Bukkit.createInventory(this, gui.getRows() * 9, gui.getName());
	render();
    }

    private void render() {

	for (Component c : gui.getComponents()) {

	    c.render(this);

	}

	gui.render(this);

    }

    /**
     * Sets an item at the given location
     * 
     * @param x
     *            the x position
     * @param y
     *            the y position
     * @param item
     *            the item
     */
    public void setItem(int x, int y, ItemStack item) {

	setItem(x + y * 9, item);

    }

    /**
     * Sets an item at the given location
     * 
     * @param pos
     *            the position
     * @param item
     *            the item
     */
    public void setItem(int pos, ItemStack item) {

	if (pos < 0 || pos > inventory.getSize()) {
	    return;
	}

	inventory.setItem(pos, item);

    }

    /**
     * 
     * @see org.bukkit.inventory.InventoryHolder#getInventory()
     */
    @Override
    public Inventory getInventory() {
	return inventory;
    }

    /**
     * @param p
     * @param rawSlot
     */
    public void handleClick(Player p, int rawSlot) {

	for (Component c : gui.getComponents()) {

	    if (c.checkClick(p, rawSlot % 9, rawSlot / 9)) {

		break;

	    }

	}

    }

}
