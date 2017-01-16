package com.perceivedev.bukkitpluginutilities.gui.anvil;

import org.bukkit.inventory.InventoryHolder;

/**
 * A marker interface that states that this inventory is a marker inventory for
 * anvil input
 */
public interface AnvilInputHolder extends InventoryHolder {

    /**
     * @param event The {@link AnvilClickEvent}
     */
    void reactToClick(AnvilClickEvent event);
}
