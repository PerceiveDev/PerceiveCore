package com.perceivedev.perceivecore.guisystem;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A Stage that can be displayed
 */
public class Stage {

    private Scene            scene;
    private UUID             playerID;
    private boolean          isClosable;
    private PlayerGuiManager guiManager;

    /**
     * Creates a Stage for the player
     *
     * @param scene The Scene
     * @param playerID The {@link UUID} of the player
     * @param isClosable Whether the player can close the GUI with 'E' or 'ESC'
     */
    public Stage(Scene scene, UUID playerID, boolean isClosable) {
        this.scene = scene;
        this.playerID = playerID;
        this.isClosable = isClosable;
    }

    /**
     * Creates a Stage for a player
     *
     * @param scene The Scene
     * @param playerID The {@link UUID} of the player
     *
     * @see #Stage(Scene, UUID, boolean) #Stage(Scene, UUID, boolean) with isClosable set to true
     */
    public Stage(Scene scene, UUID playerID) {
        this(scene, playerID, true);
    }

    /**
     * Sets the Gui manager
     * <p>
     * Invoked by the GuiManager itself, on rendering this
     *
     * @param guiManager The GuiManager
     */
    void setGuiManager(PlayerGuiManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * Returns the GuiManager of this scene.
     *
     * @return The GuiManager of this scene. Null if this stage hasn't been rendered yet.
     */
    protected PlayerGuiManager getGuiManager() {
        return guiManager;
    }

    /**
     * Sets the scene
     *
     * @param scene The scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;

        // never rendered, so not visible
        if (getGuiManager() == null) {
            return;
        }

        Optional<Stage> openedStage = getGuiManager().getOpenedStage(playerID);

        // nothing opened right now
        if (!openedStage.isPresent()) {
            return;
        }

        // I am not opened
        if (!this.equals(openedStage.get())) {
            return;
        }

        // okay, try to swap it while visible

        // remove as the onClose event will either cancel the close or remove it itself.
        getGuiManager().removeOpenedStage(playerID);

        // re-add it and show it
        getGuiManager().addStage(playerID, this);
        getGuiManager().openFirstStage(playerID);
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
     * <p>
     * Package-Private as you need to use the {@link PlayerGuiManager} classes outside,
     * otherwise you could open a gui which is no longer in the manager.
     */
    void open() {
        getPlayer().ifPresent(player -> {
            getScene().render(player);
            getScene().open(player);
        });
    }

    /**
     * Closes the inventory of the player
     */
    void close() {
        getPlayer().ifPresent(HumanEntity::closeInventory);
    }

    void onClick(InventoryClickEvent event) {
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
