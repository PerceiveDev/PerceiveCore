package me.ialistannen.bukkitpluginutilities.reflection;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;

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
