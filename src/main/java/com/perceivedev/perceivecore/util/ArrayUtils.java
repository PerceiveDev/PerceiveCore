package com.perceivedev.perceivecore.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * The ArrayUtils class contains various methods for manipulating arrays
 *
 * @author Rayzr
 * @see ArrayUtils#concat(Object[], String)
 */
public class ArrayUtils {

    /**
     * Concatenates all objects in the array with the given filler. Example:
     * <br>
     * <br>
     * <code>ArrayUtils.concat(new String[] {"Hello", "world!", "How", "are", "you?"}, "_");</code>
     * <br>
     * <br>
     * Would return {@code "Hello_world!_How_are_you?"}
     *
     * @param arr the array
     * @param filler the String to concatenate the objects with
     *
     * @return The concatenated String
     */
    @Nonnull
    public static String concat(@Nonnull Object[] arr, @Nonnull String filler) {
        Objects.requireNonNull(arr, "array can not be null");
        Objects.requireNonNull(filler, "filler can not be null");

        return Arrays.stream(arr).map(Object::toString).collect(Collectors.joining(filler));
    }

    /**
     * Remove the first element from an array
     *
     * @param original The original array
     * @param <T> The type of the array
     *
     * @return The reduced array
     */
    @Nonnull
    public static <T> T[] removeFirst(@Nonnull T[] original) {
        Objects.requireNonNull(original, "original can not be null");

        if (original.length < 1) {
            return original;
        }
        return Arrays.copyOfRange(original, 1, original.length);
    }

    /**
     * Checks if an element is contained in the given array
     * 
     * @param array The array
     * @param element The element to search
     * @param <T> The type of the array
     * @return True if the element is in the given array
     */
    public static <T> boolean contains(T[] array, T element) {
        Objects.requireNonNull(array, "array can not be null!");
        return Arrays.stream(array).anyMatch(element::equals);
    }

}
