package com.perceivedev.perceivecore.guisystem;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guisystem.component.Pane;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * A scene. Contains the components to paint
 */
public class Scene {

    private Pane      pane;
    private Inventory inventory;
    private UUID      playerID;

    /**
     * Constructs a Scene using the given pane and inventory
     *
     * @param pane The Pane to use
     * @param inventory The inventory to use
     *
     * @throws NullPointerException if any parameter is null
     */
    public Scene(Pane pane, Inventory inventory) {
        Objects.requireNonNull(pane);
        Objects.requireNonNull(inventory);

        this.pane = pane;
        this.inventory = inventory;
    }

    /**
     * Constructs a Scene using the given pane, size and title
     *
     * @param pane The Pane to use
     * @param size The size of the inventory
     * @param title The title of the Inventory
     *
     * @throws IllegalArgumentException if size is not a multiple of 9
     */
    public Scene(Pane pane, int size, String title) {
        this(pane, Bukkit.createInventory(null, size, TextUtils.colorize(title)));
    }

    /**
     * Constructs a Scene using the given pane and size
     *
     * @param pane The Pane to use
     * @param size The size of the inventory
     *
     * @throws IllegalArgumentException if size is not a multiple of 9
     */
    public Scene(Pane pane, int size) {
        this(pane, Bukkit.createInventory(null, size));
    }

    /**
     * Renders the components in an inventory
     *
     * @param player The player to render for
     */
    void render(Player player) {
        // remove old frame
        inventory.clear();

        pane.setScene(this);

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
     * Requests to re-render this pane, in case anything changed
     */
    public void requestReRender() {
        getPlayer().ifPresent(this::render);
    }

    /**
     * Sets the pane
     *
     * @param pane The new pane
     *
     * @throws NullPointerException if pane is null
     */
    public void setPane(Pane pane) {
        Objects.requireNonNull(pane);

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

    /**
     * Handles a click in this pane
     *
     * @param event The {@link InventoryClickEvent}
     */
    public void onClick(InventoryClickEvent event) {
        pane.onClick(event);
    }

    /**
     * Returns the player this Gui belongs to.
     * <p>
     * Injected in the {@link #open(Player)} method..
     *
     * @return The Player of this Gui, if he is online
     */
    private Optional<Player> getPlayer() {
        if (playerID == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Bukkit.getPlayer(playerID));
    }
}
