package com.perceivedev.perceivecore.guisystem;

import static com.perceivedev.perceivecore.guisystem.AbstractPane.InventoryMap.ANSI_CYAN;
import static com.perceivedev.perceivecore.guisystem.AbstractPane.InventoryMap.ANSI_RESET;
import static com.perceivedev.perceivecore.guisystem.AbstractPane.InventoryMap.ANSI_YELLOW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A pane that just throws the children in as they fit.
 */
public class FlowPane extends AbstractPane {

    public FlowPane(List<Component> components, Dimension size) {
        super(components, size, new FlowInventoryMap(size));
    }

    @Override
    public boolean addComponent(Component component) {
        int[] location = ((FlowInventoryMap) getInventoryMap()).getNextComponentStartingLocation(component.getSize());
        if (location == null) {
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
        if (!containsComponent(component)) {
            return;
        }
        getChildrenModifiable().removeIf(component1 -> component1.equals(component));
        getInventoryMap().removeComponent(component);
    }

    /**
     * Maps components to their coordinates
     */
    private static class FlowInventoryMap extends InventoryMap {

        private FlowInventoryMap(Dimension dimension) {
            super(dimension);
        }

        /**
         * Computes the starting location for the component
         *
         * @param dimension The Dimension of the component
         *
         * @return The starting location for the component. <code>null</code> if none. 0 == x, 1 == y
         */
        private int[] getNextComponentStartingLocation(Dimension dimension) {
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
    }

    public static void main(String[] args) {
        Dimension invSize = new Dimension(10, 10);
        FlowPane pane = new FlowPane(Collections.emptyList(), invSize);
        pane.addComponent(new DummyComp(4, 4));
        pane.addComponent(new DummyComp(4, 4));
        pane.addComponent(new DummyComp(4, 4));
        pane.render(null, null);
    }

    private static class DummyComp implements Component {
        private static int counter;

        private Dimension dimension;
        private int id = ++counter;

        public DummyComp(int width, int height) {
            this.dimension = new Dimension(width, height);
        }

        @Override
        public void onClick(InventoryClickEvent clickEvent) {
            System.out.println("Hey, it is me " + hashCode());
        }

        @Override
        public Dimension getSize() {
            return dimension;
        }

        @Override
        public void render(Inventory inventory, Player player, int x, int y) {
            System.out.println();
            System.out.println(ANSI_CYAN + ">>>>>>>>>>>>>>>>>>>>>>" + ANSI_RESET);
            System.out.println("Spanning " + x + "->" + (x + getSize().getWidth()) + " and " + y + "->" + (y + getSize().getHeight()));
            System.out.println();
            InventoryMap map = new InventoryMap(new Dimension(10, 10));
            map.addComponent(x, y, this);
            map.printLines();
            System.out.println();
            System.out.println(ANSI_YELLOW + "<<<<<<<<<<<<<<<<<<<<<<" + ANSI_RESET);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof DummyComp))
                return false;
            DummyComp dummyComp = (DummyComp) o;
            return id == dummyComp.id &&
                      Objects.equals(dimension, dummyComp.dimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimension, id);
        }

        @Override
        public String toString() {
            return "DummyComp{" +
                      "dimension=" + dimension +
                      '}';
        }
    }
}
