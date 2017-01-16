package me.ialistannen.bukkitpluginutilities.particle.ticker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A Thread that ticks!
 */
class TickerThread extends AbstractTicker {

    private Thread ticker;
    private volatile AtomicBoolean started = new AtomicBoolean(false);
    private volatile AtomicLong lastTick = new AtomicLong(System.nanoTime());

    /**
     * @param sleepMillis the time to sleep
     */
    TickerThread(long sleepMillis) {
        super(TimeUnit.MILLISECONDS.toNanos(sleepMillis));
    }

    @Override
    protected void recreate() {
        if (ticker != null) {
            ticker.interrupt();
        }
        ticker = new Thread() {
            @Override
            public void run() {
                while (isStarted() && !isInterrupted()) {
                    long elapsed = System.nanoTime() - lastTick.get();

                    tickableSet.forEach(tickable -> tickable.tick(elapsed));

                    lastTick.set(System.nanoTime());

                    try {
                        Thread.sleep(getDelay().toMillis());
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        ticker.start();
    }

    @Override
    public Ticker startTicker() {
        started.set(true);
        lastTick.set(System.nanoTime());
        recreate();
        return this;
    }

    @Override
    public Ticker stopTicker() {
        started.set(false);
        return this;
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }
}
