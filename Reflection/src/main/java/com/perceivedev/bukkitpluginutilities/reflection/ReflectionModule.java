package com.perceivedev.bukkitpluginutilities.reflection;

import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;

/**
 * The reflection module
 */
public class ReflectionModule extends AbstractModule {

    /**
     * Creates the reflection module
     */
    public ReflectionModule() {
        super(getModulePropertiesFromJar(ReflectionModule.class));
    }
}
