package com.perceivedev.perceivecore.guireal.components.implementation;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.guireal.ClickEvent;
import com.perceivedev.perceivecore.guireal.util.Dimension;

/**
 * A simple button
 */
public class Button extends Label {

    private Consumer<ClickEvent> clickHandler;

    /**
     * Constructs a button
     *
     * @param itemStack The ItemStack to display
     * @param runnable The Runnable to run on click
     * @param size The size of the button
     *
     * @throws NullPointerException if any parameter is null
     */
    public Button(ItemStack itemStack, Consumer<ClickEvent> clickHandler, Dimension size) {
        super(itemStack, size);

        this.clickHandler = clickHandler;
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
        this(itemStack, e -> {
        }, size);
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
     * @param clickHandler The click handler of the button
     */
    public void setAction(Consumer<ClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickHandler.accept(clickEvent);
        // TODO: @i_al_istannen: They can cancel this from within the
        // clickHandler if they want. We shouldn't force it cancelled :P
        // clickEvent.setCancelled(true);
    }

    @Override
    public Dimension getSize() {
        // Dimension is immutable
        return size;
    }

    /**
     * The returned Button will be logically equal to this, but equals will
     * return false. This is because of a hidden ID assigned to distinguish
     * instances.
     *
     * @return A clone of this button
     */
    @Override
    public Button deepClone() {
        try {
            return new Button(itemStack.clone(), clickHandler, size.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
