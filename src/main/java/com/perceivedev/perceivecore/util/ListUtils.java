package com.perceivedev.perceivecore.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class ListUtils {

    /**
     * Joins a List to a String
     *
     * @param list The list to concat
     * @param delimiter The delimiter
     *
     * @return The joined list
     */
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
    @Nonnull
    public static List<String> stripColors(@Nonnull List<String> list) {
        Objects.requireNonNull(list, "list can not be null");

        return list.stream().map(TextUtils::stripColor).collect(Collectors.toList());
    }
}
