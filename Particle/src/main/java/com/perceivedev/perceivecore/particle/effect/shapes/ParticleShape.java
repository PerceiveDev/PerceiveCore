package com.perceivedev.perceivecore.particle.effect.shapes;

import org.bukkit.Particle;

import com.perceivedev.perceivecore.particle.effect.ParticleEffect;

/**
 * A particle shape
 */
public interface ParticleShape extends ParticleEffect {

    /**
     * @return The {@link Orientation}
     */
    Orientation getOrientation();

    /**
     * @param orientation The new {@link Orientation}
     */
    @SuppressWarnings("unused")
    void setOrientation(Orientation orientation);

    /**
     * The granularity is the distance between each spawned particle
     *
     * @return The granularity
     */
    double getGranularity();

    /**
     * The granularity is the distance between each spawned particle
     *
     * @param granularity The granularity
     */
    @SuppressWarnings("unused")
    void setGranularity(double granularity);

    /**
     * @return The particle that is used to display the square
     */
    Particle getParticle();

    /**
     * @param particle The particle that is used to display the square
     */
    void setParticle(Particle particle);
}
