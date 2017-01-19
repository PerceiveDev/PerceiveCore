package com.perceivedev.perceivecore.gui.components;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.util.Dimension;


/**
 * A simple button
 */
public class Button extends Label {

    private Consumer<ClickEvent> clickHandler;

    /**
     * Constructs a button
     *
     * @param itemStack The ItemStack to display
     * @param clickHandler The click handler to run on click
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
     * @see #Button(ItemStack, Consumer, Dimension)
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public Button(ItemStack itemStack, Dimension size) {
        this(itemStack, e -> {
        }, size);
    }

    /**
     * Constructs a button of size {@link Dimension#ONE}
     *
     * @param itemStack The ItemStack to use
     */
    @SuppressWarnings("unused")
    public Button(ItemStack itemStack) {
        this(itemStack, Dimension.ONE);
    }

    /**
     * Sets the Button action
     *
     * @param clickHandler The click handler of the button
     */
    @SuppressWarnings("unused")
    public void setAction(Consumer<ClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }

    /**
     * Reacts to a click event
     *
     * @param clickEvent The {@link ClickEvent}
     */
    @Override
    public void onClick(ClickEvent clickEvent) {
        clickHandler.accept(clickEvent);
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
        return new Button(getItemStack(), clickHandler, getSize());
    }
}
