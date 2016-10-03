package com.perceivedev.perceivecore.guisystem.implementation.components;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.guisystem.component.Component;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A simple button
 */
public class Button implements Component {

    // make instances distinct, even if they have the same size and item
    private static int counter;

    private final int ID = ++counter;

    private ItemStack itemStack;
    private Runnable  runnable;
    private Dimension size;

    /**
     * Constructs a button
     *
     * @param itemStack The ItemStack to display
     * @param runnable The Runnable to run on click
     * @param size The size of the button
     *
     * @throws NullPointerException if any parameter is null
     */
    public Button(ItemStack itemStack, Runnable runnable, Dimension size) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(runnable);
        Objects.requireNonNull(size);

        this.itemStack = itemStack.clone();
        this.runnable = runnable;
        this.size = size;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        runnable.run();
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
                System.err.println("Button: An item was placed outside the inventory size. "
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
        if (!(o instanceof Button))
            return false;
        Button button = (Button) o;
        return ID == button.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
