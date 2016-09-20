package com.perceivedev.perceivecore.config;

/**
 * The different ways the deserializer will respond when a variable fails to
 * load for any reason
 *
 * @author Rayzr
 */
public enum FailResponse {

    /**
     * Cancel the loading and return null
     */
    CANCEL_LOAD,
    /**
     * Just use the default value. This is the default behaviour, so effectively
     * does nothing.
     */
    USE_DEFAULT,
    /**
     * Send error to the console
     */
    CONSOLE_ERR

}
