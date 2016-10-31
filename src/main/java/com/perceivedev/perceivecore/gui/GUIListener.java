/**
 *
 */
package com.perceivedev.perceivecore.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.guireal.Gui;
import com.perceivedev.perceivecore.guireal.components.implementation.component.Button;
import com.perceivedev.perceivecore.guireal.components.implementation.pane.AnchorPane;
import com.perceivedev.perceivecore.guireal.components.implementation.pane.GridPane;
import com.perceivedev.perceivecore.guireal.util.Dimension;
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
            Gui gui = createGui("hello", event.getPlayer(), false);
            PerceiveCore.getInstance().getGuiManager().addGui(event.getPlayer().getUniqueId(), gui);

            gui = createGui("NOPE", event.getPlayer(), true);
            PerceiveCore.getInstance().getGuiManager().addGui(event.getPlayer().getUniqueId(), gui);

            gui = createGui("OH YEA", event.getPlayer(), false);
            PerceiveCore.getInstance().getGuiManager().addGui(event.getPlayer().getUniqueId(), gui);

            PerceiveCore.getInstance().getGuiManager().openCurrentGui(event.getPlayer().getUniqueId());
        }
    }

    private Gui createGui(String name, Player player, boolean reopen) {
        Gui gui = new Gui(name, 4);
        AnchorPane rootAnchorPane = (AnchorPane) gui.getRootPane();

        rootAnchorPane.addComponent(new Button(ItemFactory.builder(Material.LAVA_BUCKET).setName("&3&lBUKKIT").build(),
                e -> System.out.println(name + " Buckit to the rescue!!"), new Dimension(3, 1)), 3, 0);

        AnchorPane paneOne = new AnchorPane(9, 3);
        paneOne.addComponent(new Button(ItemFactory.builder(Material.STONE_BUTTON).setName("&c&lApple").build(),
                e -> System.out.println(name + " ACTION!"), new Dimension(9, 2)), 0, 0);

        GridPane paneTwo = new GridPane(8, 1, 4, 1);
        paneTwo.addComponent(new Button(ItemFactory.builder(Material.GOLD_INGOT).setName("&6&lBUTTER").build(),
                e -> System.out.println(name + " BUTTER!"), new Dimension(1, 1)), 1, 0);

        Button button = new Button(ItemFactory.builder(Material.IRON_INGOT).setName("&7&lIRON").build(),
                e -> System.out.println(name + " IRON!"), new Dimension(1, 1));
        button.setAction(e -> {
            System.out.println(name + " IRON!");

            if (button.getItemStack().getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                button.setItemStack(ItemFactory.builder(button.getItemStack())
                        .removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            } else {
                button.setItemStack(ItemFactory.builder(button.getItemStack())
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
            }
            gui.reRender();
        });
        paneTwo.addComponent(button, 0, 0);

        paneTwo.addComponent(new Button(ItemFactory.builder(Material.BARRIER).setName("&4&lCLOSE").build(), e -> {
            System.out.println(name + " CLOSING!");
            gui.close();
        }, new Dimension(1, 1)), 2, 0);

        paneOne.addComponent(paneTwo, 0, 2);

        rootAnchorPane.addComponent(paneOne, 0, 1);

        gui.setReopenOnClose(reopen);
        return gui;
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
        gui.onClick(e);
    }

    // @EventHandler
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
