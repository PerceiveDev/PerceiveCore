package com.perceivedev.perceivecore.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.gui.components.base.pane.Pane;

/** A Click Event */
public class ClickEvent {
    private InventoryClickEvent raw;
    private Pane                lastPane;
    private int                 offsetX, offsetY;

    /**
     * Creates a ClickEvent. This sets the raw InventoryClickEvent to cancelled
     * by default, so if you have a component which needs to allow interaction,
     * make sure to {@link #setCancelled(boolean)} to <code>false</code>!
     *
     * @param raw The raw {@link InventoryClickEvent}
     * @param lastPane The last pane it went through
     * @param offsetX The offset on the x axis
     * @param offsetY The offset on the y axis
     */
    public ClickEvent(InventoryClickEvent raw, Pane lastPane, int offsetX, int offsetY) {
        this.raw = raw;
        this.lastPane = lastPane;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        setCancelled(true);
    }

    /**
     * Initializes it with <code>offsetX = 0, offsetY = 0</code>
     *
     * @param raw The raw {@link InventoryClickEvent}
     * @param lastPane The last pane it went through
     * 
     * @see #ClickEvent(InventoryClickEvent, Pane, int, int)
     */
    public ClickEvent(InventoryClickEvent raw, Pane lastPane) {
        this(raw, lastPane, 0, 0);
    }

    /**
     * Returns the raw {@link InventoryClickEvent}
     *
     * @return The raw {@link InventoryClickEvent}
     */
    public InventoryClickEvent getRaw() {
        return raw;
    }

    /**
     * Returns the last pane it wen't through
     *
     * @return The last pane it went through
     */
    public Pane getLastPane() {
        return lastPane;
    }

    /**
     * Sets the last pane it wen't through
     *
     * @param lastPane The last pane it went through
     */
    public void setLastPane(Pane lastPane) {
        this.lastPane = lastPane;
    }

    /**
     * Returns the offset on the x axis
     *
     * @return The offset on the x axis
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Sets the offset on the x axis
     *
     * @param offsetX The offset on the x axis
     */
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Returns the offset on the y axis
     *
     * @return The offset on the y axis
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Sets the offset on the y axis
     *
     * @param offsetY The offset on the y axis
     */
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * Checks if the user clicked outside the inventory
     *
     * @return true if the click was outside the inventory
     */
    public boolean isOutsideInventory() {
        return getSlot() < 0;
    }

    /**
     * The slot number that was clicked, ready for passing to
     * {@link Inventory#getItem(int)}. Note that there may be two slots with the
     * same slot number, since a view links two different inventories.
     *
     * @return The slot number.
     */
    public int getSlot() {
        return raw.getSlot();
    }

    /**
     * The type of the click. Useful for checking shift-clicking, right
     * clicking, middle clicking, etc.
     * 
     * @return The click type.
     * 
     * @see ClickType
     */
    public ClickType getClickType() {
        return raw.getClick();
    }

    /**
     * The player that clicked the slot
     *
     * @return The player who clicked
     */
    public Player getPlayer() {
        return (Player) getRaw().getWhoClicked();
    }

    /**
     * Gets whether or not this event is cancelled. This is based off of the
     * Result value returned by {@link InventoryClickEvent#getResult()}.
     * Result.ALLOW and Result.DEFAULT will result in a returned value of false,
     * but Result.DENY will result in a returned value of true.
     * <p>
     * <b>Note: By default ClickEvents are <i>cancelled</i>, so if you want to
     * allow interaction make sure to {@link #setCancelled(boolean)} to
     * <code>false</code>!</b>
     * <p>
     * {@inheritDoc}
     *
     * @return whether the event is cancelled
     */
    public boolean isCancelled() {
        return raw.isCancelled();
    }

    /**
     * Proxy method to {@link InventoryClickEvent#setResult(Event.Result)} for
     * the Cancellable interface.
     * {@link InventoryClickEvent#setResult(Event.Result)} is preferred, as it
     * allows you to specify the Result beyond Result.DENY and Result.ALLOW.
     * <p>
     * <b>Note: By default ClickEvents are <i>cancelled</i>, so if you want to
     * allow interaction make sure to {@link #setCancelled(boolean)} to
     * <code>false</code>!</b>
     * <p>
     * {@inheritDoc}
     *
     * @param toCancel result becomes DENY if true, ALLOW if false
     */
    public void setCancelled(boolean toCancel) {
        raw.setCancelled(toCancel);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "ClickEvent{" +
                  "offsetX=" + offsetX +
                  ", offsetY=" + offsetY +
                  ", slot=" + getSlot() +
                  ", cancelled=" + isCancelled()
                  + '}';
        // @formatter:on
    }
}
