package com.perceivedev.perceivecore.guireal.components.implementation.pane;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.perceivedev.perceivecore.guireal.components.base.component.Component;
import com.perceivedev.perceivecore.guireal.components.base.pane.AbstractPane;
import com.perceivedev.perceivecore.guireal.util.Dimension;

/**
 * A pane that just throws the children in as they fit.
 */
@SuppressWarnings("WeakerAccess")
public class FlowPane extends AbstractPane {

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
    protected FlowPane(List<Component> components, int width, int height, FlowInventoryMap inventoryMap) {
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
    public FlowPane(List<Component> components, int width, int height) {
        this(components, width, height, new FlowInventoryMap(new Dimension(width, height)));
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public FlowPane(int width, int height) {
        this(Collections.emptyList(), width, height);
    }

    /**
     * Adds a component
     *
     * @param component The component to add. You can't add the same component twice.
     *
     * @return True if the component was added
     */
    @Override
    public boolean addComponent(Component component) {
        Objects.requireNonNull(component);

        int[] location = ((FlowInventoryMap) getInventoryMap()).getNextComponentStartingLocation(component.getSize());
        if (location == null) {
            return false;
        }

        if (containsComponent(component)) {
            return false;
        }

        boolean worked = getInventoryMap().addComponent(location[0], location[1], component);
        if (!worked) {
            return false;
        }
        components.add(component);
        return true;
    }

    @Override
    public boolean removeComponent(Component component) {
        Objects.requireNonNull(component, "component can not be null");

        if (!containsComponent(component)) {
            return true;
        }
        components.removeIf(component1 -> component1.equals(component));
        getInventoryMap().removeComponent(component);

        return true;
    }

    @Override
    public FlowPane deepClone() {
        return (FlowPane) super.deepClone();
    }

    /**
     * Maps components to their coordinates
     */
    protected static class FlowInventoryMap extends InventoryMap {

        protected FlowInventoryMap(Dimension dimension) {
            super(dimension);
        }

        /**
         * Computes the starting location for the component
         *
         * @param dimension The Dimension of the component
         *
         * @return The starting location for the component. <code>null</code> if none. 0 == x, 1 == y
         */
        protected int[] getNextComponentStartingLocation(Dimension dimension) {
            for (int y = 0; y < lines.length; y++) {
                for (int x = 0; x < lines[0].length; x++) {
                    if (lines[y][x]) {
                        continue;
                    }

                    if (hasEnoughSpace(x, y, dimension)) {
                        return new int[] { x, y };
                    }
                }
            }
            return null;
        }
    }
}
