package com.perceivedev.perceivecore.utilities.collections;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.perceivedev.perceivecore.utilities.text.TextUtils;

import javax.annotation.Nonnull;

public class ListUtils {

    /**
     * Joins a List to a String
     *
     * @param list The list to concat
     * @param delimiter The delimiter
     *
     * @return The joined list
     *
     * @see String#join(CharSequence, CharSequence...)
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static String concatList(@Nonnull List<String> list, @Nonnull String delimiter) {
        Objects.requireNonNull(list, "list can not be null");
        Objects.requireNonNull(delimiter, "delimiter can not be null");

        return list.stream().collect(Collectors.joining(delimiter));
    }

    /**
     * Colors every object in the list
     *
     * @param list The list to color
     *
     * @return The list, with every item colored
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static List<String> colorList(@Nonnull List<String> list) {
        Objects.requireNonNull(list, "list can not be null");

        return list.stream().map(TextUtils::colorize).collect(Collectors.toList());
    }

    /**
     * Strips all colors from all lines
     *
     * @param list The list to strip colors from
     *
     * @return The list without colors
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static List<String> stripColors(@Nonnull List<String> list) {
        Objects.requireNonNull(list, "list can not be null");

        return list.stream().map(TextUtils::stripColor).collect(Collectors.toList());
    }

    /**
     * Replaces a given String in all entries
     * <p>
     * This will not modify the passed list
     *
     * @param <T> The class of the objects in the list
     * @param list The list to replace the string in
     * @param replacementFunction The function to use to replace things
     *
     * @return The replaced list
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static <T> List<T> replaceInAll(@Nonnull List<T> list, @Nonnull Function<T, T> replacementFunction) {
        Objects.requireNonNull(list, "list cannot be null!");
        Objects.requireNonNull(replacementFunction, "replacementFunction cannot be null!");

        return list.stream()
                .map(replacementFunction)
                .collect(Collectors.toList());
    }
}
