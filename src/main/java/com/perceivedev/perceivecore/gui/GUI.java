/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.component.Component;
import com.perceivedev.perceivecore.gui.component.Rect;
import com.perceivedev.perceivecore.util.ItemUtils;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author Rayzr
 *
 */
public class GUI {

    private Map<Component, Rect> components = new HashMap<>();
    private String               name;
    private int                  rows;

    private DisplayType          fill;
    private DisplayColor         color;

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
        return components.keySet().stream().collect(Collectors.toList());
    }

    /**
     * Adds a component and returns the id of it
     * 
     * @param component the component
     * @param pos the position and size of the component
     * @return The component's id
     */
    public int addComponent(Component component, Rect pos) {
        components.put(component, pos);
        component.setGui(this);
        return components.size() - 1;
    }

    /**
     * Adds a component and returns the id of it
     * 
     * @param component the component
     * @param x the x position of the component
     * @param y the y position of the component
     * @param width the width of the component
     * @param height the height of the component
     * @return The component's id
     */
    public int addComponent(Component component, int x, int y, int width, int height) {
        return addComponent(component, new Rect(x, y, width, height));
    }

    /**
     * Adds a component and returns the id of it
     * 
     * @param component the component
     * @param x the x position of the component
     * @param y the y position of the component
     * @return The component's id
     */
    public int addComponent(Component component, int x, int y) {
        return addComponent(component, x, y, 1, 1);
    }

    /**
     * Gets a component with the given id
     * 
     * @param componentId the id of the component
     * @return The component. This will be null if no component could be found
     *         for that id
     */
    public Component getComponent(int componentId) {
        if (componentId >= components.size()) {
            return null;
        }
        return components.keySet().toArray(new Component[0])[componentId];
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

    /**
     * @param component
     * @return
     */
    public Rect getPos(Component component) {
        return components.get(component);
    }

}
