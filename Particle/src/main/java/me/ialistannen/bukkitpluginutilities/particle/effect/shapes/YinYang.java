package me.ialistannen.bukkitpluginutilities.particle.effect.shapes;

import org.bukkit.Location;
import org.bukkit.Particle;

import me.ialistannen.bukkitpluginutilities.particle.effect.RecurringParticleEffect;
import me.ialistannen.bukkitpluginutilities.particle.ticker.StandardTicker;
import me.ialistannen.bukkitpluginutilities.particle.ticker.Ticker;
import me.ialistannen.bukkitpluginutilities.particle.ticker.Ticker.Tickable;

import static java.lang.Math.PI;

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
     * between each spawned particle
     * @param speedWhite The speed of the white particles. 0.4 looks nice.
     * @param radBlack The radius of the black particles
     */
    @SuppressWarnings("WeakerAccess")
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
     * between each spawned particle
     * @param speedWhite The speed of the white particles. 0.4 looks nice.
     * @param radBlack The radius of the black particles
     */
    @SuppressWarnings("unused")
    public YinYang(Location center, double granularity, double speedWhite, double radBlack) {
        this(StandardTicker.BUKKIT_SYNC_RUNNABLE.createUnstartedTicker(), center, granularity, speedWhite, radBlack);
    }

    /**
     * @param whiteOffsetRadian The offset of the white particles in Radian
     */
    @SuppressWarnings("unused")
    public void setWhiteOffsetRadian(double whiteOffsetRadian) {
        this.whiteOffsetRadian = whiteOffsetRadian;
    }

    /**
     * @return The offset of the white particles in Radian.
     */
    @SuppressWarnings("unused")
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
