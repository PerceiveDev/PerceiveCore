package me.ialistannen.bukkitpluginutilities.gui.anvil;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.ialistannen.bukkitpluginutilities.gui.Gui;
import me.ialistannen.bukkitpluginutilities.gui.anvil.AnvilClickEvent.AnvilSlot;
import me.ialistannen.bukkitpluginutilities.gui.components.Button;
import me.ialistannen.bukkitpluginutilities.gui.components.panes.AnchorPane;
import me.ialistannen.bukkitpluginutilities.gui.util.Dimension;
import me.ialistannen.bukkitpluginutilities.packet.PacketManager;
import me.ialistannen.bukkitpluginutilities.utilities.item.ItemFactory;


/**
 * A Gui taking input by allowing the user to write something in an Anvil
 */
public class AnvilGui extends Gui implements AnvilInputHolder {

    private static AnvilPacketListener listener = new AnvilPacketListener();

    private Consumer<Optional<String>> callback;

    /**
     * It will add a Paper with the name " " as the default item.
     *
     * @param name The name of the Gui
     * @param callback The callback
     */
    @SuppressWarnings("unused")
    public AnvilGui(String name, Consumer<Optional<String>> callback) {
        super(name, 1, new AnchorPane(3, 1));

        setInventory(Bukkit.createInventory(this, InventoryType.ANVIL, name));

        Objects.requireNonNull(callback, "callback cannot be null!");
        this.callback = callback;

        // Set the item to a paper with a space. This makes it actually work as
        // an input (Can be any item)
        setItem(AnvilSlot.INPUT_LEFT,
                ItemFactory.builder(Material.PAPER).setName(" ").build(),
                false);
    }

    /**
     * Called when this gui is displayed to a player
     * <p>
     * {@link #getPlayer()} is already set at this point
     * <p>
     * <br>
     * <strong><em>Must be called by sub classes, or the Gui WILL NOT
     * WORK</em></strong>
     *
     * @param previous The previous Gui that was displayed. {@code null} if this
     * is the first
     */
    @Override
    protected void onDisplay(Gui previous) {
        getPlayer().ifPresent(player -> PacketManager.getInstance().addListener(listener, player));
    }

    /**
     * Called when the Gui is closed. You may overwrite it to listen to close
     * events
     * <p>
     * <br>
     * <strong><em>Must be called by sub classes, or the Gui WILL NOT
     * WORK</em></strong>
     */
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
     * output is NEVER movable
     */
    @SuppressWarnings("WeakerAccess")
    public void setItem(AnvilSlot slot, ItemStack itemStack, @SuppressWarnings("SameParameterValue") boolean movable) {
        Objects.requireNonNull(slot, "slot cannot be null!");

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
        }
        else {
            name = Optional.ofNullable(involvedItem.getItemMeta().getDisplayName());
        }

        close();
        callback.accept(name);
    }
}
