package com.perceivedev.perceivecore.guireal.components.implementation.pane;

import java.util.List;
import java.util.Objects;

import com.perceivedev.perceivecore.guireal.components.base.component.Component;
import com.perceivedev.perceivecore.guireal.components.base.pane.AbstractPane;

/**
 * A simple anchor pane
 */
public class AnchorPane extends AbstractPane {

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param components The components to add
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does not equal size
     */
    protected AnchorPane(List<Component> components, int width, int height, InventoryMap inventoryMap) {
        super(components, width, height, inventoryMap);
    }

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param components The components to add
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public AnchorPane(List<Component> components, int width, int height) {
        super(components, width, height);
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public AnchorPane(int width, int height) {
        super(width, height);
    }

    /**
     * <i>Does nothing. Use the {@link #addComponent(Component, int, int)} method.</i>
     *
     * @param component The component to add. You can't add the same component twice.
     *
     * @return False. Every time.
     *
     * @see #addComponent(Component, int, int)
     * @deprecated Use {@link #addComponent(Component, int, int)}
     */
    @Override
    public boolean addComponent(Component component) {
        return false;
    }

    /**
     * Adds a component at the desired location.
     *
     * @param component The component to add. You can't add the same component twice.
     * @param x The x coordinate of the upper left corner
     * @param y The y coordinate of the upper left corner
     *
     * @return True if the component was added
     *
     * @throws NullPointerException if component is null
     */
    public boolean addComponent(Component component, int x, int y) {
        Objects.requireNonNull(component, "component can not be null");
        
        if (containsComponent(component)) {
            return false;
        }

        if (!getInventoryMap().hasEnoughSpace(x, y, component.getSize())) {
            return false;
        }

        if (getInventoryMap().addComponent(x, y, component)) {
            components.add(component);
            updateComponentHierarchy(component);
            return true;
        }

        return false;
    }

    @Override
    public AnchorPane deepClone() {
        return (AnchorPane) super.deepClone();
    }
}
