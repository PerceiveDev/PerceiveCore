package com.perceivedev.perceivecore.guisystem.implementation.panes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.perceivedev.perceivecore.guisystem.component.Component;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

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
     * @param size The size of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does not equal size
     */
    protected FlowPane(List<Component> components, Dimension size, FlowInventoryMap inventoryMap) {
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
    public FlowPane(List<Component> components, Dimension size) {
        this(components, size, new FlowInventoryMap(size));
    }

    /**
     * An empty Pane
     *
     * @param size The size of this pane
     */
    public FlowPane(Dimension size) {
        this(Collections.emptyList(), size);
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
        getChildrenModifiable().add(component);
        return true;
    }

    @Override
    public void removeComponent(Component component) {
        Objects.requireNonNull(component);
        
        if (!containsComponent(component)) {
            return;
        }
        getChildrenModifiable().removeIf(component1 -> component1.equals(component));
        getInventoryMap().removeComponent(component);
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

        // TODO: 03.10.2016 Remove this visualisation method 
/*        
        private void printMaybe(Dimension dimension) {
            int[] starting = getNextComponentStartingLocation(dimension);
            if (starting == null) {
                System.out.println(ANSI_PURPLE + " NULL " + ANSI_RESET);
                return;
            }

            Status[][] array = new Status[lines.length][];
            for (int y = 0; y < lines.length; y++) {
                array[y] = new Status[lines[y].length];

                for (int x = 0; x < lines[0].length; x++) {
                    if (isInRange(x, starting[0], starting[0] + dimension.getWidth())
                              && isInRange(y, starting[1], starting[1] + dimension.getHeight())) {
                        array[y][x] = Status.MAYBE;

                        continue;
                    }
                    array[y][x] = lines[y][x] ? Status.TAKEN : Status.FREE;
                }
            }

            printLines(array);
        }

        private boolean isInRange(int number, int min, int max) {
            return number >= min && number < max;
        }
*/
    }
}
