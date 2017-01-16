package me.ialistannen.bukkitpluginutilities.updater;

import java.util.List;
import java.util.Locale;

import me.ialistannen.bukkitpluginutilities.coreplugin.CorePlugin;

/**
 * A base updater
 * <p>
 * <b>Subclasses must respect {@link #getUpdateCheckSettings()}</b>
 */
public interface Updater {

    /**
     * @param strategy The strategy used to update the plugin
     */
    @SuppressWarnings("unused")
    void setUpdateStrategy(UpdateStrategy<?> strategy);

    /**
     * Checks for updates.
     * <p>
     * A blocking operation
     *
     * @return The result of the check
     */
    @SuppressWarnings("unused")
    UpdateCheckResult searchForUpdate();

    /**
     * Updates this plugin, by downloading and copying the file
     *
     * @return The result of updating
     *
     * @throws IllegalStateException if {@link #searchForUpdate()} wasn't called
     *                               before
     */
    @SuppressWarnings("unused")
    UpdateResult update();

    /**
     * @return All entries pulled in the last {@link #searchForUpdate()} method
     * call
     */
    @SuppressWarnings("unused")
    List<UpdaterEntry> getEntryList();

    /**
     * @return The current global {@link UpdateCheckSettings}
     */
    @SuppressWarnings("unused")
    default UpdateCheckSettings getUpdateCheckSettings() {
        String updateSettings = CorePlugin.getInstance().getConfig().getString("global_update_settings");
        return UpdateCheckSettings.getFromString(updateSettings, UpdateCheckSettings.DISABLED);
    }

    /**
     * The result of updating
     */
    enum UpdateResult {
        @SuppressWarnings("unused")
        SUCCESSFULLY_COPIED_TO_UPDATE_DIR,
        @SuppressWarnings("unused")
        ERROR_WHILE_DOWNLOADING,
        @SuppressWarnings("unused")
        ERROR_WHILE_CREATING_OUTPUT_FOLDER,
        @SuppressWarnings("unused")
        ERROR_WHILE_COPYING,
        @SuppressWarnings("unused")
        NO_UPDATE_FOUND,
        @SuppressWarnings("unused")
        DISABLED
    }

    /**
     * The result of checking for an update
     */
    enum UpdateCheckResult {
        @SuppressWarnings("unused")
        NO_NEW_VERSION,
        @SuppressWarnings("unused")
        UPDATE_FOUND,
        @SuppressWarnings("unused")
        NOT_SEARCHED,
        @SuppressWarnings("unused")
        ERROR_SEARCHING,
        @SuppressWarnings("unused")
        DISABLED
    }

    /**
     * The mode defined in the config
     */
    enum UpdateCheckSettings {
        @SuppressWarnings("unused")
        CHECK,
        @SuppressWarnings("unused")
        CHECK_AND_UPDATE,
        @SuppressWarnings("unused")
        DISABLED;

        /**
         * Parses a String to a setting
         *
         * @param input The input String
         * @param def The default value
         *
         * @return The parsed value or the default
         */
        public static UpdateCheckSettings getFromString(String input, UpdateCheckSettings def) {
            if (input == null) {
                return def;
            }
            try {
                return valueOf(input.toUpperCase(Locale.ROOT).replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                return def;
            }
        }
    }
}
