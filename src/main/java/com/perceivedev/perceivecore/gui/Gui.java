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
import com.perceivedev.perceivecore.gui.base.FixedPositionPane;
import com.perceivedev.perceivecore.gui.base.FreeformPane;
import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.panes.AnchorPane;
import com.perceivedev.perceivecore.util.text.TextUtils;

/**
 * A Gui for a player
 * <p>
 * Contains the Inventory and the Player
 */
public class Gui implements InventoryHolder {

    private static int counter;

    private final int ID = counter++;

    private UUID playerID;
    private Inventory inventory;
    private Pane rootPane;
    private boolean reopenOnClose;
    private boolean killMe;

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
     * Creates a gui with the given Inventory
     * 
     * @param name The name of the Gui
     * @param rootPane The root pane to use
     * @param inventory The inventory to use
     */
    protected Gui(String name, int rows, Pane rootPane, Inventory inventory) {
        Objects.requireNonNull(name, "name can not be null!");
        Objects.requireNonNull(rootPane, "rootPane can not be null!");
        Objects.requireNonNull(inventory, "inventory can not be null!");

        this.inventory = inventory;
        this.rootPane = rootPane;
        rootPane.setGui(this);
    }

    /**
     * @param name The name of the Gui
     * @param rows The amount of rows (each has 9 slots) in the gui
     *
     * @see #Gui(String, int, Pane) {@link #Gui(String, int, Pane)} {@code ->}
     *      passes an
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

    /**
     * Returns the root as a {@link FixedPositionPane}
     * 
     * @throws ClassCastException if the root is NOT a {@link FixedPositionPane}
     * @return The root pane
     */
    public FixedPositionPane getRootAsFixedPosition() {
        return (FixedPositionPane) rootPane;
    }

    /**
     * Returns the root as a {@link FreeformPane}
     * 
     * @throws ClassCastException if the root is NOT a {@link FreeformPane}
     * @return The root pane
     */
    public FreeformPane getRootAsFreeform() {
        return (FreeformPane) rootPane;
    }

    /**
     * Checks whether the root pane is a {@link FreeformPane}
     * 
     * @return True if the root pane ({@link #getRootPane()}) is a
     *         {@link FreeformPane}
     */
    public boolean isRootFreeformPane() {
        return rootPane instanceof FreeformPane;
    }

    /**
     * Checks whether the root pane is a {@link FixedPositionPane}
     * 
     * @return True if the root pane ({@link #getRootPane()}) is a
     *         {@link FixedPositionPane}
     */
    public boolean isRootFixedPositionPane() {
        return rootPane instanceof FixedPositionPane;
    }

    /**
     * Returns the inventory this Gui uses.
     * <p>
     * If you modify it you better know what you are doing!
     *
     * @return The inventory this Gui uses.
     */
    @Override
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
     * <p>
     * The Guis for the player are organized in a Stack. This means opening a
     * Gui while one is already opened will just push it on the stack, and open
     * it as next Gui
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

        onDisplay();

        player.openInventory(getInventory());
    }

    /**
     * Called when this gui is displayed to a player
     * <p>
     * {@link #getPlayer()} is already set at this point
     */
    protected void onDisplay() {

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
     * Called when the Gui is closed. You may overwrite it to listen to close
     * events
     */
    protected void onClose() {

    }

    /**
     * Sets the inventory
     * 
     * @param inventory The inventory to use
     */
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
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

    /**
     * Returns the player this Gui was/is opened for.
     * <p>
     * Will return an empty optional if the GUI was never opened
     * 
     * @return The {@link UUID} of the player this gui is opened for, if any
     */
    protected Optional<UUID> getPlayerID() {
        return Optional.ofNullable(playerID);
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
