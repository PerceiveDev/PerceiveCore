package com.perceivedev.bukkitpluginutilities.particle.effect;

import java.util.Objects;

import org.bukkit.Location;

import com.perceivedev.bukkitpluginutilities.particle.ticker.StandardTicker;
import com.perceivedev.bukkitpluginutilities.particle.ticker.Ticker;

/**
 * A particle effect that replays itself
 */
public abstract class RecurringParticleEffect implements ParticleEffect, Ticker.Tickable {

    private Ticker ticker;
    private Location center;
    private double granularity;

    /**
     * Adds this {@link Ticker.Tickable} too
     *
     * @param ticker The ticker to use
     * @param center The center location
     * @param granularity The granularity. The granularity may be the distance
     * between each spawned particle
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
     * between each spawned particle
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public void setGranularity(double granularity) {
        this.granularity = granularity;
    }

    /**
     * Returns the current ticker
     *
     * @return The ticker used
     */
    @SuppressWarnings("unused")
    public Ticker getTicker() {
        return ticker;
    }

    /**
     * Stops this particle effect
     */
    @SuppressWarnings("unused")
    public void stop() {
        ticker.stopTicker();
    }

    /**
     * Starts this particle effect
     */
    @SuppressWarnings("unused")
    public void start() {
        ticker.startTicker();
    }
}
