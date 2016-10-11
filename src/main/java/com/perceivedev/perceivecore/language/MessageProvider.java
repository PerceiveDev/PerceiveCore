package com.perceivedev.perceivecore.language;

import java.util.Locale;
import java.util.Objects;

import org.bukkit.ChatColor;

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
    default String tr(String key, String category, Object... formattingObjects) {
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
    default String tr(String key, Object... formattingObjects) {
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
    String trOrDefault(String key, String category, String defaultString, Object... formattingObjects);

    /**
     * Translates the message if found, otherwise translates the passed String
     *
     * @param key The key to use
     * @param defaultString The default value to assume, if none is set
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated String
     *
     * @see #trOrDefault(String, String, String, Object...) #trOrDefault(String, String, String, Object...) with the #getDefaultCategory()
     */
    default String trOrDefault(String key, String defaultString, Object... formattingObjects) {
        return trOrDefault(key, getDefaultCategory().getName(), defaultString, formattingObjects);
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
    String trUncolored(String key, String category, Object... formattingObjects);

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
    default String trUncolored(String key, Object... formattingObjects) {
        return trUncolored(key, getDefaultCategory().getName(), formattingObjects);
    }

    /**
     * Sets the default category used by the shortened methods.
     *
     * @param categoryName The new default category.
     *
     * @return True if the category was set
     */
    boolean setDefaultCategory(String categoryName);

    /**
     * Returns the default category. Used for the shortened methods
     *
     * @return The default category
     */
    Category getDefaultCategory();

    /**
     * Adds the category
     *
     * @param category The category to add
     */
    void addCategory(Category category);

    /**
     * Changes the language, if a new one is available.
     *
     * @param locale The Locale to set it to
     *
     * @return True if the language was changed
     */
    boolean setLanguage(Locale locale);

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
    void reload();

    /**
     * A category
     */
    public class Category {
        private String name;

        /**
         * @param name The name
         */
        public Category(String name) {
            this.name = name;
        }

        /**
         * Returns the name
         *
         * @return The name
         */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Category{" + "name='" + name + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Category)) {
                return false;
            }
            Category category = (Category) o;
            return Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
