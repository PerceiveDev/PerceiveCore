/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.DisplayColor;
import com.perceivedev.perceivecore.gui.DisplayType;
import com.perceivedev.perceivecore.gui.GUI;
import com.perceivedev.perceivecore.gui.GUIHolder;

/**
 * @author Rayzr
 *
 */
public class Component {

    protected int          x;
    protected int          y;
    protected int          width;
    protected int          height;

    protected DisplayType  type;
    protected DisplayColor color;

    protected GUI          gui;

    public Component(int x, int y, int width, int height, DisplayType type, DisplayColor color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.color = color;
    }

    public Component(int x, int y, int width, int height, DisplayColor color) {
        this(x, y, width, height, DisplayType.FLAT, color);
    }

    public Component(int x, int y, int width, int height) {
        this(x, y, width, height, DisplayColor.LIGHT_BLUE);
    }

    public Component() {
        this(0, 0, 1, 1);
    }

    /**
     * Renders all items to the inventory represented by the {@link GUIHolder}
     * parameter.
     * 
     * @param holder the GUIHolder
     */
    public final void render(GUIHolder holder) {

        for (int ix = x; ix < x + width; ix++) {

            for (int iy = y; iy < y + height; iy++) {

                holder.setItem(ix, iy, render(holder, ix, iy));

            }

        }

    }

    /**
     * "Renders" the ItemStack for the given position.
     * 
     * @param holder the {@link GUIHolder}
     * @param posX the x position
     * @param posY the y position
     * @return The ItemStack
     */
    protected ItemStack render(GUIHolder holder, int posX, int posY) {
        return type.getItem(color);
    }

    /**
     * Checks if a click is within the boundaries of this component.
     * 
     * @param player the player who clicked
     * @param clickX the x position of the click
     * @param clickY the y position of the click
     * @param e the {@link InventoryClickEvent} itself
     * 
     * @return Whether or not this click was actually on the component.
     */
    public final boolean checkClick(Player player, int clickX, int clickY, InventoryClickEvent e) {

        if (clickX >= x && clickX < x + width && clickY >= y && clickY < y + height) {

            ClickEvent event = new ClickEvent(player, clickX - x, clickY - y, e.getClick());
            onClick(event);
            e.setCancelled(event.isCancelled());

            return true;

        }
        return false;

    }

    /**
     * This is called if
     * {@link #checkClick(Player, int, int, InventoryClickEvent)} determines
     * that the component was clicked.
     * 
     * @param e the {@link ClickEvent}
     */
    protected void onClick(ClickEvent e) {

    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the position of the component
     * 
     * @param x the x position
     * @param y the y position
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the size of the component
     * 
     * @param w the width
     * @param h the height
     */
    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the type
     */
    public DisplayType getDisplayType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setDisplayType(DisplayType type) {
        this.type = type;
    }

    /**
     * @return the color
     */
    public DisplayColor getDisplayColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(DisplayColor color) {
        this.color = color;
    }

    /**
     * @param gui the gui to set
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * @return the gui
     */
    public GUI getGui() {
        return gui;
    }

}
