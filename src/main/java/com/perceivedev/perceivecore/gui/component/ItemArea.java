/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.DisplayColor;
import com.perceivedev.perceivecore.gui.DisplayType;
import com.perceivedev.perceivecore.gui.GUIHolder;

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

    public ItemArea(boolean interactable) {
        super(DisplayType.EMPTY, DisplayColor.WHITE);
        this.interactable = interactable;
    }

    public ItemArea() {
        this(false);
    }

    /**
     * Gets the contents of this ItemArea in reference to the given
     * {@link Inventory}.
     * 
     * @param inventory the Inventory to get the contents from
     */
    @Override
    public ItemStack[] getContents(Rect pos, Inventory inventory) {
        ItemStack[] items = new ItemStack[pos.getX() + pos.getY() * 9];
        for (int ix = pos.getX(); ix < pos.getX() + pos.getWidth(); ix++) {
            for (int iy = pos.getY(); iy < pos.getY() + pos.getHeight(); iy++) {
                items[ix + iy * 9] = inventory.getItem(ix + iy * 9);
            }
        }
        return items;
    }

    /**
     * Sets the contents of this ItemArea in reference to the given
     * {@link Inventory}.
     * 
     * @param inventory the Inventory to set the contents of
     * @param contents the items to set the contents to
     */
    @Override
    public void setContents(Rect pos, Inventory inventory, ItemStack[] contents) {
        if (contents == null) {
            for (int ix = pos.getX(); ix < pos.getX() + pos.getWidth(); ix++) {
                for (int iy = pos.getY(); iy < pos.getY() + pos.getHeight(); iy++) {
                    inventory.setItem(ix + iy * 9, null);
                }
            }
        } else {
            for (int ix = pos.getX(); ix < pos.getX() + pos.getWidth(); ix++) {
                for (int iy = pos.getY(); iy < pos.getY() + pos.getHeight(); iy++) {
                    inventory.setItem(ix + iy * 9, contents[ix + iy * 9]);
                }
            }
        }
    }

    /**
     * Renders as empty, due to the nature of ItemArea
     * 
     * @see Component#render(GUIHolder, int, int)
     */
    @Override
    protected ItemStack render(GUIHolder holder, int posX, int posY) {
        return null;
    }

    @Override
    protected void onClick(ClickEvent e) {
        e.setCancelled(!interactable);
    }

    /**
     * @return If this ItemArea is interactable
     */
    public boolean isInteractable() {
        return interactable;
    }

    /**
     * @param interacatable the value of interactable to set
     */
    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

}
