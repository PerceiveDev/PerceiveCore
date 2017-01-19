package com.perceivedev.perceivecore.utilities.collections;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * The ArrayUtils class contains various methods for manipulating arrays
 *
 * @author Rayzr
 */
public class ArrayUtils {

    /**
     * Concatenates all objects in the array with the given filler. Example:
     * <br>
     * <br>
     * {@code ArrayUtils.concat(new String[] {"Hello", "world!", "How", "are", "you?"}, "_");}
     * <br>
     * <br>
     * Would return {@code "Hello_world!_How_are_you?"}
     *
     * @param arr the array
     * @param delimiter the String to concatenate the objects with
     *
     * @return The concatenated String
     *
     * @see String#join(CharSequence, CharSequence...)
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static String concat(@Nonnull Object[] arr, @Nonnull String delimiter) {
        Objects.requireNonNull(arr, "array can not be null");
        Objects.requireNonNull(delimiter, "filler can not be null");

        return Arrays.stream(arr).map(Object::toString).collect(Collectors.joining(delimiter));
    }

    /**
     * Checks if an element is contained in the given array
     *
     * @param array The array
     * @param element The element to search
     * @param <T> The type of the array
     *
     * @return True if the element is in the given array
     */
    @SuppressWarnings("unused")
    public static <T> boolean contains(T[] array, T element) {
        Objects.requireNonNull(array, "array cannot be null!");
        return Arrays.stream(array).anyMatch(element::equals);
    }

}
