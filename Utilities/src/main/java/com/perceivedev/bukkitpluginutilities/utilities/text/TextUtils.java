package com.perceivedev.bukkitpluginutilities.utilities.text;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class TextUtils {

    /**
     * The {@link Pattern} to match the color chars in a string
     */
    private static final Pattern COLOR_CHAR_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "(?=[a-f0-9klmno])");

    /**
     * Colors the text
     * <p>
     * Uses {@link ChatColor#translateAlternateColorCodes(char, String)} with
     * '{@code &}' as color char
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
    @SuppressWarnings("unused")
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
     * @param newColorChar The new color char to use
     *
     * @return The text with the color codes replaced
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static String replaceColors(@Nonnull String text, @Nonnull String newColorChar) {
        Objects.requireNonNull(text, "text can not be null");
        Objects.requireNonNull(newColorChar, "newColorChar can not be null");

        return COLOR_CHAR_PATTERN.matcher(text).replaceAll(newColorChar);
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

    /**
     * Normalizes a Path name
     * <p>
     * Replaces '\' and '/' to File.separator
     *
     * @param input The input path
     *
     * @return The normalized name
     */
    @SuppressWarnings("unused")
    public static String normalizePathName(String input) {
        return input.replace("/", File.separator).replace("\\", File.separator);
    }

    // @formatter:off
    /**
     * Converts a constant name to a nicer format
     * <p>
     * Example outputs with upperCaseAfterSpace set to <b>false</b>:
     * <ul>
     *     <li>{@code "SWORD" ==> "Sword"}</li>
     *     <li>{@code "DIAMOND_SWORD" ==> "Diamond sword"}</li>
     * </ul>
     * Example outputs with upperCaseAfterSpace set to <b>true</b>:
     * <ul>
     *     <li>{@code "SWORD" ==> "Sword"}</li>
     *     <li>{@code "DIAMOND_SWORD" ==> "Diamond Sword"}</li>
     * </ul>
     *
     * @param text The constant to format
     * @param upperCaseAfterSpace Whether you want an upper case letter after a space
     *
     * @return The formatted Text
     */
    // @formatter:on
    @SuppressWarnings("unused")
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
                result.append(" ");
                continue;
            }
            if (upperCase) {
                result.append(Character.toUpperCase(c));
                upperCase = false;
            }
            else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * Hides text in color codes
     *
     * @param text The text to hide
     *
     * @return The hidden text
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static String hideText(@Nonnull String text) {
        Objects.requireNonNull(text, "text cannot be null!");

        StringBuilder output = new StringBuilder();

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        String hex = Hex.encodeHexString(bytes);

        for (char c : hex.toCharArray()) {
            output.append(ChatColor.COLOR_CHAR).append(c);
        }

        return output.toString();
    }

    /**
     * Reveals the text hidden in color codes
     *
     * @param text The hidden text
     *
     * @return The revealed text
     *
     * @throws IllegalArgumentException if an error occurred while decoding.
     */
    @SuppressWarnings("unused")
    @Nonnull
    public static String revealText(@Nonnull String text) {
        Objects.requireNonNull(text, "text cannot be null!");

        if (text.isEmpty()) {
            return text;
        }

        char[] chars = text.toCharArray();

        char[] hexChars = new char[chars.length / 2];

        IntStream.range(0, chars.length)
                .filter(value -> value % 2 != 0)
                .forEach(value -> hexChars[value / 2] = chars[value]);

        try {
            return new String(Hex.decodeHex(hexChars), StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Couldn't decode text", e);
        }
    }
}
