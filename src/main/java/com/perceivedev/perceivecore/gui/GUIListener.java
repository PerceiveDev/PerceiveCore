/**
 *
 */
package com.perceivedev.perceivecore.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.guireal.Gui;
import com.perceivedev.perceivecore.guireal.components.implementation.component.Button;
import com.perceivedev.perceivecore.guireal.components.implementation.pane.AnchorPane;
import com.perceivedev.perceivecore.guireal.components.implementation.pane.GridPane;
import com.perceivedev.perceivecore.guisystem.util.Dimension;
import com.perceivedev.perceivecore.util.ItemFactory;

/**
 * @author Rayzr
 */
public class GUIListener implements Listener {

    /**
     * @param plugin
     */
    public GUIListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Gui gui = new Gui("Hello", 4);
            AnchorPane rootAnchorPane = (AnchorPane) gui.getRootPane();

            rootAnchorPane.addComponent(new Button(
                      ItemFactory.builder(Material.LAVA_BUCKET)
                                .setName("&3&lBUKKIT")
                                .build(),
                      () -> System.out.println("Buckit to the rescue!!"),
                      new Dimension(3, 1)
            ), 3, 0);

            AnchorPane paneOne = new AnchorPane(9, 3);
            paneOne.addComponent(new Button(
                      ItemFactory.builder(Material.STONE_BUTTON)
                                .setName("&c&lApple")
                                .build(),
                      () -> System.out.println("ACTION!"),
                      new Dimension(9, 2)
            ), 0, 0);

            GridPane paneTwo = new GridPane(8, 1, 2, 1);
            paneTwo.addComponent(new Button(
                      ItemFactory.builder(Material.GOLD_INGOT)
                                .setName("&6&lBUTTER")
                                .build(),
                      () -> System.out.println("BUTTER!"),
                      new Dimension(1, 1)
            ), 1, 0);
            paneTwo.addComponent(new Button(
                      ItemFactory.builder(Material.IRON_INGOT)
                                .setName("&7&lIRON")
                                .build(),
                      () -> System.out.println("IRON!"),
                      new Dimension(1, 1)
            ), 0, 0);

            paneOne.addComponent(paneTwo, 0, 2);

            rootAnchorPane.addComponent(paneOne, 0, 1);
            gui.open(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteractF(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inv = e.getInventory();
        if (!(inv.getHolder() instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) inv.getHolder();
        System.out.println("Passing it on!");
        gui.onClick(e);
        System.out.println("  ");
    }

    //@EventHandler
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
