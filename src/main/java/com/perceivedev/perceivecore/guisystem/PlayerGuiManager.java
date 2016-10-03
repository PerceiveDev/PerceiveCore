package com.perceivedev.perceivecore.guisystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
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

    private Map<UUID, Stack<Stage>> stageMap = new HashMap<>();

    /**
     * Adds the stage and opens it.
     *
     * @param uuid The UUID of the player
     * @param stage The stage to add. Will be added at the front.
     */
    public void addStage(UUID uuid, Stage stage) {
        Stack<Stage> stages = stageMap.getOrDefault(uuid, new Stack<>());
        stages.add(stage);
        stageMap.put(uuid, stages);

        // inject GuiManager reference
        stage.setGuiManager(this);

        stage.open();
    }

    /**
     * Removes and then closes the current foremost stage for the given Player
     * <p>
     * Without removing it, closing wouldn't really work. Just re-add it if you need it later again
     *
     * @param uuid The UUID of the player
     */
    public void removeOpenGui(UUID uuid) {
        if (!containsAStage(uuid)) {
            return;
        }
        Stack<Stage> stages = stageMap.get(uuid);
        stages.pop().close();

        if (stages.isEmpty()) {
            stageMap.remove(uuid);
        }
    }

    /**
     * Moves a Stage to the front.
     * <p>
     * <u><b>Only if it is already added.</b></u>
     *
     * @param uuid The UUID of the player
     * @param stage The Stage to move to the front
     */
    public void bringStageToFront(UUID uuid, Stage stage) {
        if (!containsAStage(uuid)) {
            return;
        }

        Stack<Stage> stages = stageMap.get(uuid);
        if (!stages.contains(stage)) {
            return;
        }

        Optional<Stage> openedStage = getOpenedStage(uuid);
        // this contradicts the containsAStage method call at the top, so the suppress is okay
        //noinspection OptionalGetWithoutIsPresent
        Stage opened = openedStage.get();

        // removes the opened gui
        removeOpenGui(uuid);

        // re-add it, but without opening
        stages.add(opened);

        // remove the to-be-front stage
        stages.remove(stage);

        // re-add it (now at the top)
        stages.add(stage);

        stage.open();
    }

    /**
     * Checks if the UUID is in the manager and at least one stage is added
     *
     * @param uuid The UUID to check
     *
     * @return True if the UUID is in this manager and at least one stage is added
     */
    private boolean containsAStage(UUID uuid) {
        return stageMap.containsKey(uuid) && !stageMap.get(uuid).isEmpty();
    }

    /**
     * Checks if the UUID is in the manager and at least one stage is added
     *
     * @param humanEntity The {@link HumanEntity} to check
     *
     * @return True if the UUID is in this manager and at least one stage is added
     */
    private boolean containsAStage(HumanEntity humanEntity) {
        return containsAStage(humanEntity.getUniqueId());
    }

    /**
     * Returns the opened stage for a player
     *
     * @param uuid The UUID of the player
     *
     * @return The opened stage
     */
    public Optional<Stage> getOpenedStage(UUID uuid) {
        if (!containsAStage(uuid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(stageMap.get(uuid).peek());
    }

    /**
     * Closes and removes all stages for the current player
     *
     * @param uuid The UUID of the player
     */
    public void removePlayer(UUID uuid) {
        removeOpenGui(uuid);
        stageMap.remove(uuid);
    }

    //<editor-fold desc="Listeners">

    /***************************************************************************
     *                                                                         *
     *                               Listener                                  *
     *                                                                         *
     **************************************************************************/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check if the player has a GUI open
        if (!containsAStage(event.getWhoClicked())) {
            return;
        }
        Stage stage = stageMap.get(event.getWhoClicked().getUniqueId()).get(0);
        stage.onClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // check if the player has a GUI open
        if (!containsAStage(event.getPlayer())) {
            return;
        }
        Stage stage = stageMap.get(event.getPlayer().getUniqueId()).peek();

        // he closed it, remove it from the Gui stack to remove it code wise
        if (stage.isClosable()) {
            removeOpenGui(event.getPlayer().getUniqueId());
            return;
        }

        // run it later as you can't open the inventory in the same tick. You must wait one or it won't work.
        new BukkitRunnable() {
            @Override
            public void run() {
                stage.open();
            }
        }.runTaskLater(PerceiveCore.getInstance(), 1L);
    }
    //</editor-fold>

}
