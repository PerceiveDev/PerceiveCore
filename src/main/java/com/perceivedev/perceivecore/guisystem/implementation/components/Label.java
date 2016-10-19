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

    /**
     * Returns the {@link ItemStack} of this button
     *
     * @return The {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return itemStack.clone();
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

    /**
     * The returned Label will be logically equal to this, but equals will return false.
     * This is because of a hidden ID assigned, to distinguish instances.
     *
     * @return A clone of this label
     */
    @Override
    public Label deepClone() {
        try {
            return new Label(itemStack.clone(), size.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This WILL NOT TEST FOR A LOGICAL EQUALS.
     * It compares a hidden inner state and is used to distinguish instances of this class.
     */
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
