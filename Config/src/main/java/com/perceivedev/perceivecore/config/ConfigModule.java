package com.perceivedev.perceivecore.config;


import com.perceivedev.perceivecore.modulesystem.AbstractModule;

/**
 * The config module. Helps dealing with YML configurations
 */
public class ConfigModule extends AbstractModule {

    /**
     * Creates a new {@link ConfigModule} instance
     */
    public ConfigModule() {
        super(getModulePropertiesFromJar(ConfigModule.class));
    }
}
