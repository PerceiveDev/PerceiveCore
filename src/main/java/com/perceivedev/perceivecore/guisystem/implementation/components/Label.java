package com.perceivedev.perceivecore.guisystem.implementation.components;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.guisystem.component.Component;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A Label. Like a button, but does nothing on click
 */
public class Label implements Component {

    // make instances distinct, even if they have the same size and item
    private static int counter;

    private final int ID = ++counter;

    private ItemStack itemStack;
    private Dimension size;

    /**
     * Constructs a Label
     *
     * @param itemStack The ItemStack to use
     * @param size The size of this component
     *
     * @throws NullPointerException if any parameter is null
     */
    public Label(ItemStack itemStack, Dimension size) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(size);

        this.itemStack = itemStack.clone();
        this.size = size;
    }

    /**
     * Sets the item
     *
     * @param itemStack The new itemstack
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        clickEvent.setCancelled(true);
    }

    @Override
    public Dimension getSize() {
        // Dimension is immutable
        return size;
    }

    @Override
    public void render(Inventory inventory, Player player, int xOffset, int yOffset) {
        iterateOverRange(0, getSize().getWidth(), 0, getSize().getHeight(), (x, y) -> {
            int slot = gridToSlot(inventory.getSize(), x + xOffset, y + yOffset);
            if (slot < 0 || slot >= inventory.getSize()) {
                // can't happen *normally*
                System.err.println("Label: An item was placed outside the inventory size. "
                          + "Size: "
                          + inventory.getSize()
                          + " Slot: "
                          + slot);
            } else {
                inventory.setItem(slot, itemStack);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Label))
            return false;
        Label label = (Label) o;
        return ID == label.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
