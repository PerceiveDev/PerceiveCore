package me.ialistannen.bukkitpluginutilities.modulesystem;

import java.util.Collections;

/**
 * The module for the modular system
 */
public class ModuleSystemModule extends AbstractModule {

    /**
     * Creates a new {@link ModuleSystemModule}
     */
    public ModuleSystemModule() {
        super(
                "ModuleSystem",
                "The module representing the module system.",
                "1.0.0-SNAPSHOT",
                Collections.emptyList()
        );
    }
}
