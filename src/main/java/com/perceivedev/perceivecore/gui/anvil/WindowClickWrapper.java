package com.perceivedev.perceivecore.gui.anvil;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.OBC;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.packet.Packet;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/**
 * A wrapper for the {@code PacketPlayInWindowClick} class
 */
class WindowClickWrapper {

    private static final Class<?> CRAFT_ITEM_STACK_CLASS = ReflectionUtil
            .getClass(OBC, "inventory.CraftItemStack")
            .orElseThrow(() -> new RuntimeException("Couldn't find class 'CraftItemStack'"));
    private static final Method TO_BUKKIT = ReflectionUtil.getMethod(CRAFT_ITEM_STACK_CLASS, new MethodPredicate()
            .withName("asBukkitCopy").withModifiers(ReflectionUtil.Modifier.PUBLIC, ReflectionUtil.Modifier.STATIC))
            .get()
            .orElseThrow(() -> new RuntimeException("Couldn't find method 'CraftItemStack#asBukkitCopy'"));

    private Packet packet;
    private Player player;

    /**
     * @param packet The packet
     * @param player The Player
     */
    public WindowClickWrapper(Packet packet, Player player) {
        this.packet = packet;
        this.player = player;
    }

    /**
     * @return The {@link InventoryView} of the Player
     */
    InventoryView getInventoryView() {
        return player.getOpenInventory();
    }

    /**
     * @return The Top inventory
     */
    Inventory getInventory() {
        return player.getOpenInventory().getTopInventory();
    }

    /**
     * @return The involved ItemStack, or null if none
     */
    ItemStack getItemStack() {
        ReflectResponse<Object> itemResponse = packet.get("item");
        if (!itemResponse.isSuccessful()) {
            throw new RuntimeException("PacketPlayInWindowClick has a bad format!" +
                    " 'slot' not found!");
        }

        Object item = itemResponse.getValue();
        if (item == null) {
            return null;
        }
        return (ItemStack) ReflectionUtil.invokeMethod(TO_BUKKIT, null, item)
                .get()
                .orElseThrow(() -> new RuntimeException("Error invoking 'asBukkitCopy' method"));
    }

    /**
     * @return True if the click was inside the top inventory
     */
    boolean isInsideTopInventory() {
        int rawSlot = getRawSlot();
        return rawSlot >= 0 && rawSlot < getInventoryView().getTopInventory().getSize();
    }

    /**
     * @return The packet slot that was clicked
     */
    int getRawSlot() {
        return (int) packet.get("slot")
                .get()
                .orElseThrow(() -> new RuntimeException("PacketPlayInWindowClick has a bad format!" +
                        " 'slot' not found!"));
    }
}
