package com.perceivedev.perceivecore.gui.base;

import java.util.Collection;
import java.util.Optional;

/** The base class for Panes. */
public interface Pane extends Component {

    /**
     * Removes a {@link Component}
     *
     * @param component The {@link Component} to remove
     *
     * @return <code>true</code> if the {@link Component} was removed
     */
    boolean removeComponent(Component component);

    /**
     * Checks if the Pane contains the {@link Component}.
     *
     * @param component The {@link Component}
     *
     * @return True if it contains the component
     */
    boolean containsComponent(Component component);

    /**
     * Returns all children of this pane
     *
     * @return The children of this Pane
     */
    Collection<Component> getChildren();

    /**
     * Gets a component in a given slot
     *
     * @param x The x coordinate of the slot
     * @param y The y coordinate of the slot
     *
     * @return The component at this slot
     */
    Optional<Component> getComponentAtPoint(int x, int y);

    /**
     * Re-renders this pane
     *
     * @return <code>true</code> if it was re-rendered
     */
    boolean requestReRender();

    @Override
    Pane deepClone();
}
