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

    public final void render(GUIHolder holder) {

        for (int ix = x; ix < x + width; ix++) {

            for (int iy = y; iy < y + height; iy++) {

                holder.setItem(ix, iy, render(holder, ix, iy));

            }

        }

    }

    protected ItemStack render(GUIHolder holder, int posX, int posY) {
        return type.getItem(color);
    }

    public final boolean checkClick(Player player, int clickX, int clickY, InventoryClickEvent e) {

        if (clickX >= x && clickX < x + width && clickY >= y && clickY < y + height) {

            if (onClick(player, clickX - x, clickY - y)) {
                e.setCancelled(false);
            }
            return true;

        }
        return false;

    }

    /**
     * 
     * @param player
     * @param offX
     * @param offY
     * @return If the clicking should be allowed (not cancelled)
     */
    protected boolean onClick(Player player, int offX, int offY) {
        return false;
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
     * @return the gui
     */
    public GUI getGui() {
        return gui;
    }

    /**
     * This is only meant to be used by {@link GUI}. Don't use this unless you
     * know what you're doing.
     * 
     * @param gui the gui to set
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

}
