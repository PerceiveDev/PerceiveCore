package com.perceivedev.perceivecore.particle.effect.shapes;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A circle
 */
public class Circle extends AbstractParticleShape {

    private double radius;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     * @param radius The radius
     */
    public Circle(Orientation orientation, double granularity, Particle particle, double radius) {
        super(orientation, granularity, particle);

        this.radius = radius;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius The new radius
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public void display(Location center) {
        World world = center.getWorld();

        for (double theta = 0; theta < 2 * PI; theta += getGranularity()) {
            double x = cos(theta) * radius;
            double y = sin(theta) * radius;

            Vector vector;

            if (getOrientation() == Orientation.HORIZONTAL) {
                vector = new Vector(x, 0, y);
            } else {
                vector = new Vector(x, y, 0);
            }

            Location point = center.clone().add(vector);
            world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
        }
    }
}
