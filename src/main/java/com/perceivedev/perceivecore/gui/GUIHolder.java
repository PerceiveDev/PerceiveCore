/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.component.Component;
import com.perceivedev.perceivecore.gui.component.Container;

/**
 * 
 * This class is an extension of {@link InventoryHolder} which stores a
 * reference to it's owning {@link GUI}, as well as a map of arbitrary data.
 * 
 * @author Rayzr
 *
 */
public class GUIHolder implements InventoryHolder {

    private GUI       gui;
    private Inventory inventory;

    /**
     * @param name the name of the inventory
     * @param rows
     */
    public GUIHolder(GUI gui) {
        this.gui = gui;
        inventory = Bukkit.createInventory(this, gui.getRows() * 9, gui.getName());
        render();
    }

    private void render() {

        for (Component comp : gui.getComponents()) {

            comp.render(this);

        }

        gui.render(this);

    }

    /**
     * Sets an item at the given location
     * 
     * @param x the x position
     * @param y the y position
     * @param item the item
     */
    public void setItem(int x, int y, ItemStack item) {

        setItem(x + y * 9, item);

    }

    /**
     * Sets an item at the given location
     * 
     * @param pos the position
     * @param item the item
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
     * @param inventory the inventory to reference when retrieving the items
     * @return The contents of this ItemArea
     */
    public ItemStack[] getContents(int componentId) {
        if (gui.getComponent(componentId) == null) {
            return null;
        }
        Component component = gui.getComponent(componentId);
        if (!(component instanceof Container)) {
            return null;
        }
        return ((Container) component).getContents(inventory);

    }

    /**
     * Sets the contents of a component to {@code contents}. The component
     * represented by {@code componentId} must implement {@link Container}.
     * 
     * @param inventory the inventory to set them in
     * @param contents the contents to set
     */
    public void setContents(int componentId, ItemStack[] contents) {
        if (gui.getComponent(componentId) == null) {
            throw new IllegalArgumentException("No such component with ID #" + componentId);
        }
        Component component = gui.getComponent(componentId);
        if (!(component instanceof Container)) {
            throw new IllegalArgumentException("Component with ID #" + componentId + " is not a container!");
        }
        ((Container) component).setContents(inventory, contents);
    }

    /**
     * Handle a click. This is intended to be called from within an
     * {@link InventoryInteractEvent} listener.
     * 
     * @param player the player
     * @param event the InventoryClickEvent itself
     */
    public void handleClick(Player player, InventoryClickEvent event) {

        int rawSlot = event.getRawSlot();

        for (Component comp : gui.getComponents()) {

            if (comp.checkClick(player, rawSlot % 9, rawSlot / 9, event)) {

                break;

            }

        }

    }

}
