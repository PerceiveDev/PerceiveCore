package com.perceivedev.perceivecore.util.ticker;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A skeleton implementation for ticker
 */
public abstract class AbstractTicker implements Ticker {

    private volatile   long          delayNano;
    /**
     * Is a concurrent set
     */
    protected volatile Set<Tickable> tickableSet;

    /**
     * Creates a new AbstractTicker
     * <p>
     * Does not call {@link #recreate()}
     *
     * @param delayNano The delay in nanoseconds
     */
    protected AbstractTicker(long delayNano) {
        this.delayNano = delayNano;
        tickableSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    /**
     * Recreates this ticker with a new delay.
     * <p>
     * <b>Does not start it!</b>
     * <p>
     * Doesn't need to stop it too.
     */
    protected abstract void recreate();

    @Override
    public void setDelay(long delay, TimeUnit unit) {
        delayNano = TimeUnit.NANOSECONDS.convert(delay, unit);
        stopTicker();
        recreate();
        startTicker();
    }

    @Override
    public long getDelayNano() {
        return delayNano;
    }

    @Override
    public Duration getDelay() {
        return Duration.ofNanos(getDelayNano());
    }

    @Override
    public boolean addTickable(Tickable tickable) {
        return tickableSet.add(tickable);
    }

    @Override
    public void removeTickable(Tickable tickable) {
        tickableSet.remove(tickable);
    }

    @Override
    public void removeAllTickables() {
        tickableSet.clear();
    }
}
