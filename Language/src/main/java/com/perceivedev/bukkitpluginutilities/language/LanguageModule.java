package com.perceivedev.bukkitpluginutilities.language;

import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;

/**
 * Translations!
 */
public class LanguageModule extends AbstractModule {

    /**
     * Creates the language module
     */
    public LanguageModule() {
        super(getModulePropertiesFromJar(LanguageModule.class));
    }
}
