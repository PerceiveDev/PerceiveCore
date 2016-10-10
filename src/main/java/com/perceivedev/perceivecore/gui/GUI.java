/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.component.Component;
import com.perceivedev.perceivecore.util.ItemUtils;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author Rayzr
 *
 */
public class GUI {

    private List<Component> components = new ArrayList<>();
    private String          name;
    private int             rows;

    private DisplayType     fill;
    private DisplayColor    color;

    public GUI(String name, int rows, DisplayType fill, DisplayColor color) {

        this.name = TextUtils.colorize(name);

        rows = rows < 1 ? 1 : rows > 6 ? 6 : rows;
        this.rows = rows;

        this.fill = fill;
        this.color = color;

    }

    public GUI(String name, int rows, DisplayColor color) {
        this(name, rows, DisplayType.EMPTY, color);
    }

    public GUI(String name, int rows, DisplayType type) {
        this(name, rows, type, DisplayColor.BLACK);
    }

    public GUI(String name, int rows) {
        this(name, rows, DisplayColor.BLACK);
    }

    /**
     * @return the components
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Adds a component and returns the id of it
     * 
     * @param component the component
     * @return The component's id
     */
    public int addComponent(Component component) {
        components.add(component);
        component.setGui(this);
        return components.size() - 1;
    }

    /**
     * Gets a component with the given id
     * 
     * @param componentId the id of the component
     * @return The component. This will be null if no component could be found
     *         for that id
     */
    public Component getComponent(int componentId) {
        return components.get(componentId);
    }

    /**
     * Renders and returns the inventory
     * 
     * @return The inventory
     */
    public Inventory getInventory() {
        return new GUIHolder(this).getInventory();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Render to the given {@link GUIHolder}
     * 
     * @param holder the holder
     */
    public void render(GUIHolder holder) {
        for (int i = 0; i < rows * 9; i++) {
            ItemStack item = holder.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                holder.setItem(i, ItemUtils.setName(fill.getItem(color), " "));
            }
        }
    }

}
