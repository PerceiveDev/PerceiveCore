package com.perceivedev.bukkitpluginutilities.gui.anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * An anvil click event
 */
public class AnvilClickEvent {

    private AnvilSlot slot;
    private Player player;
    private InventoryView view;
    private ItemStack involvedItem;
    private boolean cancelled;

    /**
     * @param slot The slot of the click
     * @param player The player who clicked
     * @param view The InventoryView
     * @param involvedItem The involved item
     */
    AnvilClickEvent(AnvilSlot slot, Player player, InventoryView view, ItemStack involvedItem) {
        this.slot = slot;
        this.player = player;
        this.view = view;
        this.involvedItem = involvedItem;
    }

    /**
     * @return The {@link AnvilSlot}
     */
    @SuppressWarnings("unused")
    public AnvilSlot getSlot() {
        return slot;
    }

    /**
     * @return True if it is cancelled
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @param cancelled True if it should be cancelled
     */
    @SuppressWarnings("unused")
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return The {@link InventoryView}
     */
    @SuppressWarnings("unused")
    public InventoryView getView() {
        return view;
    }

    /**
     * @return The involved item or null if none
     */
    @SuppressWarnings("WeakerAccess")
    public ItemStack getInvolvedItem() {
        return involvedItem;
    }

    /**
     * @return The {@link Player}
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "AnvilClickEvent{" +
                "slot=" + slot +
                ", involvedItem=" + involvedItem +
                ", cancelled=" + cancelled +
                '}';
    }

    /**
     * An anvil slot
     */
    public enum AnvilSlot {
        /**
         * The left input slot
         */
        INPUT_LEFT(0),
        /**
         * The right input slot
         */
        INPUT_RIGHT(1),
        /**
         * The output slot
         */
        OUTPUT(2),
        /**
         * An unknown slot
         */
        UNKNOWN(-1);

        private int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        /**
         * @return The Minecraft slot number
         */
        public int getSlot() {
            return slot;
        }

        /**
         * @param slot The clicked slot
         *
         * @return The {@link AnvilSlot} or {@link #UNKNOWN} if not known
         */
        public static AnvilSlot getFromSlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return UNKNOWN;
        }
    }
}
