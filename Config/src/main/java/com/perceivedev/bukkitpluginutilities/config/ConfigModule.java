package com.perceivedev.bukkitpluginutilities.config;


import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;

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
