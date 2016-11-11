package com.perceivedev.perceivecore.particle.effect.shapes;

import static java.lang.Math.PI;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.perceivedev.perceivecore.particle.effect.RecurringParticleEffect;
import com.perceivedev.perceivecore.util.ticker.StandardTicker;
import com.perceivedev.perceivecore.util.ticker.Ticker;
import com.perceivedev.perceivecore.util.ticker.Ticker.Tickable;

/**
 * A Yin-Yang effect
 */
public class YinYang extends RecurringParticleEffect {

    private double time;
    private double radBlack, speedWhite;
    private double whiteOffsetRadian = Math.PI / 2.5;

    /**
     * Adds this {@link Tickable} too
     *
     * @param ticker The ticker to use
     * @param center The center location
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param speedWhite The speed of the white particles. 0.4 looks nice.
     * @param radBlack The radius of the black particles
     */
    public YinYang(Ticker ticker, Location center, double granularity, double speedWhite, double radBlack) {
        super(ticker, center, granularity);

        this.radBlack = radBlack;
        this.speedWhite = speedWhite;
    }

    /**
     * Creates this particle effect.
     * <p>
     * Uses {@link StandardTicker#BUKKIT_SYNC_RUNNABLE}
     * 
     * @param center The center location
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param speedWhite The speed of the white particles. 0.4 looks nice.
     * @param radBlack The radius of the black particles
     */
    public YinYang(Location center, double granularity, double speedWhite, double radBlack) {
        this(StandardTicker.BUKKIT_SYNC_RUNNABLE.createUnstartedTicker(), center, granularity, speedWhite, radBlack);
    }

    /**
     * @param whiteOffsetRadian The offset of the white particles in Radian
     */
    public void setWhiteOffsetRadian(double whiteOffsetRadian) {
        this.whiteOffsetRadian = whiteOffsetRadian;
    }

    /**
     * @return The offset of the white particles in Radian.
     */
    public double getWhiteOffsetRadian() {
        return whiteOffsetRadian;
    }

    @Override
    public void display(Location center) {
        time += getGranularity();

        spawnParticle(time, radBlack, Particle.SMOKE_NORMAL, center);
        spawnParticle(time + PI, radBlack, Particle.SMOKE_NORMAL, center);

        {
            double theta = time + whiteOffsetRadian;

            double xOffset = Math.cos(theta) * speedWhite;
            double yOffset = 0;
            double zOffset = Math.sin(theta) * speedWhite;
            center.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, center, 0, xOffset, yOffset, zOffset, 1);

            xOffset = Math.cos(theta + PI) * speedWhite;
            zOffset = Math.sin(theta + PI) * speedWhite;
            center.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, center, 0, xOffset, yOffset, zOffset, 1);
        }
    }

    private void spawnParticle(double theta, double rad, Particle particle, Location center) {
        double x = Math.cos(theta) * rad;
        double z = Math.sin(theta) * rad;

        Location point = center.clone().add(x, 0, z);

        center.getWorld().spawnParticle(particle, point, 10, 0, 0, 0, 0);
    }

    @Override
    public void tick(long elapsedNanoSeconds) {
        display(getCenter());
    }
}
