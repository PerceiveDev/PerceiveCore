package me.ialistannen.bukkitpluginutilities.gui.anvil;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.ialistannen.bukkitpluginutilities.coreplugin.CorePlugin;
import me.ialistannen.bukkitpluginutilities.gui.anvil.AnvilClickEvent.AnvilSlot;
import me.ialistannen.bukkitpluginutilities.packet.PacketAdapter;
import me.ialistannen.bukkitpluginutilities.packet.PacketEvent;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil;

/**
 * An anvil packet listener
 */
public class AnvilPacketListener extends PacketAdapter {

    private static final Class<?> TARGET_CLASS = ReflectionUtil
            .getClass("{nms}.PacketPlayInWindowClick")
            .orElseThrow(() -> new RuntimeException("Couldn't find NMS class 'PacketPlayInWindowClick'"));

    @Override
    public void onPacketReceived(PacketEvent packetEvent) {
        if (!packetEvent.getPacket().getPacketClass().equals(TARGET_CLASS)) {
            return;
        }
        WindowClickWrapper clickWrapper = new WindowClickWrapper(packetEvent.getPacket(), packetEvent.getPlayer());

        Inventory inventory = clickWrapper.getInventory();
        if (inventory.getType() != InventoryType.ANVIL) {
            return;
        }

        if (!(inventory.getHolder() instanceof AnvilInputHolder)) {
            return;
        }

        // No anvil input, let the gui handle it
        if (!clickWrapper.isInsideTopInventory()) {
            return;
        }

        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.getFromSlot(clickWrapper.getRawSlot()),
                packetEvent.getPlayer(), clickWrapper.getInventoryView(), clickWrapper.getItemStack());

        AnvilInputHolder holder = (AnvilInputHolder) inventory.getHolder();
        holder.reactToClick(clickEvent);

        if (clickEvent.isCancelled()) {
            packetEvent.setCancelled(true);
            new BukkitRunnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    packetEvent.getPlayer().updateInventory();
                }
            }.runTask(CorePlugin.getInstance());
        }
    }
}
