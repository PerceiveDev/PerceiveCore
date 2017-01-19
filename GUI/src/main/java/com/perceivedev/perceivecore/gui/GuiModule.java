package com.perceivedev.perceivecore.gui;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

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
