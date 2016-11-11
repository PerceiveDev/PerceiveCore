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
    private double   granularity;

    /**
     * Adds this {@link Tickable} too
     *
     * @param ticker The ticker to use
     * @param center The center location
     * @param granularity The granularity. The granularity may be the distance
     *            between each spawned particle
     */
    public RecurringParticleEffect(Ticker ticker, Location center, double granularity) {
        Objects.requireNonNull(center, "center location can't be null");
        Objects.requireNonNull(ticker, "ticker can't be null");

        this.ticker = ticker;
        this.center = center.clone();
        this.granularity = granularity;

        ticker.addTickable(this);
    }

    /**
     * Creates this particle effect.
     * <p>
     * Uses {@link StandardTicker#BUKKIT_SYNC_RUNNABLE}
     * 
     * @param center The center location
     * @param granularity The granularity. The granularity may be the distance
     *            between each spawned particle
     */
    public RecurringParticleEffect(Location center, double granularity) {
        this(StandardTicker.BUKKIT_SYNC_RUNNABLE.createUnstartedTicker(), center, granularity);
    }

    /**
     * Returns the current center
     *
     * @return The current center
     */
    protected Location getCenter() {
        return center.clone();
    }

    /**
     * Sets the new center
     *
     * @param center The new center
     */
    protected void setCenter(Location center) {
        this.center = center.clone();
    }

    /**
     * The granularity is the distance between each spawned particle
     *
     * @return The granularity
     */
    public double getGranularity() {
        return granularity;
    }

    /**
     * The granularity is the distance between each spawned particle
     *
     * @param granularity The granularity
     */
    public void setGranularity(double granularity) {
        this.granularity = granularity;
    }

    /**
     * Returns the current ticker
     * 
     * @return The ticker used
     */
    public Ticker getTicker() {
        return ticker;
    }

    /**
     * Stops this particle effect
     */
    public void stop() {
        ticker.stopTicker();
    }

    /**
     * Starts this particle effect
     */
    public void start() {
        ticker.startTicker();
    }
}
