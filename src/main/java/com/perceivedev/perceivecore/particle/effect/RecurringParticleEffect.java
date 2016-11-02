package com.perceivedev.perceivecore.particle.effect;

import java.util.Objects;

import org.bukkit.Location;

import com.perceivedev.perceivecore.util.ticker.StandardTicker;
import com.perceivedev.perceivecore.util.ticker.Ticker;
import com.perceivedev.perceivecore.util.ticker.Ticker.Tickable;

/** A particle effect that replays itself */
public abstract class RecurringParticleEffect implements ParticleEffect, Tickable {

    private Ticker   ticker;
    private Location center;

    /**
     * Adds this {@link Tickable} too
     *
     * @param ticker The ticker to use
     */
    public RecurringParticleEffect(Ticker ticker, Location center) {
        Objects.requireNonNull(center, "center location can't be null");
        Objects.requireNonNull(ticker, "ticker can't be null");

        this.ticker = ticker;
        this.center = center.clone();

        ticker.addTickable(this);
    }

    /**
     * Creates this particle effect.
     * <p>
     * Uses {@link StandardTicker#BUKKIT_SYNC_RUNNABLE}
     */
    public RecurringParticleEffect(Location center) {
        this(StandardTicker.BUKKIT_SYNC_RUNNABLE.createUnstartedTicker(), center);
    }

    /**
     * Returns the current center
     *
     * @return The current center
     */
    protected Location getCenter() {
        return center;
    }

    /**
     * Sets the new center
     *
     * @param center The new center
     */
    protected void setCenter(Location center) {
        this.center = center;
    }

    /** Stops this particle effect */
    public void stop() {
        ticker.stopTicker();
    }

    /** Starts this particle effect */
    public void start(Location center) {
        ticker.startTicker();
    }
}
