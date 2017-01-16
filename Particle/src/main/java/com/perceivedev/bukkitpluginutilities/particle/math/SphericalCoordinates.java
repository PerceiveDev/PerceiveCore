package com.perceivedev.bukkitpluginutilities.particle.math;

import java.text.DecimalFormat;

import org.bukkit.util.Vector;

/**
 * A class to deal with Spherical coordinates
 */
public class SphericalCoordinates implements Cloneable {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

    private double rho, phi, theta;

    /**
     * @param rho The distance rho
     * @param theta The angle theta
     * @param phi The angle phi
     */
    public SphericalCoordinates(double rho, double theta, double phi) {
        this.rho = rho;
        this.theta = theta;
        this.phi = phi;
    }

    /**
     * Returns the angle {@code phi}
     *
     * @return The angle {@code phi}
     */
    @SuppressWarnings("WeakerAccess")
    public double getPhi() {
        return phi;
    }

    /**
     * Sets the angle {@code phi}
     *
     * @param phi The new angle {@code phi}
     */
    public void setPhi(double phi) {
        this.phi = phi;
    }

    /**
     * Returns the distance {@code rho}
     *
     * @return The distance {@code rho}
     */
    @SuppressWarnings("WeakerAccess")
    public double getRho() {
        return rho;
    }

    /**
     * Sets the distance {@code rho}
     *
     * @param rho The new distance {@code rho}
     */
    public void setRho(double rho) {
        this.rho = rho;
    }

    /**
     * Returns the angle {@code theta}
     *
     * @return The angle {@code theta}
     */
    @SuppressWarnings("WeakerAccess")
    public double getTheta() {
        return theta;
    }

    /**
     * Sets the angle {@code theta}
     *
     * @param theta The new angle {@code theta}
     */
    public void setTheta(double theta) {
        this.theta = theta;
    }

    @Override
    public String toString() {
        return "SphericalCoordinates{" +
                "rho=" + FORMAT.format(rho) +
                ", theta=" + FORMAT.format(theta) +
                ", phi=" + FORMAT.format(phi) +
                '}';
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts Spherical Coordinates to Cartesian
     *
     * @return The Coordinates in the cartesian coordinate system
     */
    public Vector toCartesian() {
        double x = Math.cos(getTheta()) * Math.sin(getPhi()) * getRho();
        double y = Math.sin(getTheta()) * Math.sin(getPhi()) * getRho();
        double z = Math.cos(getPhi()) * getRho();

        return new Vector(x, y, z);
    }

    /**
     * Converts Spherical Coordinates to Cartesian and accounts for Minecraft's
     * y-z swap
     *
     * @return The Coordinates in the cartesian coordinate system <b>and</b> y
     * and z switched
     */
    @SuppressWarnings("unused")
    public Vector toBukkitCartesian() {
        Vector vector = toCartesian();

        // swap y and z
        double y = vector.getY();
        vector.setY(vector.getZ());
        vector.setZ(y);

        return vector;
    }

    /**
     * Converts cartesian coordinates to Spherical ones
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     *
     * @return The resulting Spherical coordinates
     */
    public static SphericalCoordinates fromCartesian(double x, double y, double z) {
        double rho = Math.sqrt(x * x + y * y + z * z);
        double phi = Math.acos(z / rho);
        double theta = Math.atan2(y, x);

        return new SphericalCoordinates(rho, theta, phi);
    }
}
