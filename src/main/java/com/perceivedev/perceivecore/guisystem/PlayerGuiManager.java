package com.perceivedev.perceivecore.guisystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.PerceiveCore;

/**
 * Manages the GUIs for players
 */
public class PlayerGuiManager implements Listener {

    private Map<UUID, PlayerStages> stageMap = new HashMap<>();

    /**
     * Adds a Stage
     *
     * @param uuid The {@link UUID} of the player
     * @param stage The Stage to add
     */
    public void addStage(UUID uuid, Stage stage) {
        PlayerStages stages = stageMap.getOrDefault(uuid, new PlayerStages());
        stages.addStage(stage);
        
        stageMap.put(uuid, stages);

        stage.setGuiManager(this);
    }

    /**
     * Removes the stage (and closes it)
     *
     * @param uuid The {@link UUID} of the player
     * @param stage The Stage to remove
     */
    public void removeStage(UUID uuid, Stage stage) {
        if (!containsStageForPlayer(uuid)) {
            return;
        }
        stageMap.get(uuid).removeStage(stage);
    }

    /**
     * Removes the currently opened stage (and closes it)
     *
     * @param uuid The {@link UUID} of the player
     *
     * @see #removeStage(UUID, Stage)
     */
    public void removeOpenedStage(UUID uuid) {
        if (!containsStageForPlayer(uuid)) {
            return;
        }
        stageMap.get(uuid).removeOpenedStage();
    }

    /**
     * Brings the stage to the front. Won't open it though.
     *
     * @param uuid The {@link UUID} of the player
     * @param stage The Stage to bring to the front
     */
    public void bringStageToFront(UUID uuid, Stage stage) {
        if (!containsStageForPlayer(uuid)) {
            return;
        }
        stageMap.get(uuid).bringToFront(stage);
    }

    /**
     * Opens the first Stage
     *
     * @param uuid The {@link UUID} of the player
     */
    public void openFirstStage(UUID uuid) {
        if (!containsStageForPlayer(uuid)) {
            return;
        }
        stageMap.get(uuid).openFirstStage(false);
    }

    /**
     * Returns the opened stage
     *
     * @param uuid The {@link UUID} of the player
     *
     * @return The opened Stage.
     */
    public Optional<Stage> getOpenedStage(UUID uuid) {
        if (!hasStageOpened(uuid)) {
            return Optional.empty();
        }
        return stageMap.get(uuid).getOpenedStage();
    }

    /**
     * Checks if the Player has a Stage opened
     *
     * @param uuid The {@link UUID} of the player
     *
     * @return True if the Player has a Stage open
     */
    public boolean hasStageOpened(UUID uuid) {
        return containsStageForPlayer(uuid) && stageMap.get(uuid).getOpenedStage().isPresent();
    }

    /**
     * Checks if the given player has a Stage in this Manager
     *
     * @param uuid The {@link UUID} of the player
     *
     * @return True if there is a Stage for the given player
     */
    public boolean containsStageForPlayer(UUID uuid) {
        return stageMap.containsKey(uuid) && !stageMap.get(uuid).isEmpty();
    }

    /**
     * Reacts to the closing of a Stage
     *
     * @param uuid The {@link UUID} of the player
     */
    private void reactToClosing(UUID uuid) {
        if (!hasStageOpened(uuid)) {
            return;
        }
        stageMap.get(uuid).reactToClosing();
    }

    //<editor-fold desc="Helper Classes">

    /***************************************************************************
     *                                                                         *
     *                            Helper Classes                               *
     *                                                                         *
     **************************************************************************/

    private static class PlayerStages {
        private Stack<Stage> stages = new Stack<>();
        private Stage openedStage;

        /**
         * Returns the opened stage
         *
         * @return The opened Stage.
         */
        private Optional<Stage> getOpenedStage() {
            return Optional.ofNullable(openedStage);
        }

        /**
         * Checks if the Stage is contained in this Object
         *
         * @param stage The Stage
         *
         * @return True if the stage is in this manager
         */
        private boolean containsStage(Stage stage) {
            return stages.contains(stage);
        }

        /**
         * The stage to add
         *
         * @param stage Adds this stage
         */
        public void addStage(Stage stage) {
            if (containsStage(stage)) {
                return;
            }
            stages.add(stage);
        }

        /**
         * Removes the stage (and closes it)
         *
         * @param stage The Stage to remove
         */
        public void removeStage(Stage stage) {
            stages.remove(stage);

            if (openedStage.equals(stage)) {
                closeCurrentStage();
            }
        }

        /**
         * Removes the currently opened stage (and closes it)
         *
         * @see #removeStage(Stage)
         */
        public void removeOpenedStage() {
            if (openedStage == null) {
                return;
            }
            removeStage(openedStage);
        }

        /**
         * Checks if there are no stages
         *
         * @return True if this is empty
         */
        private boolean isEmpty() {
            return stages.isEmpty();
        }

        /**
         * Opens this stage
         *
         * @param stage The stage to open
         */
        private void openStage(Stage stage) {
            openedStage = stage;
            stage.open();
        }

        /**
         * Opens the first Stage
         */
        private void openFirstStage(boolean delay) {
            if (isEmpty()) {
                return;
            }
            if (delay) {
                openLater(stages.peek());
            } else {
                openStage(stages.peek());
            }
        }

        /**
         * Brings the stage to the front. Won't open it though.
         *
         * @param stage The Stage to bring to the front
         */
        private void bringToFront(Stage stage) {
            if (!containsStage(stage)) {
                return;
            }

            Stage first = stages.pop();
            stages.add(first);

            if (first.equals(stage)) {
                return;
            }

            stages.add(stage);
        }

        /**
         * Closes the currently opened stage
         */
        private void closeCurrentStage() {
            if (openedStage == null) {
                return;
            }
            Stage opened = openedStage;
            openedStage = null;
            opened.close();
        }

        /**
         * Reacts to the closing of a Stage
         */
        private void reactToClosing() {
            if (openedStage == null) {
                // What was this??
                return;
            }
            if (openedStage.isClosable()) {
                removeStage(openedStage);
                openFirstStage(true);
            } else if (stages.peek().equals(openedStage)) {
                // it is still the first stage, so the user closed it. If this is false, it was swapped.
                openLater(openedStage);
            }
        }

        /**
         * Opens the stage after a few ticks.
         *
         * @param stage The stage to open
         */
        private void openLater(Stage stage) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    openStage(stage);
                }
            }.runTaskLater(PerceiveCore.getInstance(), 2L);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">

    /***************************************************************************
     *                                                                         *
     *                               Listener                                  *
     *                                                                         *
     **************************************************************************/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        // check if the player has a GUI open
        if (!hasStageOpened(uuid)) {
            return;
        }
        Optional<Stage> openedStage = stageMap.get(uuid).getOpenedStage();
        openedStage.ifPresent(stage -> stage.onClick(event));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // check if the player has a GUI open
        if (!hasStageOpened(uuid)) {
            return;
        }
        reactToClosing(uuid);
    }
    //</editor-fold>

}
