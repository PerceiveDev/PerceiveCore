package com.perceivedev.perceivecore.util.ticker;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/** Ticks {@link Tickable}s at some interval :) */
public interface Ticker {

    /**
     * Starts the ticking
     * 
     * @return This ticker
     */
    Ticker startTicker();

    /**
     * Stops the ticking
     * 
     * @return This ticker
     */
    Ticker stopTicker();

    /**
     * Checks if the ticker is started
     *
     * @return True if the ticker is started
     */
    boolean isStarted();

    /**
     * Sets the delay between ticks
     *
     * @param delay The delay between ticks
     * @param unit The unit the delay is in
     */
    void setDelay(long delay, TimeUnit unit);

    /**
     * Returns the delay in Nanoseconds
     *
     * @return The delay in NanoSeconds
     */
    long getDelayNano();

    /**
     * Returns the delay
     *
     * @return The delay
     */
    Duration getDelay();

    /**
     * Adds a {@link Tickable} to be run on tick
     *
     * @param tickable The {@link Tickable} to add
     *
     * @return True if it was added
     */
    boolean addTickable(Tickable tickable);

    /**
     * Removes a {@link Tickable}
     *
     * @param tickable The {@link Tickable} to remove
     */
    void removeTickable(Tickable tickable);

    /** Removes all {@link Tickable}s */
    void removeAllTickables();

    /** Represents a class that can be ticked */
    @FunctionalInterface
    interface Tickable {

        /**
         * Ticks this
         *
         * @param elapsedNanoSeconds The elapsed nano seconds
         */
        void tick(long elapsedNanoSeconds);
    }
}
