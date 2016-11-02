package com.perceivedev.perceivecore.particle.effect;

import org.bukkit.Location;

/** A simple particle effect */
public interface ParticleEffect {

    /**
     * Displays a particle effect at the given center
     *
     * @param center The center of the effect
     */
    void display(Location center);
}
