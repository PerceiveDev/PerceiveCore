package com.perceivedev.perceivecore.gui;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.gui.base.Component;
import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.panes.AnchorPane;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * A Gui for a player
 * <p>
 * Contains the Inventory and the Player
 */
public class Gui implements InventoryHolder {

    private static int counter;

    private final int  ID = counter++;

    private UUID       playerID;
    private Inventory  inventory;
    private Pane       rootPane;
    private boolean    reopenOnClose;
    private boolean    killMe;

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
        rootPane.setGui(this);
    }

    /**
     * @param name The name of the Gui
     * @param rows The amount of rows (each has 9 slots) in the gui
     *
     * @see #Gui(String, int, Pane) {@link #Gui(String, int, Pane)} -> passes an
     *      AnchorPane
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

    public boolean addComponent(Component component) {
        return getRootPane().addComponent(component);
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
     * Sets whether the Gui reopens if the player closes it
     *
     * @param reopenOnClose if true, the Gui will reopen if the player closes
     *            it
     */
    public void setReopenOnClose(boolean reopenOnClose) {
        this.reopenOnClose = reopenOnClose;
    }

    /**
     * Checks whether the Gui reopens if the player closes it
     *
     * @return if true, the gui will reopen if the player closes it
     */
    public boolean isReopenOnClose() {
        return reopenOnClose;
    }

    /**
     * Re-Renders the Gui.
     *
     * @return False if the player is not online or an error occurred. True if
     *         it was re-rendered.
     */
    public boolean reRender() {
        Optional<Player> player = getPlayer();
        if (!player.isPresent()) {
            return false;
        }

        inventory.clear();
        rootPane.render(inventory, player.get(), 0, 0);

        return true;
    }

    /**
     * Opens the Gui for the player, if no other Gui is opened
     *
     * @param player The player to open the Gui for
     */
    public void open(Player player) {
        Objects.requireNonNull(player, "player can not be null");

        PerceiveCore.getInstance().getGuiManager().submit(player.getUniqueId(), this);
    }

    /**
     * Pushes the Gui on the stack, <b>without</b> opening it.
     *
     * @param player The player to push the Gui for
     */
    public void push(Player player) {
        Objects.requireNonNull(player, "player can not be null");

        PerceiveCore.getInstance().getGuiManager().addGui(player.getUniqueId(), this);
    }

    /**
     * Opens the inventory for the player
     *
     * @param player The Player to open it for
     */
    void openInventory(Player player) {
        Objects.requireNonNull(player);

        playerID = player.getUniqueId();

        reRender();

        player.openInventory(getInventory());
    }

    /**
     * Whether you should kill the gui
     *
     * @return Whether you should kill the gui
     */
    boolean isKillMe() {
        return killMe;
    }

    /**
     * Whether you should kill the gui
     *
     * @param killMe Whether you should kill the gui
     */
    void setKillMe(boolean killMe) {
        this.killMe = killMe;
    }

    /**
     * Closes the Inventory
     *
     * @throws IllegalStateException If player is null or the player doesn't
     *             have this Gui opened
     */
    public void close() {
        if (!getPlayer().isPresent()) {
            throw new IllegalStateException("Player not found!");
        }

        PerceiveCore.getInstance().getGuiManager().removeGui(playerID, this);
    }

    /**
     * Handles the {@link InventoryClickEvent}
     *
     * @param event The {@link InventoryClickEvent}
     */
    public void onClick(InventoryClickEvent event) {
        rootPane.onClick(new ClickEvent(event, rootPane, null));
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

    @Override
    public String toString() {
        return "Gui{" + "name=" + inventory.getName() + ", id=" + ID + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Gui))
            return false;
        Gui gui = (Gui) o;
        return gui.ID == ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
