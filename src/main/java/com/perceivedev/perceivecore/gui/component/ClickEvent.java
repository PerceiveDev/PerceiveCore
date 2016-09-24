/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * 
 * ClickEvent is a simple data class for passing along all the needed
 * information of a click event to components, without having a massive number
 * of parameters in {@link Component#onClick(ClickEvent)}
 * 
 * @author Rayzr
 *
 */
public class ClickEvent {

    private Player    player;
    private int       offX;
    private int       offY;
    private ClickType click;
    private boolean   cancelled = true;

    /**
     * Initializes a new ClickEvent data object
     * 
     * @param player the player
     * @param offX the offsetX
     * @param offY the offsetY
     * @param type the click type
     */
    public ClickEvent(Player player, int offX, int offY, ClickType click) {
        this.player = player;
        this.offX = offX;
        this.offY = offY;
        this.click = click;
    }

    /**
     * @return the player who clicked
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the x position relative to the button origin
     */
    public int getOffX() {
        return offX;
    }

    /**
     * @return the y position relative to the button origin
     */
    public int getOffY() {
        return offY;
    }

    /**
     * @return the type of click
     * @see ClickType
     */
    public ClickType getClick() {
        return click;
    }

    /**
     * 
     * {@code cancelled} defaults to true, but you can set it to false to allow
     * interaction.
     * 
     * @return Whether or not the even is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 
     * {@code cancelled} defaults to true, but you can set it to false to allow
     * interaction.
     * 
     * @param whether or not the event should be cancelled
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}