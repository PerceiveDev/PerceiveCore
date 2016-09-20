package com.perceivedev.perceivecore.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The ArrayUtils class contains various methods for manipulating arrays
 *
 * @author Rayzr
 * @see ArrayUtils#concat(Object[], String)
 */
public class ArrayUtils {

    /**
     * Concatenates all objects in the array with the given filler. Example:<br>
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
    public static String concat(Object[] arr, String filler) {
	return Arrays.stream(arr).map(Object::toString).collect(Collectors.joining(filler));
    }

    /**
     * Functions exactly like {@link ArrayUtils#concat(Object[], String)}
     *
     * @param list the list
     * @param filler the String to concatenate the objects with
     *
     * @return The concatenated String
     */
    public static String concat(List<? extends Object> list, String filler) {
	return concat(list.toArray(), filler);
    }

    /**
     * Remove the first element from an array
     *
     * @param original the original array
     *
     * @return The reduced array
     */
    public static <T> T[] removeFirst(T[] original) {
	if (original.length < 1) {
	    return original;
	}
	return Arrays.copyOfRange(original, 1, original.length);
    }

}
