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

    private double  width;
    private double  height;
    private boolean filled;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     * @param height The height
     * @param width The width
     * @param filled Whether the Rectangle is filled
     */
    public Rectangle(Orientation orientation, double width, double height, double granularity, Particle particle, boolean filled) {
        super(orientation, granularity, particle);
        Objects.requireNonNull(orientation, "orientation can not be null!");
        Objects.requireNonNull(particle, "particle can not be null!");

        this.width = width;
        this.height = height;
        this.filled = filled;
    }

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param sideLength The width and height
     * @param granularity The granularity. The granularity is the distance
     *            between each spawned particle
     * @param particle The Particle to use
     * @param filled Whether the Rectangle is filled
     */
    public Rectangle(Orientation orientation, double sideLength, double granularity, Particle particle, boolean filled) {
        this(orientation, sideLength, sideLength, granularity, particle, filled);
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

    /**
     * Checks if a Rectangle is filled
     * 
     * @return True if the Rectangle is filled
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets whether a Rectangle is filled
     * 
     * @param filled True if the Rectangle is filled
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    @Override
    public void display(Location center) {
        World world = center.getWorld();

        if (filled) {
            displayFilled(center, world);
        } else {
            displayOutline(center);
        }
    }

    private void displayOutline(Location center) {
        Location bottomLeft = center.clone();
        Location topLeft = center.clone();
        Location bottomRight = center.clone();
        Location topRight = center.clone();

        {
            Vector bottomLeftAddition = new Vector(-width / 2, 0, -height / 2);
            Vector topLeftAddition = new Vector(-width / 2, 0, height / 2);
            Vector bottomRightAddition = new Vector(width / 2, 0, -height / 2);
            Vector topRightAddition = new Vector(width / 2, 0, height / 2);

            if (getOrientation() == Orientation.VERTICAL) {
                swapZY(bottomLeftAddition);
                swapZY(topLeftAddition);
                swapZY(topRightAddition);
                swapZY(bottomRightAddition);
            }

            bottomLeft.add(bottomLeftAddition);
            topLeft.add(topLeftAddition);
            bottomRight.add(bottomRightAddition);
            topRight.add(topRightAddition);
        }

        AxisAlignedLine axisAlignedLine = new AxisAlignedLine(getGranularity(), getParticle(), bottomLeft, bottomRight);

        // BOTTOM LINE
        axisAlignedLine.display();

        // LEFT LINE
        axisAlignedLine.setSecond(topLeft);
        axisAlignedLine.display();

        // TOP LINE
        axisAlignedLine.setFirst(topRight);
        axisAlignedLine.display();

        // RIGHT LINE
        axisAlignedLine.setSecond(bottomRight);
        axisAlignedLine.display();
    }

    private void swapZY(Vector in) {
        double tmpY = in.getY();
        in.setY(in.getZ());
        in.setZ(tmpY);
    }

    private void displayFilled(Location center, World world) {
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
