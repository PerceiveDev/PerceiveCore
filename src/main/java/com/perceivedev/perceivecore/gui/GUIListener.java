/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

/**
 * @author Rayzr
 *
 */
public class GUIListener implements Listener {

    /**
     * @param plugin
     */
    public GUIListener(Plugin plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent e) {

	if (!(e.getWhoClicked() instanceof Player)) {
	    return;
	}

	Inventory inv = e.getInventory();
	if (!(inv.getHolder() instanceof GUIHolder)) {
	    return;
	}

	GUIHolder holder = (GUIHolder) inv.getHolder();
	if (e.getRawSlot() >= holder.getInventory().getSize()) {
	    return;
	}

	Player p = (Player) e.getWhoClicked();

	e.setCancelled(true);

	holder.handleClick(p, e);

    }

}
