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
    private String	    name;
    private int		    rows;

    private DisplayType	    fill;
    private DisplayColor    color;

    public GUI(String name, int rows, DisplayType fill, DisplayColor color) {

	this.name = TextUtils.colorize(name);

	rows = rows < 1 ? 1 : rows > 6 ? 6 : rows;
	this.rows = rows;

	this.fill = fill;
	this.color = color;

    }

    public GUI(String name, int rows, DisplayColor color) {
	this(name, rows, DisplayType.FLAT, color);
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

    public void addComponent(Component c) {
	components.add(c);
    }

    /**
     * Renders and returns the inventory
     * 
     * @return
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

    public void render(GUIHolder holder) {
	for (int i = 0; i < rows * 9; i++) {
	    ItemStack item = holder.getInventory().getItem(i);
	    if (item == null || item.getType() == Material.AIR) {
		holder.setItem(i, ItemUtils.setName(fill.getItem(color), " "));
	    }

	}
    }

}
