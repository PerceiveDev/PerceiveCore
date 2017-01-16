package me.ialistannen.bukkitpluginutilities.updater;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;

/**
 * An updater for your plugins
 */
public class UpdaterModule extends AbstractModule {

    public UpdaterModule() {
        super(getModulePropertiesFromJar(UpdaterModule.class));
    }
}
