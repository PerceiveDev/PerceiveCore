package com.perceivedev.perceivecore.guisystem;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A scene. Contains the components to paint
 */
public class Scene {

    private Pane      pane;
    private Inventory inventory;

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
        pane.render(inventory, player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    /**
     * Sets the pane
     *
     * @param pane The new pane
     */
    public void setPane(Pane pane) {
        this.pane = pane;
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
}
