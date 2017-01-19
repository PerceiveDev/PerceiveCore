package com.perceivedev.perceivecore.updater;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

/**
 * An updater for your plugins
 */
public class UpdaterModule extends AbstractModule {

    public UpdaterModule() {
        super(getModulePropertiesFromJar(UpdaterModule.class));
    }
}
