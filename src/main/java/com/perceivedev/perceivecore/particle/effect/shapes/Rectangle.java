package com.perceivedev.perceivecore.particle.effect.shapes;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A simple square
 */
public class Rectangle extends AbstractParticleShape {

    private double width;
    private double height;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     * @param height The height
     * @param width The width
     */
    public Rectangle(Orientation orientation, double width, double height, double granularity, Particle particle) {
        super(orientation, granularity, particle);
        Objects.requireNonNull(orientation, "orientation can not be null!");
        Objects.requireNonNull(particle, "particle can not be null!");

        this.width = width;
        this.height = height;
    }

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param sideLength The width and height
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     */
    public Rectangle(Orientation orientation, double sideLength, double granularity, Particle particle) {
        this(orientation, sideLength, sideLength, granularity, particle);
    }

    /**
     * @return The height of the rectangle
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height The height of the rectangle
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return The width of this rectangle
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width The width of this rectangle
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Sets the length of both sides.
     * <p>
     * This is the same as calling {@link #setWidth(double)} and
     * {@link #setHeight(double)} with the same value.
     * 
     * @param sideLength The new length of the sides
     */
    public void setSideLength(double sideLength) {
        setWidth(sideLength);
        setHeight(sideLength);
    }

    @Override
    public void display(Location center) {
        World world = center.getWorld();

        for (double x = 0; x <= width; x += getGranularity()) {
            for (double y = 0; y <= height; y += getGranularity()) {
                Vector vector;

                if (getOrientation() == Orientation.HORIZONTAL) {
                    vector = new Vector(x - width / 2, 0, y - height / 2);
                } else {
                    vector = new Vector(x - width / 2, y - height / 2, 0);
                }

                Location point = center.clone().add(vector);
                world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
            }
        }
    }
}
