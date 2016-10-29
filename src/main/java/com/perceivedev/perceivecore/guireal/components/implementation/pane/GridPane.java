package com.perceivedev.perceivecore.guireal.components.implementation.pane;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.perceivedev.perceivecore.guireal.components.base.component.Component;
import com.perceivedev.perceivecore.guireal.components.base.pane.AbstractPane;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A Grid pane
 */
public class GridPane extends AbstractPane {

    private boolean[][] slots;
    private int         gridWidth, gridHeight;

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     * @param columns The columns. It will leave spaces empty, if this isn't chosen in a fashion compatible with the inventory.
     * @param rows The rows. It will leave spaces empty, if this isn't chosen in a fashion compatible with the inventory.
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does not equal size
     */
    public GridPane(int width, int height, InventoryMap inventoryMap, int columns, int rows) {
        super(Collections.emptyList(), width, height, inventoryMap);

        gridWidth = (int) Math.floor(getSize().getWidth() / (double) columns);
        gridHeight = (int) Math.floor(getSize().getHeight() / (double) rows);

        // initialize slots
        slots = new boolean[rows][];

        for (int y = 0; y < rows; y++) {
            slots[y] = new boolean[columns];

            for (int x = 0; x < columns; x++) {
                slots[y][x] = false;
            }
        }
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param columns The columns
     * @param rows The rows
     */
    public GridPane(int width, int height, int columns, int rows) {
        this(width, height, new InventoryMap(new Dimension(width, height)), columns, rows);
    }

    /**
     * Creates a pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if InventoryMap{@link #getSize()} does not equal size
     */
    public GridPane(int width, int height, InventoryMap inventoryMap) {
        this(width, height, inventoryMap, 2, 2);
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public GridPane(int width, int height) {
        this(width, height, new InventoryMap(new Dimension(width, height)));
    }

    /**
     * Returns the width of a grid cell
     *
     * @return The width of a grid cell
     */
    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * Returns the height of a grid cell
     *
     * @return The height of a grid cell
     */
    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * Returns the size of a grid
     *
     * @return The Size of a grid
     */
    public Dimension getGridSize() {
        return new Dimension(gridWidth, gridHeight);
    }

    /**
     * Finds the next free slot for a Component
     *
     * @param component The component to fit
     *
     * @return The free slot (0 == x, 1 == y) or null if none (or the component doesn't fit in the gridSize)
     */
    private int[] getNextFreeSlot(Component component) {
        if (!component.getSize().fitsInside(getGridSize())) {
            return null;
        }

        for (int y = 0; y < slots.length; y++) {
            for (int x = 0; x < slots[y].length; x++) {
                if (!slots[y][x]) {
                    return new int[] { x, y };
                }
            }
        }
        return null;
    }

    /**
     * Adds a component
     * <p>
     * Uses the next free grid
     *
     * @param component The component to add.You can't add the same component twice.
     *
     * @return True if the component was added
     *
     * @throws NullPointerException if component is null
     */
    @Override
    public boolean addComponent(Component component) {
        Objects.requireNonNull(component);

        int[] freeSlot = getNextFreeSlot(component);
        return freeSlot != null
                  && addComponent(component, freeSlot[0], freeSlot[1]);
    }

    /**
     * Tries to add the given component in the given slot
     *
     * @param component The component to add. You can't add the same component twice.
     * @param slotX The x coordinate of the slot
     * @param slotY The y coordinate of the slot
     *
     * @return True if it was added.
     *
     * @throws NullPointerException if component is null
     */
    public boolean addComponent(Component component, int slotX, int slotY) {
        Objects.requireNonNull(component);

        if (!component.getSize().fitsInside(getGridSize())) {
            return false;
        }

        if (containsComponent(component)) {
            return false;
        }

        if (slotX < 0 || slotY < 0) {
            throw new IllegalArgumentException("slotX and slotY must be > 0. Given: '" + slotX + "' and '" + slotY + "'");
        }
        if (slotX >= getSize().getWidth() / gridWidth
                  || slotY >= getSize().getHeight() / gridHeight) {
            throw new IllegalArgumentException("slotX and slotY must be smaller than the amount of columns/rows. "
                      + "Given: '" + slotX + "' and '" + slotY + "'. "
                      + "Columns: " + (getSize().getWidth() / gridWidth)
                      + "Rows: " + (slotY >= getSize().getHeight() / gridHeight));
        }

        // already occupied
        if (slots[slotY][slotX]) {
            return false;
        }

        boolean result = getInventoryMap().addComponent(slotX * gridWidth, slotY * gridHeight, component);

        if (result) {
            slots[slotY][slotX] = true;
            components.add(component);
        }

        return result;
    }

    @Override
    public boolean removeComponent(Component component) {
        Objects.requireNonNull(component);

        if (!containsComponent(component)) {
            return true;
        }
        Optional<Interval> interval = getInventoryMap().getComponentMap().entrySet()
                  .stream()
                  .filter(entry -> component.equals(entry.getValue()))
                  .map(Entry::getKey)
                  .findFirst();

        // Is valid, because if it couldn't find any, containsComponent would return false
        //noinspection OptionalGetWithoutIsPresent
        Interval bounds = interval.get();

        int slotX = bounds.getMinX() / gridWidth;
        int slotY = bounds.getMinY() / gridHeight;

        // clear the slot
        slots[slotY][slotX] = false;

        super.removeComponent(component);

        return true;
    }

    /**
     * Removes the component in the given slot
     *
     * @param x The x coordinate of the slot
     * @param y The y coordinate of the slot
     */
    public boolean removeComponent(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }
        if (x >= getSize().getWidth() / gridWidth
                  || y >= getSize().getHeight() / gridHeight) {
            return false;
        }
        Optional<Component> component = getInventoryMap().getComponent(x * gridWidth, y * gridHeight);

        if (!component.isPresent()) {
            return true;
        }

        removeComponent(component.get());

        return true;
    }

    @Override
    public GridPane deepClone() {
        GridPane clone = (GridPane) super.deepClone();
        clone.slots = slots.clone();
        for (int i = 0; i < slots.length; i++) {
            clone.slots[i] = slots[i].clone();
        }
        return clone;
    }
}
