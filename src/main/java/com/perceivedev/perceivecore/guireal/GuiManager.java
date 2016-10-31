package com.perceivedev.perceivecore.guireal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

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
public class GuiManager implements Listener {
    
    /**
     * Opens the first GUI on the stack for the player. After closing that
     * window, the next GUI on the stack will open, etc. until the stack is
     * empty.
     * 
     * @param player The player to open the GUI for
     * @return If a GUI was actually opened for the player
     */
    public static boolean openFirst(Player player) {
        return PerceiveCore.getInstance().getGuiManager().openCurrentGui(player.getUniqueId());
    }

    private Map<UUID, PlayerGuiData> playerMap = new HashMap<>();

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
     * Removes the Gui, closing it if it was opened
     *
     * @param uuid The {@link UUID} of the player
     * @param gui The {@link Gui} to remove
     */
    public void removeGui(UUID uuid, Gui gui) {
        getOrCreatePlayerData(uuid).removeGui(gui);
    }

    /**
     * Removes ALL {@link Gui}s for the Player, closing any that is opened
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
     * @return True if the Gui was opened, false if an error occurred or it was put back to later open it
     */
    public boolean openCurrentGui(UUID playerID) {
        return getOrCreatePlayerData(playerID).openNextGui();
    }

    /**
     * Adds the Gui and tries to open it
     *
     * @param playerID The {@link UUID} of the player
     * @param gui The gui to open
     *
     * @return True if the Gui was opened
     */
    public boolean submit(UUID playerID, Gui gui) {
        PlayerGuiData playerData = getOrCreatePlayerData(playerID);
        playerData.addGui(gui);
        return playerData.openNextGui();
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
        private UUID       playerID;
        private Stack<Gui> guis;

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
            guis.add(gui);
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
                    // it may be called in the PlayerCloseInventoryEvent, which could introduce bugs without this line
                    runLater(this::closeCurrentInventory);
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
         */
        private boolean openNextGui() {
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

            guis.peek().openInventory(player);

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

            if (gui.isKillMe() || !gui.isReopenOnClose()) {
                removeGui(gui);
                runLater(this::openNextGui);
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

        PlayerGuiData playerData = PerceiveCore.getInstance().getGuiManager().getOrCreatePlayerData(event.getPlayer().getUniqueId());
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
