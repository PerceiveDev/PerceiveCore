package com.perceivedev.bukkitpluginutilities.nbt;

import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;
import com.perceivedev.bukkitpluginutilities.modulesystem.Module;

/**
 * The NBT {@link Module}
 */
public class NbtModule extends AbstractModule {

    /**
     * Creates the NBT module
     */
    public NbtModule() {
        super(getModulePropertiesFromJar(NbtModule.class));
    }
}
