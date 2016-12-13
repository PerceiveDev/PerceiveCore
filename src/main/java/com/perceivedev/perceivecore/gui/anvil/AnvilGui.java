package com.perceivedev.perceivecore.gui.anvil;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.Gui;
import com.perceivedev.perceivecore.gui.anvil.AnvilClickEvent.AnvilSlot;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.AnchorPane;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.packet.PacketManager;

/**
 * A Gui taking input by allowing the user to write something in an Anvil
 */
public class AnvilGui extends Gui implements AnvilInputHolder {

    private static AnvilPacketListener listener = new AnvilPacketListener();

    private Consumer<Optional<String>> callback;

    /**
     * @param name The name of the Gui
     */
    public AnvilGui(String name, Consumer<Optional<String>> callback) {
        super(name, 1, new AnchorPane(3, 1));

        setInventory(Bukkit.createInventory(this, InventoryType.ANVIL, name));

        Objects.requireNonNull(callback, "callback can not be null!");
        this.callback = callback;
    }

    @Override
    public void open(Player player) {
        PacketManager.getInstance().addListener(listener, player);
        super.open(player);
    }

    @Override
    protected void onClose() {
        getPlayer().ifPresent(player -> PacketManager.getInstance().removeListener(listener, player));
    }

    /**
     * Adds an item to the Gui
     *
     * @param slot The slot of the item
     * @param itemStack The {@link ItemStack} to add
     * @param movable Whether the item should be movable by the player. The
     *            output is NEVER movable
     */
    public void setItem(AnvilSlot slot, ItemStack itemStack, boolean movable) {
        Objects.requireNonNull(slot, "slot can not be null!");

        getRootAsFixedPosition().addComponent(new Button(itemStack, clickEvent -> {
            if (movable) {
                clickEvent.setCancelled(false);
            }
        }, Dimension.ONE), slot.getSlot(), 0);
    }

    /**
     * @param event The {@link AnvilClickEvent}
     */
    @Override
    public void reactToClick(AnvilClickEvent event) {
        ItemStack involvedItem = event.getInvolvedItem();
        Optional<String> name;

        if (involvedItem == null || involvedItem.getType() == Material.AIR || !involvedItem.hasItemMeta()
                || !involvedItem.getItemMeta().hasDisplayName()) {
            name = Optional.empty();
        } else {
            name = Optional.ofNullable(involvedItem.getItemMeta().getDisplayName());
        }

        close();
        callback.accept(name);
    }
}
