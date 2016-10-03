package com.perceivedev.perceivecore.guisystem.implementation.panes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guisystem.component.Component;
import com.perceivedev.perceivecore.guisystem.component.Pane;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A Skeleton implementation for the {@link Pane} class
 */
public abstract class AbstractPane implements Pane {

    private List<Component> components;
    private Dimension       size;
    private InventoryMap    inventoryMap;

    /**
     * The offset of this pane in the inventory. Needed to fetch the right component in onClick. 0 until this pane is rendered.
     * <p>
     * If a child overwrites {@link #render(Inventory, Player, int, int)}, without calling super, it must set these himself.
     * <p>
     * <br>
     * The x offset
     */
    protected int renderedXOffset;
    /**
     * The offset of this pane in the inventory. Needed to fetch the right component in onClick. 0 until this pane is rendered.
     * <p>
     * If a child overwrites {@link #render(Inventory, Player, int, int)}, without calling super, it must set these himself.
     * <p>
     * <br>
     * The y offset
     */
    protected int renderedYOffset;

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
    protected AbstractPane(List<Component> components, Dimension size, InventoryMap inventoryMap) {
        Objects.requireNonNull(components);
        Objects.requireNonNull(size);
        Objects.requireNonNull(inventoryMap);

        if (!inventoryMap.getSize().equals(size)) {
            throw new IllegalArgumentException("InventoryMap has wrong size. Expected " + size + " got " + inventoryMap.getSize());
        }

        this.components = new ArrayList<>();
        this.size = size;
        this.inventoryMap = inventoryMap;

        components.forEach(this::addComponent);
    }

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param components The components to add
     * @param size The size of this pane
     */
    public AbstractPane(List<Component> components, Dimension size) {
        this(components, size, new InventoryMap(size));
    }

    /**
     * An empty Pane
     *
     * @param size The size of this pane
     */
    public AbstractPane(Dimension size) {
        this(Collections.emptyList(), size);
    }

    @Override
    public boolean containsComponent(Component component) {
        return components.contains(component);
    }

    @Override
    public Collection<Component> getChildrenUnmodifiable() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public Dimension getSize() {
        // Dimension is immutable
        return size;
    }

    /**
     * Adds a component. You can't add the same pane twice.
     * <p>
     * <b><i>Must update the {@link #getInventoryMap()} itself</i></b>
     *
     * @param component The component to add. You can't add the same component twice.
     *
     * @return True if the component was added
     */
    @Override
    public abstract boolean addComponent(Component component);

    @Override
    public void removeComponent(Component component) {
        Objects.requireNonNull(component);
        
        if (!containsComponent(component)) {
            return;
        }

        components.remove(component);
        getInventoryMap().removeComponent(component);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        // clicked outside the inventory. Constant is '-111' currently.
        if (slot < 0) {
            event.setCancelled(true);
            return;
        }

        int invSize = event.getInventory().getSize();
        int x = slotToGrid(invSize, slot)[0] - renderedXOffset;
        int y = slotToGrid(invSize, slot)[1] - renderedYOffset;
        Optional<Component> component = inventoryMap.getComponent(x, y);
        if (component.isPresent()) {
            component.get().onClick(event);
        }
    }

    @Override
    public void render(Inventory inventory, Player player, int x, int y) {
        renderedXOffset = x;
        renderedYOffset = y;
        for (Entry<Interval, Component> entry : getInventoryMap().getComponentMap().entrySet()) {
            if (fitsInside(inventory, renderedXOffset, renderedYOffset, entry.getKey())) {
                // render the components
                entry.getValue().render(inventory, player, x + entry.getKey().getMinX(), y + entry.getKey().getMinY());
            } else {
                System.err.println("A component couldn't be rendered. Check your bounds and offsets!");
            }
        }
    }

    private boolean fitsInside(Inventory inventory, int xOffset, int yOffset, Interval interval) {
        int inventoryWidth = inventory.getSize() % 9 == 0 ? 9 : inventory.getSize();
        int inventoryHeight = inventory.getSize() / inventoryWidth;

        // if the item is out of x bounds (>= and the > as the max is exclusive)
        if ((interval.getMinX() + xOffset) >= inventoryWidth
                  || (interval.getMaxX() + xOffset) > inventoryWidth) {
            return false;
        }

        // out of y bounds (>= and the > as the max is exclusive)
        return !(
                  ((interval.getMinY() + yOffset) >= inventoryHeight)
                            || ((interval.getMaxY() + yOffset) > inventoryHeight)
        );
    }

    /**
     * Returns all children, but is modifiable
     *
     * @return The children. Modifiable
     */
    protected List<Component> getChildrenModifiable() {
        return components;
    }

    /**
     * Returns the {@link InventoryMap}
     *
     * @return Then {@link InventoryMap}
     */
    protected InventoryMap getInventoryMap() {
        return inventoryMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractPane))
            return false;
        AbstractPane that = (AbstractPane) o;
        return Objects.equals(components, that.components) &&
                  Objects.equals(size, that.size) &&
                  Objects.equals(inventoryMap, that.inventoryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components, size, inventoryMap);
    }

    //<editor-fold desc="Utility Classes">
    /* *************************************************************************
     *                                                                         *
     *                           Utility Classes                               *
     *                                                                         *
     **************************************************************************/

    //<editor-fold desc="Interval">
    /* *************************************************************************
     *                               Interval
     ***************************************************************************/

    /**
     * An Interval
     */
    protected static class Interval {
        private int minX, maxX;
        private int minY, maxY;

        /**
         * Creates an interval
         *
         * @param minX The min x (inclusive)
         * @param maxX The max x (exclusive)
         * @param minY The min y (inclusive)
         * @param maxY The max y (exclusive)
         */
        protected Interval(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        /**
         * @return The min x. Inclusive.
         */
        protected int getMinX() {
            return minX;
        }

        /**
         * @return The max x. Exclusive.
         */
        protected int getMaxX() {
            return maxX;
        }

        /**
         * @return The min y. Inclusive.
         */
        protected int getMinY() {
            return minY;
        }

        /**
         * @return The max y. Exclusive.
         */
        protected int getMaxY() {
            return maxY;
        }

        protected boolean isInside(int x, int y) {
            return x >= minX && x < maxX
                      && y >= minY && y < maxY;
        }

        @Override
        public String toString() {
            return "Interval{" +
                      "minX=" + minX +
                      ", maxX=" + maxX +
                      ", minY=" + minY +
                      ", maxY=" + maxY +
                      '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Interval))
                return false;
            Interval interval = (Interval) o;
            return minX == interval.minX &&
                      maxX == interval.maxX &&
                      minY == interval.minY &&
                      maxY == interval.maxY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minX, maxX, minY, maxY);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Map">
    /* *************************************************************************
     *                             Inventory Map
     ***************************************************************************/

    /**
     * Maps components to their coordinates
     */
    protected static class InventoryMap {
        protected boolean[][] lines;
        protected Map<Interval, Component> componentMap = new HashMap<>();

        /**
         * @param dimension The size of this map
         *
         * @throws IllegalArgumentException if dimension is null
         */
        protected InventoryMap(Dimension dimension) {
            Objects.requireNonNull(dimension);

            lines = new boolean[dimension.getHeight()][];

            for (int y = 0; y < dimension.getHeight(); y++) {
                lines[y] = new boolean[dimension.getWidth()];

                for (int x = 0; x < dimension.getWidth(); x++) {
                    lines[y][x] = false;
                }
            }
        }

        /**
         * Returns the size of this map
         *
         * @return The size of this map
         */
        protected Dimension getSize() {
            return new Dimension(lines[0].length, lines.length);
        }

        /**
         * Adds a component starting with the given upper left corner, if the space is enough
         *
         * @param x The x coordinate of the upper left corner
         * @param y The y coordinate of the upper left corner
         * @param component The component to add
         *
         * @return True if could be added, false if the space was not enough or something else went wrong.
         *
         * @throws IllegalArgumentException if <code>x < 0 or y < 0 or x > width or y > height</code>
         * @throws NullPointerException if component is null
         */
        protected boolean addComponent(int x, int y, Component component) {
            ensureInSize(x, y);

            Objects.requireNonNull(component);

            Dimension componentSize = component.getSize();

            if (!hasEnoughSpace(x, y, componentSize)) {
                return false;
            }

            fillInterval(x, x + componentSize.getWidth(), y, y + componentSize.getHeight(), true);

            Interval interval = new Interval(x, x + componentSize.getWidth(), y, y + componentSize.getHeight());
            componentMap.put(interval, component);

            return true;
        }

        /**
         * Checks if x an y are inside the bounds of this map
         *
         * @param x The x coordinate
         * @param y The y coordinate
         *
         * @throws IllegalArgumentException if <code>x < 0 or y < 0 or x > width or y > height</code>
         */
        private void ensureInSize(int x, int y) {
            if (x < 0) {
                throw new IllegalArgumentException("x < 0 (" + x + ")");
            }
            if (y < 0) {
                throw new IllegalArgumentException("y < 0 (" + y + ")");
            }
            if (x >= lines[0].length) {
                throw new IllegalArgumentException("x >= size (" + x + "/" + lines[0].length + ")");
            }
            if (y >= lines.length) {
                throw new IllegalArgumentException("y >= size (" + y + "/" + lines.length + ")");
            }
        }

        /**
         * Removes the given component
         *
         * @param component The component to remove
         *
         * @throws NullPointerException if component is null
         */
        protected void removeComponent(Component component) {
            Objects.requireNonNull(component);

            // check if it is inside. Yes, this is slow.
            Optional<Interval> componentEntry = componentMap.entrySet().stream()
                      .filter(entry -> entry.getValue().equals(component))
                      .map(Entry::getKey)
                      .findFirst();

            // not inside
            if (!componentEntry.isPresent()) {
                return;
            }
            componentMap.remove(componentEntry.get());
            
            
            // free up the space
            fillInterval(componentEntry.get(), false);
        }

        /**
         * Fills the Interval with the given value
         *
         * @param minX The min x (inclusive)
         * @param maxX The max x (exclusive)
         * @param minY The min y (inclusive)
         * @param maxY The max y (exclusive)
         * @param value The value to fill it with
         *
         * @throws IllegalArgumentException if <code>minX/maxX < 0 or minY/maxY < 0 or minX/maxX > width or minY/maxY > height
         * or minX > maxX or minY > maxY</code>
         */
        protected void fillInterval(int minX, int maxX, int minY, int maxY, boolean value) {
            ensureInSize(minX, minY);
            // is exclusive.
            ensureInSize(maxX - 1, maxY - 1);

            if (minX > maxX) {
                throw new IllegalArgumentException("minX > maxX (" + minX + "/" + maxX + ")");
            }
            if (minY > maxY) {
                throw new IllegalArgumentException("minY > maxY (" + minY + "/" + maxY + ")");
            }

            for (int y = minY; y < maxY; y++) {
                for (int x = minX; x < maxX; x++) {
                    lines[y][x] = value;
                }
            }
        }

        /**
         * Fills the Interval with the given value
         *
         * @param interval The interval to fill
         * @param value The value to fill it with
         *
         * @throws IllegalArgumentException if <code>minX/maxX < 0 or minY/maxY < 0 or minX/maxX > width or minY/maxY > height
         * or minX > maxX or minY > maxY</code>
         * @see #fillInterval(int, int, int, int, boolean)
         */
        protected void fillInterval(Interval interval, boolean value) {
            fillInterval(interval.getMinX(), interval.getMaxX(), interval.getMinY(), interval.getMaxY(), value);
        }

        /**
         * Returns the Component at the given location.
         *
         * @param x The x coordinate of the component
         * @param y The y coordinate of the component
         *
         * @return The Component at the given location, if any
         *
         * @throws IllegalArgumentException if <code>x < 0 or y < 0 or x > width or y > height</code>
         */
        protected Optional<Component> getComponent(int x, int y) {
            ensureInSize(x, y);

            for (Entry<Interval, Component> entry : componentMap.entrySet()) {
                if (entry.getKey().isInside(x, y)) {
                    return Optional.ofNullable(entry.getValue());
                }
            }
            return Optional.empty();
        }

        /**
         * Returns an unmodifiable view of the component map
         *
         * @return An unmodifiable view of the Component map
         */
        protected Map<Interval, Component> getComponentMap() {
            return Collections.unmodifiableMap(componentMap);
        }

        /**
         * Checks if there is enough space
         *
         * @param x The x coordinate of the upper left corner
         * @param y The y coordinate of the upper left corner
         * @param dimension The size of the element
         *
         * @return True if there is enum space
         *
         * @throws IllegalArgumentException if <code>x < 0 or y < 0 or x > width or y > height</code>
         */
        protected boolean hasEnoughSpace(int x, int y, Dimension dimension) {
            ensureInSize(x, y);

            // exceeds height
            if (y + dimension.getHeight() > lines.length) {
                return false;
            }
            // exceeds width
            if (x + dimension.getWidth() > lines[y].length) {
                return false;
            }

            for (int tmpY = 0; tmpY < dimension.getHeight(); tmpY++) {
                for (int tmpX = 0; tmpX < dimension.getWidth(); tmpX++) {
                    if (lines[y + tmpY][x + tmpX]) {
                        return false;
                    }
                }
            }

            return true;
        }

        // TODO: 02.10.2016 Remove these visualizing methods 
/*
        protected void printLines() {
            Status[][] array = new Status[lines.length][];
            for (int y = 0; y < lines.length; y++) {
                array[y] = new Status[lines[y].length];

                for (int x = 0; x < lines[0].length; x++) {
                    array[y][x] = lines[y][x] ? Status.TAKEN : Status.FREE;
                }
            }

            printLines(array);
        }

        protected void printLines(Status[][] lines) {
            for (Status[] line : lines) {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                for (Status b : line) {
                    String string = b.toString();
                    string = padRight(string, 6);
                    string = b.color(string);
                    string = string + ANSI_RESET;
                    builder.append(string);
                }
                builder.append("]");
                System.out.println(builder);
            }
        }

        protected static String padRight(String s, int n) {
            return String.format("%1$-" + n + "s", s);
        }

        protected static final String ANSI_RESET  = "\u001B[0m";
        protected static final String ANSI_BLACK  = "\u001B[30m";
        protected static final String ANSI_RED    = "\u001B[31m";
        protected static final String ANSI_GREEN  = "\u001B[32m";
        protected static final String ANSI_YELLOW = "\u001B[33m";
        protected static final String ANSI_BLUE   = "\u001B[34m";
        protected static final String ANSI_PURPLE = "\u001B[35m";
        protected static final String ANSI_CYAN   = "\u001B[36m";
        protected static final String ANSI_WHITE  = "\u001B[37m";

        protected enum Status {
            TAKEN(ANSI_RED),
            FREE(ANSI_GREEN),
            MAYBE(ANSI_BLUE);

            private String color;

            Status(String color) {
                this.color = color;
            }

            protected String color(String input) {
                return color + input;
            }

            @Override
            public String toString() {
                switch (this) {
                case FREE:
                    return "free";
                case MAYBE:
                    return "maybe";
                default:
                    return "taken";
                }
            }
        }
*/
    }
    //</editor-fold>
    //</editor-fold>
}
