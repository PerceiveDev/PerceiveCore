package com.perceivedev.bukkitpluginutilities.gui.components.panes;

import java.util.Objects;
import java.util.Optional;

import com.perceivedev.bukkitpluginutilities.gui.base.AbstractPane;
import com.perceivedev.bukkitpluginutilities.gui.base.Component;
import com.perceivedev.bukkitpluginutilities.gui.base.FixedPositionPane;

/**
 * A simple anchor pane
 */
public class AnchorPane extends AbstractPane implements FixedPositionPane {

    /**
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does
     *                                  not equal size
     */
    @SuppressWarnings("unused")
    protected AnchorPane(int width, int height, InventoryMap inventoryMap) {
        super(width, height, inventoryMap);
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
     * Adds a component at the desired location.
     *
     * @param component The component to add. You can't add the same component
     * twice.
     * @param x The x coordinate of the upper left corner
     * @param y The y coordinate of the upper left corner
     *
     * @return True if the component was added
     *
     * @throws NullPointerException if component is null
     */
    @Override
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
    public boolean removeComponent(int x, int y) {
        Optional<Component> component = getComponentAtPoint(x, y);

        return component.isPresent() && removeComponent(component.get());
    }

    @Override
    public AnchorPane deepClone() {
        return (AnchorPane) super.deepClone();
    }
}
