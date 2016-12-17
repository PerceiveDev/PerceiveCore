package com.perceivedev.perceivecore.util.math;

import java.util.Objects;

import org.bukkit.util.Vector;

/**
 * Allows for rotation of points
 */
public class RotationMatrices {

    // @formatter:off
    // Rx(t)
    // [ 1       | 0       | 0       ]
    // [ 1       | cos(t)  | -sin(t) ]
    // [ 1       | sin(t)  | cos(t)  ]

    // Ry(t)
    // [ cos(t)  | 0       | sin(t)  ]
    // [ 0       | 1       | 0       ]
    // [ -sin(t) | 0       | cos(t)  ]

    // Rz(t)
    // [ cos(t)  | -sin(t) | 0       ]
    // [ sin(t)  | cos(t)  | 0       ]
    // [ 0       | 0       | 1       ]
    // @formatter:on

    /**
     * Rotates a vector with the given yaw and pitch
     * <p>
     * Yaw and pitch are in <b>degree</b>
     * 
     * @param in The vector
     * @param yaw The yaw (rotation around y axis)
     * @param pitch The pitch (rotation around x axis)
     * 
     * @return A clone with the new coordinates
     * 
     * @see #rotateRadian(Vector, double, double)
     */
    public static Vector rotateDegree(Vector in, double yaw, double pitch) {
        return rotateRadian(in, Math.toRadians(yaw), Math.toRadians(pitch));
    }

    /**
     * Rotates a vector with the given yaw and pitch
     * <p>
     * Yaw and pitch are in <b>radian</b>
     *
     * @param in The vector
     * @param yaw The yaw (rotation around y axis)
     * @param pitch The pitch (rotation around x axis)
     *
     * @return A clone with the new coordinates
     *
     * @see #rotateRadian(Vector, double, double)
     */
    public static Vector rotateRadian(Vector in, double yaw, double pitch) {
        Objects.requireNonNull(in, "in can not be null!");

        Vector out = in.clone();
        out = RotationMatrices.rotateY(out, -yaw);
        out = RotationMatrices.rotateX(out, pitch);

        return out;
    }

    /**
     * Rotates a vector along the x axis
     * 
     * @param in The vector
     * @param theta The angle theta to rotate it with
     *
     * @return A clone with the new coordinates
     */
    public static Vector rotateX(Vector in, double theta) {
        Objects.requireNonNull(in, "in can not be null!");

        double y = Math.cos(theta) * in.getY() - Math.sin(theta) * in.getZ();
        double z = Math.sin(theta) * in.getY() + Math.cos(theta) * in.getZ();

        return new Vector(in.getX(), y, z);
    }

    /**
     * Rotates a vector along the y axis
     *
     * @param in The vector
     * @param theta The angle theta to rotate it with
     * 
     * @return A clone with the new coordinates
     */
    public static Vector rotateY(Vector in, double theta) {
        Objects.requireNonNull(in, "in can not be null!");

        double x = in.getX() * Math.cos(theta) + in.getZ() * Math.sin(theta);
        double z = in.getX() * -Math.sin(theta) + in.getZ() * Math.cos(theta);

        return new Vector(x, in.getY(), z);
    }
}
