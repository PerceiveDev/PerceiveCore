/**
 * 
 */
package com.perceivedev.perceivecore.util;

/**
 * @author Rayzr
 *
 */
public class MathUtil {

    /**
     * Clamps a number between a minimum and maximum value
     * 
     * @param value The value to clamp
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The clamped number
     */
    public static int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max!");
        }
        if (min == max) {
            return min;
        }
        return value < min ? min : (value > max ? max : value);
    }

}
