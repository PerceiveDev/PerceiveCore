package com.perceivedev.bukkitpluginutilities.particle.ticker;

import java.util.function.Supplier;

import com.perceivedev.bukkitpluginutilities.coreplugin.CorePlugin;

/**
 * Holds some standard implementations of {@link Ticker}
 */
public enum StandardTicker {

    /**
     * Uses the Bukkit scheduler to dispatch a Sync runnable
     */
    BUKKIT_SYNC_RUNNABLE(() -> new BukkitRunnableTicker(5) {
        @Override
        protected void startRunnable(long ticksDelay) {
            runnable.runTaskTimer(CorePlugin.getInstance(), 0, ticksDelay);
        }
    }),
    /**
     * Uses the Bukkit scheduler to dispatch an async runnable <b>Be
     * careful!</b>
     */
    ASYNC_BUKKIT_RUNNABLE(() -> new BukkitRunnableTicker(5) {
        @Override
        protected void startRunnable(long ticksDelay) {
            runnable.runTaskTimerAsynchronously(CorePlugin.getInstance(), 0, ticksDelay);
        }
    }),
    /**
     * Creates a new Thread. As async as it gets. Be careful!
     */
    THREAD(() -> new TickerThread(50));

    private Supplier<Ticker> tickerSupplier;

    /**
     * @param tickerSupplier The ticker supplier
     */
    StandardTicker(Supplier<Ticker> tickerSupplier) {
        this.tickerSupplier = tickerSupplier;
    }

    /**
     * Generates a new <b>not started</b> ticker
     *
     * @return A new ticker of the given type, not started
     */
    public Ticker createUnstartedTicker() {
        return tickerSupplier.get();
    }

    /**
     * Generates a new <b>started</b> ticker
     *
     * @return A new ticker of the given type, already started
     */
    @SuppressWarnings("unused")
    public Ticker createStartedTicker() {
        return tickerSupplier.get().startTicker();
    }
}
