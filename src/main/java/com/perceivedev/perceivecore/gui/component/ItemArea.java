/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.DisplayColor;
import com.perceivedev.perceivecore.gui.DisplayType;

/**
 * 
 * Defines an area where a certain set of items are displayed. It's possible to
 * make this area interactable.
 * 
 * @author Rayzr
 *
 */
public class ItemArea extends Component implements Container {

    protected boolean interactable;

    public ItemArea(int x, int y, int width, int height, boolean interactable) {
	super(x, y, width, height, DisplayType.EMPTY, DisplayColor.WHITE);
	this.interactable = interactable;
    }

    public ItemArea(int x, int y, int width, int height) {
	this(x, y, width, height, false);
    }

    @Override
    public ItemStack[] getContents(Inventory inventory) {
	ItemStack[] items = new ItemStack[x + y * 9];
	for (int ix = x; ix < x + width; ix++) {
	    for (int iy = y; iy < y + height; iy++) {
		items[ix + iy * 9] = inventory.getItem(ix + iy * 9);
	    }
	}
	return items;
    }

    @Override
    public void setContents(Inventory inventory, ItemStack[] contents) {
	if (contents == null) {
	    for (int ix = x; ix < x + width; ix++) {
		for (int iy = y; iy < y + height; iy++) {
		    inventory.setItem(ix + iy * 9, null);
		}
	    }
	} else {
	    for (int ix = x; ix < x + width; ix++) {
		for (int iy = y; iy < y + height; iy++) {
		    inventory.setItem(ix + iy * 9, contents[ix + iy * 9]);
		}
	    }
	}
    }

    @Override
    protected boolean onClick(Player player, int offX, int offY) {
	return interactable;
    }

    /**
     * @return If this ItemArea is interacatable
     */
    public boolean isInteractable() {
	return interactable;
    }

    /**
     * @param interacatable
     *            the value of interacatable to set
     */
    public void setInteractable(boolean interactable) {
	this.interactable = interactable;
    }

}
