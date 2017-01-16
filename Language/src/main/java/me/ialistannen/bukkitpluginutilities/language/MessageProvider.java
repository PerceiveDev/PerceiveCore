package me.ialistannen.bukkitpluginutilities.language;

import java.util.Locale;
import java.util.Objects;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

/**
 * Provides translations
 */
public interface MessageProvider {

    /**
     * Translates a message <b>and color it</b>
     *
     * @param key The key
     * @param category The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, colored String
     *
     * @see #trUncolored(String, String, Object...)
     */
    @Nonnull
    default String tr(@Nonnull String key, @Nonnull String category, @Nonnull Object... formattingObjects) {
        Objects.requireNonNull(key, "key can not be null");
        Objects.requireNonNull(category, "category can not be null");
        Objects.requireNonNull(formattingObjects, "formattingObjects can not be null");

        return ChatColor.translateAlternateColorCodes('&', trUncolored(key, category, formattingObjects));
    }

    /**
     * Translates a message <b>and color it</b>
     *
     * @param key The key
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, colored String
     *
     * @see #tr(String, String, Object...) #tr(String, String, Object...) with
     * the default category
     */
    @SuppressWarnings("unused")
    @Nonnull
    default String tr(@Nonnull String key, @Nonnull Object... formattingObjects) {
        Objects.requireNonNull(key, "key can not be null");
        Objects.requireNonNull(formattingObjects, "formattingObjects can not be null");

        return ChatColor.translateAlternateColorCodes('&', trUncolored(key, formattingObjects));
    }

    /**
     * Translates the message if found, otherwise translates the passed String
     *
     * @param key The key to use
     * @param category The category it belongs to
     * @param defaultString The default value to assume, if none is set
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated String
     */
    @Nonnull
    String trOrDefault(@Nonnull String key, @Nonnull String category, @Nonnull String defaultString, @Nonnull
            Object... formattingObjects);

    /**
     * Translates the message if found, otherwise translates the passed String
     *
     * @param key The key to use
     * @param defaultString The default value to assume, if none is set
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated String
     *
     * @see #trOrDefault(String, String, String, Object...) #trOrDefault(String,
     * String, String, Object...) with the #getDefaultCategory()
     */
    @SuppressWarnings("unused")
    @Nonnull
    default String trOrDefault(@Nonnull String key, @Nonnull String defaultString, @Nonnull Object...
            formattingObjects) {
        Objects.requireNonNull(key, "key can not be null");
        Objects.requireNonNull(defaultString, "defaultString can not be null");
        Objects.requireNonNull(formattingObjects, "formattingObjects can not be null");

        return trOrDefault(key, getDefaultCategory(), defaultString, formattingObjects);
    }

    /**
     * Translates a message <b>and doesn't color it</b>
     *
     * @param key The key
     * @param category The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     */
    @Nonnull
    String trUncolored(@Nonnull String key, @Nonnull String category, @Nonnull Object... formattingObjects);

    /**
     * Translates a message <b>and doesn't color it</b>
     *
     * @param key The key
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @see #trUncolored(String, String, Object...) #trUncolored(String, String,
     * Object...) with the default category
     */
    @Nonnull
    default String trUncolored(@Nonnull String key, @Nonnull Object... formattingObjects) {
        Objects.requireNonNull(key, "key can not be null");
        Objects.requireNonNull(formattingObjects, "formattingObjects can not be null");

        return trUncolored(key, getDefaultCategory(), formattingObjects);
    }

    /**
     * Sets the default category used by the shortened methods.
     *
     * @param categoryName The new default category.
     *
     * @return True if the category was set
     */
    @SuppressWarnings("unused")
    boolean setDefaultCategory(@Nonnull String categoryName);

    /**
     * Returns the default category. Used for the shortened methods
     *
     * @return The default category
     */
    String getDefaultCategory();

    /**
     * Adds the category
     *
     * @param category The category to add
     */
    @SuppressWarnings("unused")
    void addCategory(@Nonnull String category);

    /**
     * Changes the language, if a new one is available.
     *
     * @param locale The Locale to set it to
     *
     * @return True if the language was changed
     */
    @SuppressWarnings("unused")
    boolean setLanguage(@Nonnull Locale locale);

    /**
     * Returns the current language
     *
     * @return The current language
     */
    Locale getLanguage();

    /**
     * Reloads the language files from disk. The internal ones will not be
     * reloaded.
     */
    @SuppressWarnings("unused")
    void reload();
}
