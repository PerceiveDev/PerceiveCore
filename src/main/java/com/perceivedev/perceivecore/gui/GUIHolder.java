/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.component.Component;
import com.perceivedev.perceivecore.gui.component.Container;
import com.perceivedev.perceivecore.gui.component.Rect;

/**
 * 
 * This class is an extension of {@link InventoryHolder} which stores a
 * reference to it's owning {@link GUI}, as well as a map of arbitrary data.
 * 
 * @author Rayzr
 *
 */
public class GUIHolder implements InventoryHolder {

    private GUI                 gui;
    private Inventory           inventory;
    private Map<String, Object> data = new HashMap<String, Object>();

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
            comp.render(getPos(comp), this);
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
     * @param component the component to get the position of
     * @return The position or null if the component couldn't be found
     */
    private Rect getPos(Component component) {
        return gui.getPos(component);
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
        return ((Container) component).getContents(getPos(component), inventory);
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
        ((Container) component).setContents(getPos(component), inventory, contents);
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
            if (comp.checkClick(getPos(comp), player, rawSlot % 9, rawSlot / 9, event)) {
                break;
            }
        }

    }

    /**
     * Sets a piece of arbitrary data
     * 
     * @param key the key of the data
     * @param value the value of the data
     */
    public void setData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Gets a piece of arbitrary data
     * 
     * @param key the key of the data
     * @return The data. Will be null if no data was found with that key.
     */
    public Object getData(String key) {
        return data.get(key);
    }

    /**
     * @param key
     * @see #getData(String)
     */
    public String getString(String key) {
        return getData(key).toString();
    }

    /**
     * @param key
     * @see #getData(String)
     */
    public int getInt(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * @param key
     * @see #getData(String)
     */
    public double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * @param key
     * @see #getData(String)
     */
    public float getFloat(String key) {
        try {
            return Float.parseFloat(getString(key));
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    /**
     * @param key
     * @see #getData(String)
     */
    public long getLong(String key) {
        try {
            return Long.parseLong(getString(key));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

}
