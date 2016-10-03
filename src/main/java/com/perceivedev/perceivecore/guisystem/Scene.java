package com.perceivedev.perceivecore.guisystem;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guisystem.component.Pane;

/**
 * A scene. Contains the components to paint
 */
public class Scene {

    private Pane      pane;
    private Inventory inventory;
    private UUID      playerID;

    public Scene(Pane pane, Inventory inventory) {
        this.pane = pane;
        this.inventory = inventory;
    }

    /**
     * Renders the components in an inventory
     *
     * @param player The player to render for
     */
    public void render(Player player) {
        // remove old frame
        inventory.clear();

        pane.render(inventory, player, 0, 0);
    }

    /**
     * Opens the Gui for a player
     * <p>
     * Package-Private as you need to use the {@link PlayerGuiManager} classes outside,
     * otherwise you could open a gui which is no longer in the manager.
     */
    void open(Player player) {
        playerID = player.getUniqueId();
        player.openInventory(inventory);
    }

    /**
     * Sets the pane
     *
     * @param pane The new pane
     */
    public void setPane(Pane pane) {
        this.pane = pane;

        // is open, re-render it
        getPlayer().ifPresent(this::render);
    }

    /**
     * Returns the current pane
     *
     * @return The Pane
     */
    public Pane getPane() {
        return pane;
    }

    public void onClick(InventoryClickEvent event) {
        pane.onClick(event);
    }

    /**
     * Returns the player this Gui belongs to
     *
     * @return The Player of this Gui, if he is online
     */
    private Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(playerID));
    }
}
