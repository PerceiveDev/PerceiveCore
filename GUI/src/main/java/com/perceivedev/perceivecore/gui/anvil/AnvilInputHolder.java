package com.perceivedev.perceivecore.gui.anvil;

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

    /**
     * @param event The {@link AnvilTypeEvent}
     */
    void reactToTyping(AnvilTypeEvent event);
}
