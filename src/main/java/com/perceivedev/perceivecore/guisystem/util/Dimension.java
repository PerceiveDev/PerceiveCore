package com.perceivedev.perceivecore.guisystem.util;

import java.util.Objects;

import com.perceivedev.perceivecore.guisystem.component.Component;

/**
 * The dimension of a {@link Component}
 */
public class Dimension {

    private int width, height;

    /**
     * @param width The width
     * @param height The height
     *
     * @throws IllegalArgumentException if width or height <= 0
     */
    public Dimension(int width, int height) {
        ensureValidDimension(width, height);

        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of the component
     *
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the component
     *
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks if this dimension fits in the given one
     *
     * @param dimension The dimension to check against
     *
     * @return True if this dimension fits in the given one
     */
    public boolean fitsInside(Dimension dimension) {
        return width <= dimension.getWidth() && height <= dimension.getHeight();
    }

    /**
     * Ensures the values are in the allowed intervals
     *
     * @param width The width
     * @param height The height
     *
     * @throws IllegalArgumentException if width or height <= 0
     */
    public static void ensureValidDimension(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be > 0 (" + width + ")");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be > 0 (" + height + ")");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Dimension))
            return false;
        Dimension dimension = (Dimension) o;
        return width == dimension.width &&
                  height == dimension.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Dimension{" +
                  "width=" + width +
                  ", height=" + height +
                  '}';
    }
}
