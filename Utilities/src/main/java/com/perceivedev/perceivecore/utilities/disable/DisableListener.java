package com.perceivedev.perceivecore.utilities.disable;

/**
 * An object that needs to be informed about Plugin deactivation
 */
@FunctionalInterface
public interface DisableListener {

    /**
     * Called when the plugin disables
     */
    void onDisable();
}
