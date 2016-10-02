package com.perceivedev.perceivecore.guisystem;

import static com.perceivedev.perceivecore.util.TextUtils.colorize;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.util.ItemFactory;

/**
 * Created by Julian on 02.10.2016.
 */
public class TestListener implements Listener {

    @EventHandler
    public void onHit(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        Pane pane = new FlowPane(Collections.emptyList(), new Dimension(9, 5));
        pane.addComponent(
                  new ItemComponent(
                            ItemFactory.builder(Material.GRASS)
                                      .setName("&6This is GRASS")
                                      .build()
                  )
        );
        pane.addComponent(
                  new ItemComponent(
                            ItemFactory.builder(Material.GLASS)
                                      .setName("&bThis is GLASS")
                                      .build()
                  )
        );
        Scene scene = new Scene(pane, Bukkit.createInventory(null, 9 * 5));
        Stage stage = new Stage(scene, event.getPlayer().getUniqueId());

        PlayerGuiManager playerGuiManager = PerceiveCore.getInstance().getPlayerGuiManager();
        playerGuiManager.addPlayer(event.getPlayer().getUniqueId(), stage);
        playerGuiManager.openPlayersGui(event.getPlayer().getUniqueId());
    }

    private static class ItemComponent implements Component {

        private ItemStack itemStack;

        public ItemComponent(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public void onClick(InventoryClickEvent clickEvent) {
            clickEvent.getWhoClicked().sendMessage(colorize("You clicked the item: " + itemStack.getItemMeta().getDisplayName()));
            clickEvent.setCancelled(true);
        }

        @Override
        public Dimension getSize() {
            return new Dimension(4, 4);
        }

        @Override
        public void render(Inventory inventory, Player player, int x, int y) {
            for (int tmpY = 0; tmpY < getSize().getHeight(); tmpY++) {
                for (int tmpX = 0; tmpX < getSize().getWidth(); tmpX++) {
                    int slot = gridToSlot(inventory.getSize(), tmpX + x, tmpY + y);
                    inventory.setItem(slot, itemStack);
                }
            }
        }
    }
}
