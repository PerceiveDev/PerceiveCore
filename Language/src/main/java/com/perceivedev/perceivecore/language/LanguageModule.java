package com.perceivedev.perceivecore.language;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

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
