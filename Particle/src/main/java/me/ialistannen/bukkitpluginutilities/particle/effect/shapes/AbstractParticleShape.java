package me.ialistannen.bukkitpluginutilities.particle.effect.shapes;

import java.util.Objects;

import org.bukkit.Particle;

/**
 * A skeleton implementation for the {@link ParticleShape}
 */
public abstract class AbstractParticleShape implements ParticleShape {

    private Orientation orientation;
    private double granularity;
    private Particle particle;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularity The granularity. The granularity is the distance
     * between each spawned particle
     * @param particle The Particle to use
     */
    @SuppressWarnings("WeakerAccess")
    public AbstractParticleShape(Orientation orientation, double granularity, Particle particle) {
        this.orientation = orientation;
        this.granularity = granularity;
        this.particle = particle;
    }

    /**
     * @return The {@link Orientation}
     */
    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * @param orientation The new {@link Orientation}
     */
    @Override
    public void setOrientation(Orientation orientation) {
        Objects.requireNonNull(orientation, "orientation cannot be null!");

        this.orientation = orientation;
    }

    /**
     * The granularity is the distance between each spawned particle
     *
     * @return The granularity
     */
    @Override
    public double getGranularity() {
        return granularity;
    }

    /**
     * The granularity is the distance between each spawned particle
     *
     * @param granularity The granularity
     */
    @Override
    public void setGranularity(double granularity) {
        this.granularity = granularity;
    }

    /**
     * @return The particle that is used to display the square
     */
    @Override
    public Particle getParticle() {
        return particle;
    }

    /**
     * @param particle The particle that is used to display the square
     */
    @Override
    public void setParticle(Particle particle) {
        Objects.requireNonNull(particle, "particle cannot be null!");

        this.particle = particle;
    }

}
