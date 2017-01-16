package me.ialistannen.bukkitpluginutilities.gui;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;

/**
 * A module to allow working with guis
 */
public class GuiModule extends AbstractModule {

    /**
     * Creates the Gui module
     */
    public GuiModule() {
        super(getModulePropertiesFromJar(GuiModule.class));
    }
}
