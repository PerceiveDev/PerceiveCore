package com.perceivedev.perceivecore.util.snapshots.implementation.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.util.snapshots.SnapshotProperty;

/**
 * Saves the player's {@link GameMode}
 */
public class PlayerGamemodeProperty extends SnapshotProperty<Player> {

    private GameMode gameMode;

    @Override
    public void restoreFor(Player target) {
        throwUninitializedIfTrue(gameMode == null);

        target.setGameMode(gameMode);
    }

    @Override
    public SnapshotProperty<Player> update(Player target) {
        gameMode = target.getGameMode();
        return this;
    }

    @Override
    public SnapshotProperty<Player> createForTarget(Player target) {
        return new PlayerGamemodeProperty().update(target);
    }
}
