package com.perceivedev.perceivecore.reflection;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

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
