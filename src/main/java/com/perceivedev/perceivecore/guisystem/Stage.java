package com.perceivedev.perceivecore.guisystem;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A Stage that can be displayed
 */
public class Stage {

    private Scene   scene;
    private UUID    playerID;
    private boolean isClosable;

    public Stage(Scene scene, UUID playerID) {
        this.scene = scene;
        this.playerID = playerID;
    }

    /**
     * Sets the scene
     *
     * @param scene The scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Returns the scene
     *
     * @return The Scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Checks if the user can close the GUI with 'E' or 'ESC'
     *
     * @param closable If true, the user can close the GUI with 'E' or 'ESC'
     */
    public void setClosable(boolean closable) {
        isClosable = closable;
    }

    /**
     * Checks whether a user button input can close the GUI
     *
     * @return True if the user can close the GUI with 'E' or 'ESC'
     */
    public boolean isClosable() {
        return isClosable;
    }

    /**
     * Opens the Gui for a player
     */
    public void open() {
        System.out.println("Opening!");
        getPlayer().ifPresent(player -> {
            getScene().render(player);
            getScene().open(player);
        });
    }

    public void onClick(InventoryClickEvent event) {
        scene.onClick(event);
    }

    /**
     * Returns the player this Gui belongs to
     *
     * @return The Player of this Gui, if he is online
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(playerID));
    }
}
