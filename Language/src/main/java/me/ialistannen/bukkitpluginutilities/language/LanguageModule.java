package me.ialistannen.bukkitpluginutilities.language;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;

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
