package com.perceivedev.perceivecore.utilities.snapshots.implementation.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.utilities.snapshots.SnapshotProperty;

/**
 * Saves the inventory of a player
 */
public class PlayerInventoryProperty extends SnapshotProperty<Player> {

    private List<ItemStack> contents;

    @Override
    public void restoreFor(Player target) {
        throwUninitializedIfTrue(contents == null);

        target.getInventory().setContents(contents.toArray(new ItemStack[contents.size()]));
    }

    @Override
    public SnapshotProperty<Player> update(Player target) {
        ItemStack[] inventoryContents = target.getInventory().getContents();

        contents = new ArrayList<>(inventoryContents.length);

        for (ItemStack itemStack : inventoryContents) {
            contents.add(itemStack != null ? itemStack.clone() : null);
        }

        return this;
    }

    @Override
    public SnapshotProperty<Player> createForTarget(Player target) {
        return new PlayerInventoryProperty().update(target);
    }
}
