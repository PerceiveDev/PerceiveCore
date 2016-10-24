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

    protected DisplayType  type;
    protected DisplayColor color;

    protected GUI          gui;

    public Component(DisplayType type, DisplayColor color) {
        this.type = type;
        this.color = color;
    }

    public Component(DisplayColor color) {
        this(DisplayType.FLAT, color);
    }

    public Component() {
        this(DisplayColor.LIGHT_BLUE);
    }

    /**
     * Renders all items to the inventory represented by the {@link GUIHolder}
     * parameter.
     * 
     * @param holder the GUIHolder
     */
    public final void render(Rect pos, GUIHolder holder) {
        for (int ix = pos.getX(); ix < pos.getX() + pos.getWidth(); ix++) {
            for (int iy = pos.getY(); iy < pos.getY() + pos.getHeight(); iy++) {
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
    public final boolean checkClick(Rect pos, Player player, int clickX, int clickY, InventoryClickEvent e) {

        if (clickX >= pos.getX() && clickX < pos.getX() + pos.getWidth() && clickY >= pos.getY() && clickY < pos.getY() + pos.getHeight()) {

            ClickEvent event = new ClickEvent(player, clickX - pos.getX(), clickY - pos.getY(), e.getClick());
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
     * @return the type
     */
    public DisplayType getDisplayType() {
        return type;
    }

    /**
     * @param type the type to set
     * @return this component (useful for chaining method calls)
     */
    public Component setDisplayType(DisplayType type) {
        this.type = type;
        return this;
    }

    /**
     * @return the color
     */
    public DisplayColor getColor() {
        return color;
    }

    /**
     * @param color the color to set
     * @return this component (useful for chaining method calls)
     */
    public Component setColor(DisplayColor color) {
        this.color = color;
        return this;
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
