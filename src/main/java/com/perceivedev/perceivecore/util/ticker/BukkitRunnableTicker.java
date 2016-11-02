package com.perceivedev.perceivecore.util.ticker;

import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitRunnable;

/** Uses a BukkitRunnable to tick */
abstract class BukkitRunnableTicker extends AbstractTicker {

    BukkitRunnable  runnable;
    private long    lastTick = System.nanoTime();
    private boolean started  = false;

    /**
     * Creates a new AbstractTicker
     * <p>
     * Calls {@link #recreate()}
     *
     * @param tickDelay The delay in ticks
     */
    BukkitRunnableTicker(long tickDelay) {
        super(TimeUnit.MILLISECONDS.toNanos(tickDelay * 50));
    }

    @Override
    protected void recreate() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                long elapsed = System.nanoTime() - lastTick;
                tickableSet.forEach(tickable -> tickable.tick(elapsed));
                lastTick = System.nanoTime();
            }
        };
    }

    @Override
    public Ticker startTicker() {
        if (isStarted()) {
            throw new IllegalStateException("already started!");
        }

        // runnable can only be scheduled once
        recreate();

        lastTick = System.nanoTime();
        long delayInTicks = Math.round(getDelay().toMillis() / 50d);
        startRunnable(delayInTicks);
        started = true;

        return this;
    }

    /**
     * Starts the runnable
     *
     * @param ticksDelay The delay in ticks
     */
    protected abstract void startRunnable(long ticksDelay);

    @Override
    public Ticker stopTicker() {
        if (!isStarted()) {
            return this;
        }

        runnable.cancel();
        started = false;

        return this;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
