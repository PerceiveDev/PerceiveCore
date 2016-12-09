package com.perceivedev.perceivecore.gui.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.Gui;
import com.perceivedev.perceivecore.gui.util.Dimension;

// @formatter:off
/**
 * A Skeleton implementation for the {@link Pane} class
 * <p>
 * A note regarding <b>adding</b>:
 * <ul>
 *     <li>
 *         <b><i>It must update the {@link #getInventoryMap()} itself.</i></b>
 *         <p>
 *         <b>You should call upon successful add
 *         {@link #updateComponentHierarchy(Component)} or perform the tasks
 *         yourself</b>
 *     </li>
 * </ul>
 */
// @formatter:on
public abstract class AbstractPane extends AbstractComponent implements Pane {

    protected List<Component> components;
    private InventoryMap inventoryMap;

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the size of the InventoryMap is
     *             different than the size of this Pane
     */
    public AbstractPane(int width, int height, InventoryMap inventoryMap) {
        super(new Dimension(width, height));

        Objects.requireNonNull(inventoryMap);

        if (!inventoryMap.getSize().equals(getSize())) {
            throw new IllegalArgumentException("InventoryMap has wrong size. Expected " + getSize() + " got " + inventoryMap.getSize());
        }

        this.components = new ArrayList<>();
        this.inventoryMap = inventoryMap;
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public AbstractPane(int width, int height) {
        this(width, height, new InventoryMap(new Dimension(width, height)));
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
    public boolean containsComponent(Component component) {
        return components.contains(component);
    }

    @Override
    public Optional<Component> getComponentAtPoint(int x, int y) {
        return getInventoryMap().getComponent(x, y);
    }

    @Override
    public Collection<Component> getChildren() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public void setGui(Gui gui) {
        // Update for all!
        super.setGui(gui);
        for (Component component : getChildren()) {
            component.setGui(gui);
        }
    }

    // @formatter:off
    /**
     * Performs basic updating task, that need to be done at adding
     * 
     * Currently:
     * <ul>
     *     <li>Sets the owner gui</li>
     * </ul>
     *
     * @param component The component to update
     */
    // @formatter:on
    protected void updateComponentHierarchy(Component component) {
        component.setGui(ownerGui);
    }

    @Override
    public boolean removeComponent(Component component) {
        Objects.requireNonNull(component);

        if (!containsComponent(component)) {
            return true;
        }

        components.remove(component);
        getInventoryMap().removeComponent(component);

        component.setGui(null);
        return true;
    }

    @Override
    public boolean requestReRender() {
        if (ownerGui == null) {
            return false;
        }

        // TODO: 29.10.2016 Continue here! Currently re-renders the Whole Gui
        return ownerGui.reRender();
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (!isVisible()) {
            return;
        }
        clickEvent.setLastPane(this);

        if (clickEvent.isOutsideInventory()) {
            clickEvent.setCancelled(true);
            // prevent nasty bugs, as the slot is negative
            return;
        }

        // user clicked in his own inventory. Silently drop it
        if (clickEvent.getRaw().getRawSlot() > clickEvent.getRaw().getInventory().getSize()) {
            return;
        }

        int x = slotToGrid(clickEvent.getSlot())[0] - clickEvent.getOffsetX();
        int y = slotToGrid(clickEvent.getSlot())[1] - clickEvent.getOffsetY();

        Optional<Component> componentOptional = getInventoryMap().getComponent(x, y);
        if (componentOptional.isPresent()) {
            Component component = componentOptional.get();

            if (!component.isVisible()) {
                return;
            }

            Optional<Interval> intervalOpt = getInventoryMap().getComponentInterval(component);
            // Adjust the offsets you pass on, to make the calculations for the
            // next pane work
            intervalOpt.ifPresent(interval -> {
                clickEvent.setOffsetX(clickEvent.getOffsetX() + interval.getMinX());
                clickEvent.setOffsetY(clickEvent.getOffsetY() + interval.getMinY());
                clickEvent.setComponent(component);
            });
            component.onClick(clickEvent);
        }
    }

    /**
     * Renders the component in the Inventory
     * <p>
     * <b>If you overwrite this method, check if the components are visible
     * before rendering them!</b>
     * 
     * @param inventory The inventory to render in
     * @param player The Player to render for
     * @param x The x offset
     * @param y The y offset
     */
    @Override
    public void render(Inventory inventory, Player player, int x, int y) {
        if (!isVisible()) {
            return;
        }
        for (Entry<Interval, Component> entry : getInventoryMap().getComponentMap().entrySet()) {
            // skip invisible ones
            if (!entry.getValue().isVisible()) {
                continue;
            }
            if (fitsInside(inventory, x, y, entry.getKey())) {
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
        return !(((interval.getMinY() + yOffset) >= inventoryHeight)
                || ((interval.getMaxY() + yOffset) > inventoryHeight));
    }

    /**
     * Clones the component.
     * <p>
     * <b>Leaves the reference to the Scene intact!</b>
     *
     * @return A deepClone of this component
     */
    @Override
    public AbstractPane deepClone() {
        AbstractPane superClone = (AbstractPane) super.clone();
        superClone.inventoryMap = inventoryMap.clone();
        superClone.components = new ArrayList<>();
        superClone.components.addAll(components
                .stream()
                .map(Component::deepClone)
                .collect(Collectors.toList()));
        return superClone;
    }

    // <editor-fold desc="Utility Classes">
    // -------------------- Utility Classes -------------------- //

    // <editor-fold desc="Interval">
    // -------------------- Interval -------------------- //

    /**
     * An Interval
     */
    protected static class Interval implements Cloneable {
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
        public int getMinX() {
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
        public int getMinY() {
            return minY;
        }

        /**
         * @return The max y. Exclusive.
         */
        protected int getMaxY() {
            return maxY;
        }

        /**
         * Checks if the given coordinates are inside the Interval
         * 
         * @param x The x coordinate to check
         * @param y The y coordinate to check
         * @return True if the coordinates are inside this Interval
         */
        protected boolean isInside(int x, int y) {
            return x >= minX && x < maxX
                    && y >= minY && y < maxY;
        }

        @Override
        public Interval clone() throws CloneNotSupportedException {
            return (Interval) super.clone();
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
    // </editor-fold>

    // <editor-fold desc="Inventory Map">
    // -------------------- Inventory Map -------------------- //

    /**
     * Maps components to their coordinates You may pass your own to a map, if
     * you really want. Could be useful if you make a optimised version
     */
    public static class InventoryMap implements Cloneable {
        protected boolean[][] lines;
        protected Map<Interval, Component> componentMap = new HashMap<>();

        /**
         * @param dimension The size of this map
         *
         * @throws IllegalArgumentException if dimension is null
         */
        public InventoryMap(Dimension dimension) {
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
        public Dimension getSize() {
            return new Dimension(lines[0].length, lines.length);
        }

        /**
         * Adds a component starting with the given upper left corner, if the
         * space is enough
         *
         * @param x The x coordinate of the upper left corner
         * @param y The y coordinate of the upper left corner
         * @param component The component to add
         *
         * @return True if could be added, false if the space was not enough or
         *         something else went wrong.
         *
         * @throws IllegalArgumentException if
         *             {@code x < 0 or y < 0 or x > width or y > height}
         * @throws NullPointerException if component is null
         */
        public boolean addComponent(int x, int y, Component component) {
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
         * @throws IllegalArgumentException if
         *             {@code x < 0 or y < 0 or x > width or y > height}
         */
        protected void ensureInSize(int x, int y) {
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
        public void removeComponent(Component component) {
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
         * @throws IllegalArgumentException if
         *             {@code minX/maxX < 0 or minY/maxY < 0 or minX/maxX > width or minY/maxY > height
         * or minX > maxX or minY > maxY}
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
         * @throws IllegalArgumentException if
         *             {@code minX/maxX < 0 or minY/maxY < 0 or minX/maxX > width or minY/maxY > height
         * or minX > maxX or minY > maxY}
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
         * @throws IllegalArgumentException if
         *             {@code x < 0 or y < 0 or x > width or y > height}
         */
        public Optional<Component> getComponent(int x, int y) {
            ensureInSize(x, y);

            for (Entry<Interval, Component> entry : componentMap.entrySet()) {
                if (entry.getKey().isInside(x, y)) {
                    return Optional.ofNullable(entry.getValue());
                }
            }
            return Optional.empty();
        }

        /**
         * Returns the interval for a Component
         *
         * @param component The Component to get it for
         *
         * @return The Interval for the Component
         */
        public Optional<Interval> getComponentInterval(Component component) {
            return componentMap.entrySet().stream().filter(entry -> entry.getValue().equals(component)).map(Entry::getKey).findFirst();
        }

        /**
         * Returns an unmodifiable view of the component map
         *
         * @return An unmodifiable view of the Component map
         */
        public Map<Interval, Component> getComponentMap() {
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
         * @throws IllegalArgumentException if
         *             {@code x < 0 or y < 0 or x > width or y > height}
         */
        public boolean hasEnoughSpace(int x, int y, Dimension dimension) {
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

        @Override
        public InventoryMap clone() {
            try {
                InventoryMap clone = (InventoryMap) super.clone();
                clone.lines = lines.clone();
                for (int i = 0; i < lines.length; i++) {
                    clone.lines[i] = lines[i].clone();
                }
                clone.componentMap = new HashMap<>();
                for (Entry<Interval, Component> entry : componentMap.entrySet()) {
                    clone.componentMap.put(entry.getKey().clone(), entry.getValue().deepClone());
                }
                return clone;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }

        // TODO: 02.10.2016 Remove these visualizing methods
        /*
         * 
         * protected void printLines() {
         * Status[][] array = new Status[lines.length][];
         * for (int y = 0; y < lines.length; y++) {
         * array[y] = new Status[lines[y].length];
         * 
         * for (int x = 0; x < lines[0].length; x++) {
         * array[y][x] = lines[y][x] ? Status.TAKEN : Status.FREE;
         * }
         * }
         * 
         * printLines(array);
         * }
         * 
         * protected void printLines(Status[][] lines) {
         * for (Status[] line : lines) {
         * StringBuilder builder = new StringBuilder();
         * builder.append("[");
         * for (Status b : line) {
         * String string = b.toString();
         * string = padRight(string, 6);
         * string = b.color(string);
         * string = string + ANSI_RESET;
         * builder.append(string);
         * }
         * builder.append("]");
         * System.out.println(builder);
         * }
         * }
         * 
         * protected static String padRight(String s, int n) {
         * return String.format("%1$-" + n + "s", s);
         * }
         * 
         * protected static final String ANSI_RESET = "\u001B[0m";
         * protected static final String ANSI_BLACK = "\u001B[30m";
         * protected static final String ANSI_RED = "\u001B[31m";
         * protected static final String ANSI_GREEN = "\u001B[32m";
         * protected static final String ANSI_YELLOW = "\u001B[33m";
         * protected static final String ANSI_BLUE = "\u001B[34m";
         * protected static final String ANSI_PURPLE = "\u001B[35m";
         * protected static final String ANSI_CYAN = "\u001B[36m";
         * protected static final String ANSI_WHITE = "\u001B[37m";
         * 
         * protected enum Status {
         * TAKEN(ANSI_RED),
         * FREE(ANSI_GREEN),
         * MAYBE(ANSI_BLUE);
         * 
         * private String color;
         * 
         * Status(String color) {
         * this.color = color;
         * }
         * 
         * protected String color(String input) {
         * return color + input;
         * }
         * 
         * @Override
         * public String toString() {
         * switch (this) {
         * case FREE:
         * return "free";
         * case MAYBE:
         * return "maybe";
         * default:
         * return "taken";
         * }
         * }
         * }
         */

    }
    // </editor-fold>
    // </editor-fold>
}
