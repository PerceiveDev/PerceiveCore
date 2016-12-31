package com.perceivedev.perceivecore.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.PerceiveCore;

/**
 * Manages the {@link Gui}s
 */
public enum GuiManager implements Listener {
    INSTANCE;

    /**
     * Opens the first GUI on the stack for the player. After closing that
     * window, the next GUI on the stack will open, etc. until the stack is
     * empty.
     * <p>
     * <br>
     * This method will open the Gui immediately.
     * 
     * @param player The player to open the GUI for
     * @return If a GUI was actually opened for the player
     */
    public static boolean openFirst(Player player) {
        return INSTANCE.openCurrentGui(player.getUniqueId());
    }

    // ==== START OF INSTANCE RELEVANT CODE ====

    private Map<UUID, PlayerGuiData> playerMap = new HashMap<>();

    {
        // close guis on reload/restart
        PerceiveCore.getInstance().getDisableManager().addListener(() -> {
            Collection<UUID> uuidList = new ArrayList<>(playerMap.keySet());
            uuidList.forEach(this::removeAll);
        });
    }

    // ==== METHODS ====

    /**
     * Adds a Gui to the player's stack, without opening it
     *
     * @param playerID The {@link UUID} of the player
     * @param gui The {@link UUID} to add
     */
    public void addGui(UUID playerID, Gui gui) {
        getOrCreatePlayerData(playerID).addGui(gui);
    }

    /**
     * Adds a Gui to the player's stack, pushing the currently opened Gui back,
     * if any.
     * <br>
     * Does not open a new Gui, if none is opened.
     *
     * <p>
     * <br>
     * This method will add the Gui immediately and open it on the next tick
     * 
     * @param playerID The {@link UUID} of the player
     * @param gui The {@link UUID} to add
     */
    public void addGuiAndBringToFront(UUID playerID, Gui gui) {
        getOrCreatePlayerData(playerID).addGui(gui);
        getOrCreatePlayerData(playerID).freezeCurrentSelectNext();
    }

    /**
     * Closes the currently opened Gui for the player, without removing it from
     * the stack. Will be reopened when you call {@link #openCurrentGui(UUID)}
     * 
     * <p>
     * <br>
     * This method closes the Gui immediately
     * 
     * @param playerID The {@link UUID} of the player
     */
    public void closeGuiWithoutRemoving(UUID playerID) {
        getOrCreatePlayerData(playerID).freezeCurrentGui();
    }

    /**
     * Closes the currently opened Gui for the Player and calls
     * {@link #removeGui(UUID, Gui)} on it
     * <p>
     * Does nothing if no Gui is opened
     * 
     * <p>
     * <br>
     * This method closes the Gui immediately
     * 
     * @param playerID The {@link UUID} of the player
     */
    public void closeOpenedGui(UUID playerID) {
        getOrCreatePlayerData(playerID).closeOpenedGui();
    }

    /**
     * Removes the Gui, closing it if it was opened
     * <p>
     * <br>
     * This method removes the Gui immediately and closes it on the next tick,
     * if
     * it was opened.
     * 
     * @param uuid The {@link UUID} of the player
     * @param gui The {@link Gui} to remove
     */
    public void removeGui(UUID uuid, Gui gui) {
        getOrCreatePlayerData(uuid).removeGui(gui);
    }

    /**
     * Removes ALL {@link Gui}s for the Player, closing any that is opened
     * <p>
     * <br>
     * This method removes and closes all Guis immediately.
     * 
     * @param uuid The {@link UUID} of the player
     */
    public void removeAll(UUID uuid) {
        getOrCreatePlayerData(uuid).removeAllGuis();
    }

    /**
     * Opens the current {@link Gui} for the player
     *
     * @param playerID The {@link UUID} of the player
     *
     * @return True if the Gui was opened, false if an error occurred or it was
     *         put back to later open it
     */
    public boolean openCurrentGui(UUID playerID) {
        return getOrCreatePlayerData(playerID).openNextGui(null);
    }

    /**
     * Adds the Gui and tries to open it
     *
     * <p>
     * <br>
     * This method will open the Gui immediately (if it is its turn)
     * 
     * @param playerID The {@link UUID} of the player
     * @param gui The gui to open
     *
     * @return True if the Gui was opened
     */
    public boolean submit(UUID playerID, Gui gui) {
        PlayerGuiData playerData = getOrCreatePlayerData(playerID);
        playerData.addGui(gui);
        return playerData.openNextGui(null);
    }

    /**
     * @param uuid The {@link UUID} of the player
     *
     * @return the saved PlayerData or a newly created one
     */
    private PlayerGuiData getOrCreatePlayerData(UUID uuid) {
        Objects.requireNonNull(uuid);

        if (playerMap.containsKey(uuid)) {
            return playerMap.get(uuid);
        }
        PlayerGuiData playerGuiData = new PlayerGuiData(uuid);
        playerMap.put(uuid, playerGuiData);

        return playerGuiData;
    }

    private static class PlayerGuiData {
        private UUID playerID;
        private Stack<Gui> guis;

        /**
         * If this returns true, the execution of the {@link #reactToClose(Gui)}
         * will stop right there
         */
        private Function<Gui, Boolean> specialCloseBehaviour;

        /**
         * @param playerID The {@link UUID} of the player
         */
        private PlayerGuiData(UUID playerID) {
            this.playerID = playerID;
            this.guis = new Stack<>();
        }

        /**
         * Adds the Gui to the stack
         *
         * @param gui The {@link UUID} to add
         */
        private void addGui(Gui gui) {
            guis.push(gui);
        }

        /**
         * Freezes the current Gui, hides it (does NOT destroy it) and then
         * displays the next one.
         * <br>
         * If there is just one Gui, this method does nothing.
         */
        private void freezeCurrentSelectNext() {
            if (guis.size() < 2) {
                return;
            }

            getPlayer().ifPresent(player -> {
                specialCloseBehaviour = gui -> {
                    runLater(() -> guis.peek().openInventory(player.getPlayer(), gui));
                    return true;
                };

                // trigger a call of the `reactToClose` method, which will open
                // the next for us
                player.closeInventory();
            });
        }

        private void freezeCurrentGui() {
            if (guis.isEmpty()) {
                return;
            }

            getPlayer().ifPresent(player -> {
                if (player.getOpenInventory().getTopInventory() == null) {
                    return;
                }
                InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();

                // has a Gui opened
                if (guis.peek().equals(holder)) {
                    // just swallow the close event
                    specialCloseBehaviour = gui -> true;

                    // trigger a call of the `reactToClose` method
                    player.closeInventory();
                }
            });
        }

        /**
         * Removes the Gui from the stack
         *
         * @param gui The {@link Gui} to remove
         */
        private void removeGui(Gui gui) {
            guis.remove(gui);
            gui.setKillMe(true);

            Optional<Player> playerOptional = getPlayer();
            if (playerOptional.isPresent()) {
                InventoryHolder holder = playerOptional.get().getOpenInventory().getTopInventory().getHolder();

                if (gui.equals(holder)) {
                    // it may be called in the PlayerCloseInventoryEvent, which
                    // could introduce bugs without this line
                    runLater(() -> {
                        // the Gui may be already closed. Yes, I know that we
                        // only waited the one needed tick, go ask bukkit
                        if (gui.equals(playerOptional.get().getOpenInventory().getTopInventory().getHolder())) {
                            closeCurrentInventory();
                        }
                    });
                }
            }
        }

        /**
         * Removes and closes all {@link Gui}s
         */
        private void removeAllGuis() {
            guis.clear();
            closeOpenedGui();
        }

        /**
         * Opens the next gui, if the player has no gui opened currently
         *
         * @return True if a gui was opened
         * @param previous The previous Gui that was displayed. {@code null} if
         *            this is the first
         */
        private boolean openNextGui(Gui previous) {
            if (guis.isEmpty()) {
                return false;
            }
            Optional<Player> playerOptional = getPlayer();
            if (!playerOptional.isPresent()) {
                return false;
            }

            Player player = playerOptional.get();

            // they have an inventory opened
            if (player.getOpenInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getType() != InventoryType.CREATIVE) {
                return false;
            }

            guis.peek().openInventory(player, previous);

            return true;
        }

        /**
         * Closes the currently opened gui and removes it from the stack
         * <p>
         * Does nothing if no {@link Gui} is opened
         */
        private void closeOpenedGui() {
            Optional<Player> playerOptional = getPlayer();
            if (!playerOptional.isPresent()) {
                return;
            }

            Player player = playerOptional.get();

            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
            if (!(holder instanceof Gui)) {
                return;
            }

            guis.remove(holder);
            player.closeInventory();
        }

        /**
         * Reacts to an {@link InventoryCloseEvent}.
         *
         * @param gui The gui to open
         */
        private void reactToClose(Gui gui) {
            Optional<Player> playerOptional = getPlayer();
            if (!playerOptional.isPresent()) {
                return;
            }

            Player player = playerOptional.get();

            // Special behaviour is defined
            if (specialCloseBehaviour != null) {
                boolean stopHere = specialCloseBehaviour.apply(gui);

                // reset it!
                specialCloseBehaviour = null;

                if (stopHere) {
                    return;
                }
            }

            if (gui.isKillMe() || !gui.isReopenOnClose()) {
                removeGui(gui);
                gui.onClose();
                runLater(() -> openNextGui(gui));
            } else {
                // reopen it
                runLater(() -> player.openInventory(gui.getInventory()));
            }
        }

        /**
         * Runs the runnable on the next tick
         *
         * @param runnable The Runnable to run
         */
        private void runLater(Runnable runnable) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }.runTaskLater(PerceiveCore.getInstance(), 2L);
        }

        /**
         * Closes the currently open Inventory for the player
         */
        private void closeCurrentInventory() {
            getPlayer().ifPresent(Player::closeInventory);
        }

        /**
         * @return The Player if he is online
         */
        private Optional<Player> getPlayer() {
            return Optional.ofNullable(Bukkit.getPlayer(playerID));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Gui)) {
            return;
        }

        PlayerGuiData playerData = INSTANCE.getOrCreatePlayerData(event.getPlayer().getUniqueId());
        playerData.reactToClose((Gui) holder);
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inv = e.getInventory();
        if (!(inv.getHolder() instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) inv.getHolder();
        gui.onClick(e);
    }
}
