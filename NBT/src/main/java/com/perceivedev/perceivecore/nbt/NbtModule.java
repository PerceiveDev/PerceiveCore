package com.perceivedev.perceivecore.nbt;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;
import com.perceivedev.perceivecore.modulesystem.Module;

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
