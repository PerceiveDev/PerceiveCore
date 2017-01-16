package com.perceivedev.bukkitpluginutilities.gui.components.panes;

import java.util.Objects;

import com.perceivedev.bukkitpluginutilities.gui.base.AbstractPane;
import com.perceivedev.bukkitpluginutilities.gui.base.Component;
import com.perceivedev.bukkitpluginutilities.gui.base.FreeformPane;
import com.perceivedev.bukkitpluginutilities.gui.util.Dimension;


/**
 * A pane that just throws the children in as they fit.
 */
public class FlowPane extends AbstractPane implements FreeformPane {

    /**
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does
     *                                  not equal size
     */
    @SuppressWarnings("WeakerAccess")
    protected FlowPane(int width, int height, FlowInventoryMap inventoryMap) {
        super(width, height, inventoryMap);
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    @SuppressWarnings("unused")
    public FlowPane(int width, int height) {
        this(width, height, new FlowInventoryMap(new Dimension(width, height)));
    }

    /**
     * Adds a component
     *
     * @param component The component to add. You can't add the same component
     * twice.
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
        updateComponentHierarchy(component);
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

        @SuppressWarnings("WeakerAccess")
        public FlowInventoryMap(Dimension dimension) {
            super(dimension);
        }

        /**
         * Computes the starting location for the component
         *
         * @param dimension The Dimension of the component
         *
         * @return The starting location for the component. <code>null</code> if
         * none. 0 == x, 1 == y
         */
        @SuppressWarnings("WeakerAccess")
        public int[] getNextComponentStartingLocation(Dimension dimension) {
            for (int y = 0; y < lines.length; y++) {
                for (int x = 0; x < lines[0].length; x++) {
                    if (lines[y][x]) {
                        continue;
                    }

                    if (hasEnoughSpace(x, y, dimension)) {
                        return new int[]{x, y};
                    }
                }
            }
            return null;
        }
    }
}
