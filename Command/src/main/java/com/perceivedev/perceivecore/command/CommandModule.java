package com.perceivedev.perceivecore.command;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

/**
 * Allows you do specify commands
 */
public class CommandModule extends AbstractModule {

    /**
     * Constructs a new {@link CommandModule}
     */
    public CommandModule() {
        super(getModulePropertiesFromJar(CommandModule.class));
    }
}
