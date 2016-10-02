package com.perceivedev.perceivecore.guisystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Manages the Guis for players
 */
public class PlayerGuiManager implements Listener {

    private Map<UUID, Stack<Stage>> stageMap = new HashMap<>();

    public void addPlayer(UUID uuid, Stage stage) {
        Stack<Stage> stages = stageMap.getOrDefault(uuid, new Stack<>());
        stages.add(stage);
        stageMap.put(uuid, stages);
    }

    public void openPlayersGui(UUID uuid) {
        if (!contains(uuid)) {
            return;
        }
        Stage stage = stageMap.get(uuid).get(0);
        stage.open();
    }

    /**
     * Checks if the UUID is in the manager
     *
     * @param uuid The UUID to check
     *
     * @return True if the UUID is in this manager
     */
    public boolean contains(UUID uuid) {
        return stageMap.containsKey(uuid);
    }

    //<editor-fold desc="Listeners">

    /***************************************************************************
     *                                                                         *
     *                               Listener                                  *
     *                                                                         *
     **************************************************************************/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        System.out.println("Clicked: " + event.getWhoClicked().getName());
        // check if the player has a GUI open
        if (!stageMap.containsKey(event.getWhoClicked().getUniqueId())) {
            return;
        }
        System.out.println("Went through!");
        Stage stage = stageMap.get(event.getWhoClicked().getUniqueId()).get(0);
        stage.onClick(event);
        System.out.println("Cancelled: " + event.isCancelled());
    }
    //</editor-fold>

}
