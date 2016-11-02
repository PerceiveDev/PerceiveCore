package com.perceivedev.perceivecore.command;

/** The result of executing a command */
public enum CommandResult {

    /** If the player has no permission */
    NO_PERMISSION,
    /** If the usage should be send */
    SEND_USAGE,
    /**
     * If the command was successfully invoked. If it completed successfully is
     * not asked here.
     */
    SUCCESSFULLY_INVOKED,
    /** If the command was not found. */
    NOT_FOUND,
    /**
     * If a critical error occurred invoking the command. Normally indicates
     * errors with the invocation system, NOT your command.
     */
    ERROR
}
