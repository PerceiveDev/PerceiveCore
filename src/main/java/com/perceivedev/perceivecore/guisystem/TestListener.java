package com.perceivedev.perceivecore.guisystem;

import static com.perceivedev.perceivecore.util.TextUtils.colorize;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.guisystem.component.Button;
import com.perceivedev.perceivecore.guisystem.component.Component;
import com.perceivedev.perceivecore.guisystem.component.Label;
import com.perceivedev.perceivecore.guisystem.component.Pane;
import com.perceivedev.perceivecore.guisystem.implementation.AnchorPane;
import com.perceivedev.perceivecore.guisystem.implementation.FlowPane;
import com.perceivedev.perceivecore.guisystem.implementation.GridPane;
import com.perceivedev.perceivecore.guisystem.util.Dimension;
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
        PlayerGuiManager playerGuiManager = PerceiveCore.getInstance().getPlayerGuiManager();

        Pane pane1 = constructGridPane(1, playerGuiManager, event.getPlayer());

        Scene scene = new Scene(pane1, Bukkit.createInventory(null, 9 * 6));
        Stage stage = new Stage(scene, event.getPlayer().getUniqueId(), true);

        Stage stageTwo = new Stage(
                  new Scene(
                            constructPane(1, playerGuiManager, event.getPlayer()), Bukkit.createInventory(null, 9 * 5)
                  ),
                  event.getPlayer().getUniqueId(),
                  false
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                scene.setPane(constructGridPane(2, playerGuiManager, event.getPlayer()));
            }
        }.runTaskLater(PerceiveCore.getInstance(), 20L * 2);

        UUID uuid = event.getPlayer().getUniqueId();

        playerGuiManager.addStage(uuid, stageTwo);
        playerGuiManager.addStage(uuid, stage);

        playerGuiManager.openFirstStage(uuid);
    }

    private Pane constructGridPane(int number, PlayerGuiManager playerGuiManager, Player player) {
        GridPane pane = new GridPane(new Dimension(9, 6), 3, 3);
        System.out.println("Grid size: " + pane.getGridSize());

        pane.addComponent(
                  new Label(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(number == 1 ? DyeColor.YELLOW : DyeColor.BROWN)
                                      .setName((number == 1 ? ChatColor.GRAY : ChatColor.BLUE) + "Placeholder")
                                      .build(),
                            new Dimension(3, 2)
                  ),
                  0,
                  0
        );
        pane.addComponent(
                  new Label(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(number == 1 ? DyeColor.BLACK : DyeColor.BLUE)
                                      .setName((number == 1 ? ChatColor.GRAY : ChatColor.BLUE) + "Placeholder")
                                      .build(),
                            new Dimension(3, 2)
                  ),
                  2,
                  0
        );
        pane.addComponent(
                  new Label(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(number == 1 ? DyeColor.GREEN : DyeColor.CYAN)
                                      .setName((number == 1 ? ChatColor.GRAY : ChatColor.BLUE) + "Placeholder")
                                      .build(),
                            new Dimension(3, 2)
                  ),
                  0,
                  2
        );
        pane.addComponent(
                  new Label(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(number == 1 ? DyeColor.BLACK : DyeColor.GRAY)
                                      .setName((number == 1 ? ChatColor.GRAY : ChatColor.BLUE) + "Placeholder")
                                      .build(),
                            new Dimension(3, 2)
                  ),
                  2,
                  2
        );

        // center: 1,1
        Pane centerPane = new FlowPane(new Dimension(3, 2));
        centerPane.addComponent(
                  new Button(
                            ItemFactory.builder(Material.GRASS)
                                      .setName(number + "&6This is GRASS")
                                      .build(),
                            () -> player.sendMessage(
                                      colorize(number + " You clicked the item: &6This is GRASS")
                            ),
                            new Dimension(3, 1)
                  )
        );
        centerPane.addComponent(
                  new Button(
                            ItemFactory.builder(Material.GLASS)
                                      .setName("&bThis is GLASS")
                                      .build(),
                            () -> player.sendMessage(
                                      colorize(number + " You clicked the item: &bThis is GLASS")
                            ),
                            new Dimension(3, 1)
                  )
        );
        pane.addComponent(centerPane, 1, 1);

        if (number == 1) {
            pane.removeComponent(0, 0);
        } else {
            pane.removeComponent(2, 2);
        }

        return pane;
    }

    private Pane constructPane(int number, PlayerGuiManager playerGuiManager, Player player) {
        AnchorPane pane = new AnchorPane(Collections.emptyList(), new Dimension(9, 5));
        //        Pane pane = new FlowPane(new Dimension(9, 5));
        pane.addComponent(
                  new Label(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(number == 1 ? DyeColor.BLACK : DyeColor.BLUE)
                                      .setName((number == 1 ? ChatColor.GRAY : ChatColor.BLUE) + "Placeholder")
                                      .build(),
                            new Dimension(9, 1)
                  ),
                  0,
                  0
        );
        pane.addComponent(
                  new DummyComp(
                            ItemFactory.builder(Material.STAINED_GLASS_PANE)
                                      .setColour(DyeColor.GRAY)
                                      .setName(ChatColor.GRAY + "Placeholder")
                                      .build(),
                            () -> System.out.println("I am a dummy too! " + number),
                            new Dimension(1, 4)
                  ),
                  0,
                  1
        );
        pane.addComponent(
                  new Button(
                            ItemFactory.builder(Material.GRASS)
                                      .setName(number + "&6This is GRASS")
                                      .build(),
                            () -> player.sendMessage(
                                      colorize(number + " You clicked the item: &6This is GRASS")
                            ),
                            new Dimension(3, 3)
                  ),
                  1,
                  1
        );
        pane.addComponent(
                  new Button(
                            ItemFactory.builder(Material.GLASS)
                                      .setName("&bThis is GLASS")
                                      .build(),
                            () -> player.sendMessage(
                                      colorize(number + " You clicked the item: &bThis is GLASS")
                            ),
                            new Dimension(3, 3)
                  ),
                  4,
                  1
        );
        pane.addComponent(
                  new Button(
                            ItemFactory.builder(Material.BARRIER)
                                      .setName("&6Go away!")
                                      .build(),
                            () -> playerGuiManager.removeOpenedStage(player.getUniqueId()),
                            new Dimension(1, 1)
                  ),
                  4,
                  4
        );
        return pane;
    }

    private static class ItemComponent implements Component {

        private   ItemStack itemStack;
        protected Runnable  runnable;

        public ItemComponent(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemComponent(ItemStack itemStack, Runnable runnable) {
            this.itemStack = itemStack;
            this.runnable = runnable;
        }

        @Override
        public void onClick(InventoryClickEvent clickEvent) {
            if (itemStack.getType() == Material.BARRIER) {
                if (runnable != null) {
                    runnable.run();
                }
            } else {
                clickEvent.getWhoClicked().sendMessage(colorize("You clicked the item: " + itemStack.getItemMeta().getDisplayName()));
            }
            clickEvent.setCancelled(true);
        }

        @Override
        public Dimension getSize() {
            return itemStack.getType() == Material.BARRIER ? new Dimension(1, 1) : new Dimension(4, 4);
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

    private static class DummyComp extends ItemComponent {
        private Dimension dimension;

        public DummyComp(ItemStack itemStack, Runnable runnable, Dimension dimension) {
            super(itemStack, runnable);
            this.dimension = dimension;
        }

        @Override
        public Dimension getSize() {
            return dimension;
        }

        @Override
        public void onClick(InventoryClickEvent clickEvent) {
            runnable.run();
            clickEvent.setCancelled(true);
        }
    }
}
