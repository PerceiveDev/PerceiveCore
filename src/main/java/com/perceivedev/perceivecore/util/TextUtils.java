
package com.perceivedev.perceivecore.util;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

public class TextUtils {

    /**
     * Colors the text
     * <p>
     * Uses {@link ChatColor#translateAlternateColorCodes(char, String)} with
     * '&' as color char
     *
     * @param text The text to color
     *
     * @return The colored text
     */
    @Nonnull
    public static String colorize(@Nonnull String text) {
        Objects.requireNonNull(text, "text can not be null");

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Repeats a String
     *
     * @param string The String to repeat
     * @param amount The amount to repeat it for
     *
     * @return The repeated String
     */
    @Nonnull
    public static String repeat(@Nonnull String string, int amount) {
        Objects.requireNonNull(string, "string can not be null");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            builder.append(string);
        }

        return builder.toString();
    }

    /**
     * Replaces the color Char with the given String
     * <p>
     * Replaces {@link ChatColor#COLOR_CHAR} ('{@value ChatColor#COLOR_CHAR}')
     *
     * @param text The text to replace the color codes with
     *
     * @return The text with the color codes replaced
     */
    @Nonnull
    public static String uncolorize(@Nonnull String text, @Nonnull String newColorChar) {
        Objects.requireNonNull(text, "text can not be null");
        Objects.requireNonNull(newColorChar, "newColorChar can not be null");

        return text.replace(Character.toString(ChatColor.COLOR_CHAR), text);
    }

    /**
     * Strips all color codes from the text
     *
     * @param text The text to strip the colors from
     *
     * @return The text uncolored
     */
    @Nonnull
    public static String stripColor(@Nonnull String text) {
        Objects.requireNonNull(text, "text can not be null");

        return ChatColor.stripColor(text);
    }

    // @formatter:off
    /**
     * Converts a constant name to a nicer format
     * <p>
     * Example outputs with upperCaseAfterSpace set to <b>false</b>:
     * <ul>
     *     <li><code>"SWORD" ==> "Sword"</code></li>
     *     <li><code>"DIAMOND_SWORD" ==> "Diamond sword"</code></li>
     * </ul>
     * Example outputs with upperCaseAfterSpace set to <b>true</b>:
     * <ul>
     *     <li><code>"SWORD" ==> "Sword"</code></li>
     *     <li><code>"DIAMOND_SWORD" ==> "Diamond Sword"</code></li>
     * </ul>
     *
     * @param text The constant to format
     * @param upperCaseAfterSpace Whether you want an upper case letter after a space
     *
     * @return The formatted Text
     */
    // @formatter:on
    @Nonnull
    public static String enumFormat(@Nonnull String text, boolean upperCaseAfterSpace) {
        Objects.requireNonNull(text, "text can not be null");

        if (!upperCaseAfterSpace) {
            return Character.toUpperCase(text.charAt(0)) + text.toLowerCase().replace("_", " ");
        }
        StringBuilder result = new StringBuilder();
        boolean upperCase = true;

        for (char c : text.toCharArray()) {
            if (c == '_') {
                upperCase = true;
                result.append("_");
                continue;
            }
            if (upperCase) {
                result.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }
}
