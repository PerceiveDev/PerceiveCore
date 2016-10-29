package com.perceivedev.perceivecore.guireal;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.perceivedev.perceivecore.guireal.components.base.pane.Pane;
import com.perceivedev.perceivecore.guireal.components.implementation.pane.AnchorPane;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * A Gui for a player
 * <p>
 * Contains the Inventory and the Player
 */
public class Gui implements InventoryHolder {

    private UUID      playerID;
    private Inventory inventory;
    private Pane      rootPane;

    /**
     * @param name The name of the Gui
     * @param rows The amount of rows (each has 9 slots) in the gui
     * @param rootPane The root pane to use
     */
    public Gui(String name, int rows, Pane rootPane) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(rootPane);

        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows invalid. Allowed range: '1 <= rows <= 6'. Given was '" + rows + "'");
        }

        this.inventory = Bukkit.createInventory(this, rows * 9, TextUtils.colorize(name));
        this.rootPane = rootPane;
    }

    /**
     * @param name The name of the Gui
     * @param rows The amount of rows (each has 9 slots) in the gui
     *
     * @see #Gui(String, int, Pane) {@link #Gui(String, int, Pane)} -> passes an AnchorPane
     */
    public Gui(String name, int rows) {
        this(name, rows, new AnchorPane(9, rows));
    }

    /**
     * Returns the root pane
     *
     * @return The root pane
     */
    public Pane getRootPane() {
        return rootPane;
    }

    /**
     * Returns the inventory this Gui uses.
     * <p>
     * If you modify it you better know what you are doing!
     *
     * @return The inventory this Gui uses.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Re-Renders the Gui.
     *
     * @return False if the player is not online or an error occurred. True if it was re-rendered.
     */
    public boolean reRender() {
        Optional<Player> player = getPlayer();
        if (!player.isPresent()) {
            return false;
        }

        rootPane.render(inventory, player.get(), 0, 0);

        return true;
    }

    /**
     * Opens the Gui for the player, if no other Gui is opened
     *
     * @param player The player to open the Gui for
     */
    public void open(Player player) {
        Objects.requireNonNull(player);

        playerID = player.getUniqueId();

        reRender();
        // TODO: 29.10.2016 Incorporate GuiManager 
        player.openInventory(inventory);
    }

    /**
     * Closes the Inventory
     *
     * @throws IllegalStateException If player is null or the player doesn't have this Gui opened
     */
    public void close() {
        if (!getPlayer().isPresent()) {
            throw new IllegalStateException("Player not found!");
        }

        // TODO: 29.10.2016 Check if you are opened 
        getPlayer().ifPresent(HumanEntity::closeInventory);
    }

    /**
     * Handles the {@link InventoryClickEvent}
     *
     * @param event The {@link InventoryClickEvent}
     */
    public void onClick(InventoryClickEvent event) {
        rootPane.onClick(new ClickEvent(event, rootPane));
    }

    /**
     * Checks if an Inventory belongs to this Gui
     *
     * @param inventory The inventory to check
     *
     * @return True if the given inventory is this Gui
     */
    boolean isYou(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    /**
     * Gets the player this Gui belongs to if he is online
     *
     * @return The player if he is online
     */
    public Optional<Player> getPlayer() {
        if (playerID == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Bukkit.getPlayer(playerID));
    }
}
