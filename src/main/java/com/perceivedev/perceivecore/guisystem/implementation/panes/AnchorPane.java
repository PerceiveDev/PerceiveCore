package com.perceivedev.perceivecore.guisystem.implementation.panes;

import java.util.List;
import java.util.Objects;

import com.perceivedev.perceivecore.guireal.util.Dimension;
import com.perceivedev.perceivecore.guisystem.component.Component;

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
     * @param size The size of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does not equal size
     */
    protected AnchorPane(List<Component> components, Dimension size, InventoryMap inventoryMap) {
        super(components, size, inventoryMap);
    }

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param components The components to add
     * @param size The size of this pane
     */
    public AnchorPane(List<Component> components, Dimension size) {
        super(components, size);
    }

    /**
     * An empty Pane
     *
     * @param size The size of this pane
     */
    public AnchorPane(Dimension size) {
        super(size);
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
        Objects.requireNonNull(component);

        if (!getInventoryMap().hasEnoughSpace(x, y, component.getSize())) {
            return false;
        }

        if (containsComponent(component)) {
            return false;
        }

        boolean result = getInventoryMap().addComponent(x, y, component);

        if (result) {
            getChildrenModifiable().add(component);
        }

        return result;
    }

    @Override
    public AnchorPane deepClone() {
        return (AnchorPane) super.deepClone();
    }
}
