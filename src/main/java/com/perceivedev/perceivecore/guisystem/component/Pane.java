package com.perceivedev.perceivecore.guisystem.component;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guisystem.Scene;
import com.perceivedev.perceivecore.guisystem.util.Dimension;

/**
 * A Pane to add Components to
 */
public interface Pane extends Component {

    /**
     * Reacts to a click
     *
     * @param event The event. Distributes it to the place it belongs.
     */
    @Override
    void onClick(InventoryClickEvent event);

    /**
     * Called to set the scene this pane is in.
     * <p>
     * Should not be called by you. It is possible, but you can screw things up. Ugly implementation, because it allows i7 :/
     *
     * @param scene The Scene
     */
    void setScene(Scene scene);

    /**
     * Returns the Scene this pane is in
     *
     * @return The Scene this pane is in
     */
    Scene getScene();

    /**
     * Adds a component
     *
     * @param component The component to add
     *
     * @return True if the component was added
     */
    boolean addComponent(Component component);

    /**
     * Removes a component
     *
     * @param component The component to remove
     */
    void removeComponent(Component component);

    /**
     * Checks if it contains a component
     *
     * @param component The component to search
     *
     * @return True if it contains the component
     */
    boolean containsComponent(Component component);

    /**
     * Returns the size of this pane
     *
     * @return The size of this pane
     */
    @Override
    Dimension getSize();

    /**
     * Returns all children of this pane
     *
     * @return All the children, Not modifiable.
     */
    Collection<Component> getChildrenUnmodifiable();

    @Override
    void render(Inventory inventory, Player player, int x, int y);

    @Override
    Pane deepClone();
}
