package com.perceivedev.bukkitpluginutilities.updater;

import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;

/**
 * An updater for your plugins
 */
public class UpdaterModule extends AbstractModule {

    public UpdaterModule() {
        super(getModulePropertiesFromJar(UpdaterModule.class));
    }
}
