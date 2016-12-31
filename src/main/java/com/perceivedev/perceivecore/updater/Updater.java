package com.perceivedev.perceivecore.updater;

/**
 * A base updater
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
     * The result of updating
     */
    enum UpdateResult {
        SUCCESSFULLY_COPIED_TO_UPDATE_DIR,
        ERROR_WHILE_DOWNLOADING,
        ERROR_WHILE_CREATING_OUTPUT_FOLDER,
        ERROR_WHILE_COPYING
    }

    /**
     * The result of checking for an update
     */
    enum UpdateCheckResult {
        NO_NEW_VERSION,
        UPDATE_FOUND,
        NOT_SEARCHED,
        ERROR_SEARCHING
    }
}
