package com.perceivedev.perceivecore.updater;

import java.util.List;
import java.util.Locale;

import com.perceivedev.perceivecore.PerceiveCore;

/**
 * A base updater
 * <p>
 * <b>Subclasses must respect {@link #getUpdateCheckSettings()}</b>
 */
public interface Updater {

    /**
     * 
     * @param strategy The strategy used to update the plugin
     */
    void setUpdateStrategy(UpdateStrategy<?> strategy);

    /**
     * Checks for updates.
     * <p>
     * A blocking operation
     * 
     * @return The result of the check
     */
    UpdateCheckResult searchForUpdate();

    /**
     * Updates this plugin, by downloading and copying the file
     * 
     * @return The result of updating
     * @throws IllegalStateException if {@link #searchForUpdate()} wasn't called
     *             before
     */
    UpdateResult update();

    /**
     * @return All entries pulled in the last {@link #searchForUpdate()} method
     *         call
     */
    List<UpdaterEntry> getEntryList();

    /**
     * @return The current global {@link UpdateCheckSettings}
     */
    default UpdateCheckSettings getUpdateCheckSettings() {
        String updateSettings = PerceiveCore.getInstance().getConfig().getString("global_update_settings");
        return UpdateCheckSettings.getFromString(updateSettings, UpdateCheckSettings.DISABLED);
    }

    /**
     * The result of updating
     */
    enum UpdateResult {
        SUCCESSFULLY_COPIED_TO_UPDATE_DIR,
        ERROR_WHILE_DOWNLOADING,
        ERROR_WHILE_CREATING_OUTPUT_FOLDER,
        ERROR_WHILE_COPYING,
        NO_UPDATE_FOUND,
        DISABLED
    }

    /**
     * The result of checking for an update
     */
    enum UpdateCheckResult {
        NO_NEW_VERSION,
        UPDATE_FOUND,
        NOT_SEARCHED,
        ERROR_SEARCHING,
        DISABLED
    }

    /**
     * The mode defined in the config
     */
    enum UpdateCheckSettings {
        CHECK,
        CHECK_AND_UPDATE,
        DISABLED;

        /**
         * Parses a String to a setting
         * 
         * @param input The input String
         * @param def The default value
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
