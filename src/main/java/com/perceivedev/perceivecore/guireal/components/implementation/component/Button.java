package com.perceivedev.perceivecore.guireal.components.implementation.component;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.guireal.ClickEvent;
import com.perceivedev.perceivecore.guireal.components.base.component.AbstractComponent;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A simple button
 */
public class Button extends AbstractComponent {

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
        super(size);
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(runnable);

        this.itemStack = itemStack.clone();
        this.runnable = runnable;
        this.size = size;
    }

    /**
     * Constructs a button
     *
     * @param itemStack The ItemStack to display
     * @param size The size of the button
     *
     * @throws NullPointerException if any parameter is null
     * @see #Button(ItemStack, Runnable, Dimension)
     */
    public Button(ItemStack itemStack, Dimension size) {
        this(itemStack,
                  () -> {
                  },
                  size);
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

    /**
     * Sets the Button action
     *
     * @param action The Action of the button
     */
    public void setAction(Runnable action) {
        this.runnable = action;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
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
        iterateOver2DRange(0, getSize().getWidth(), 0, getSize().getHeight(), (x, y) -> {
            int slot = gridToSlot(x + xOffset, y + yOffset);
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

    /**
     * The returned Button will be logically equal to this, but equals will return false.
     * This is because of a hidden ID assigned, to distinguish instances.
     *
     * @return A clone of this button
     */
    @Override
    public Button deepClone() {
        try {
            return new Button(itemStack.clone(), runnable, size.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
