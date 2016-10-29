package com.perceivedev.perceivecore.guireal.components.implementation.component;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.guireal.ClickEvent;
import com.perceivedev.perceivecore.guireal.components.base.component.AbstractComponent;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A Label. Like a button, but does nothing on click
 */
public class Label extends AbstractComponent {

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
        super(size);
        Objects.requireNonNull(itemStack);

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
    public void onClick(ClickEvent clickEvent) {
        System.out.println("Label.onClick()");
        System.out.println(clickEvent);
        clickEvent.setCancelled(true);
    }

    @Override
    public void render(Inventory inventory, Player player, int xOffset, int yOffset) {
        System.out.println(xOffset + " " + yOffset);
        iterateOver2DRange(0, getSize().getWidth(), 0, getSize().getHeight(), (x, y) -> {
            int slot = gridToSlot(x + xOffset, y + yOffset);
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
}
