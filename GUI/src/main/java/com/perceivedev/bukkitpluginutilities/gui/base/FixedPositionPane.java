package com.perceivedev.bukkitpluginutilities.gui.base;

/**
 * A pane that needs an x and y coordinate to add a component
 */
public interface FixedPositionPane extends Pane {

    /**
     * Adds a {@link Component}
     *
     * @param component The {@link Component} to add
     * @param x The x index
     * @param y The y index
     *
     * @return <code>true</code> if the component could be added
     */
    boolean addComponent(Component component, int x, int y);

    /**
     * Removes a {@link Component}
     *
     * @param y The y index
     * @param x The x index
     *
     * @return <code>true</code> if the {@link Component} was removed
     */
    @SuppressWarnings("unused")
    boolean removeComponent(int x, int y);
}
