package com.perceivedev.perceivecore.gui.base;

/**
 * A pane that is able to add the components without any further parameters
 */
public interface FreeformPane extends Pane {

    /**
     * Adds a {@link Component}
     *
     * @param component The {@link Component} to add
     *
     * @return <code>true</code> if the component could be added
     */
    boolean addComponent(Component component);

}
