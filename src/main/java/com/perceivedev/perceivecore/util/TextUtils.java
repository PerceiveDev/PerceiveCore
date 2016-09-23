
package com.perceivedev.perceivecore.util;

import java.util.Locale;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class TextUtils {

    public static final Pattern UNSAFE_CHARS = Pattern.compile("[^a-z0-9]");

    public static String colorize(String text) {

        return ChatColor.translateAlternateColorCodes('&', text);

    }

    public static String uncolorize(String text) {

        return text.replace(ChatColor.COLOR_CHAR, '&');

    }

    public static String stripColor(String text) {

        return ChatColor.stripColor(text);

    }

    public static String enumFormat(String text) {

        return text.trim().toUpperCase().replace(" ", "_");

    }

    // Yes, this is from Essentials. Stop judging me.
    public static String safeString(String text) {
        return UNSAFE_CHARS.matcher(text.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

}
