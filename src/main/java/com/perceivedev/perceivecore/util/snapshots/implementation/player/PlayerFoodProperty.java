package com.perceivedev.perceivecore.util.snapshots.implementation.player;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.util.snapshots.SnapshotProperty;

/**
 * A property for the player food
 */
public class PlayerFoodProperty extends SnapshotProperty<Player> {

    private int foodLevel = -1;
    private float saturation = -1;

    @Override
    public void restoreFor(Player target) {
        throwUninitializedIfTrue(foodLevel < 0 || saturation < 0);

        target.setFoodLevel(foodLevel);
        target.setSaturation(saturation);
    }

    @Override
    public SnapshotProperty<Player> update(Player target) {
        foodLevel = target.getFoodLevel();
        saturation = target.getSaturation();

        return this;
    }

    @Override
    public SnapshotProperty<Player> createForTarget(Player target) {
        return new PlayerFoodProperty().update(target);
    }
}
