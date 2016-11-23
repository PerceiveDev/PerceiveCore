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
public class Helix extends AbstractParticleShape {

    private double radius;
    private double height;
    private double loops;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     * @param radius The radius
     * @param height The height of the helix
     * @param loops How many times to loop around
     */
    public Helix(Orientation orientation, double granularity, Particle particle, double radius, double height, double loops) {
        super(orientation, granularity, particle);

        this.radius = radius;
        this.height = height;
        this.loops = loops;
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

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height The new height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the number of loops
     */
    public double getLoops() {
        return loops;
    }

    /**
     * @param loops The new number of loops
     */
    public void setLoops(double loops) {
        this.loops = loops;
    }

    @Override
    public void display(Location center) {
        World world = center.getWorld();

        double y = 0.0;
        double limit = 2 * PI * loops;
        for (double theta = 0; theta < limit; theta += getGranularity()) {
            double x = cos(theta) * radius;
            y += height / (limit / getGranularity());
            double z = sin(theta) * radius;

            Vector vector;

            if (getOrientation() == Orientation.VERTICAL) {
                vector = new Vector(x, y, z);
            } else {
                vector = new Vector(x, z, y);
            }

            Location point = center.clone().add(vector);
            world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
        }
    }
}
