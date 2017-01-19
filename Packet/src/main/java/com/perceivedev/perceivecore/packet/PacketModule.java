package com.perceivedev.perceivecore.packet;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

/**
 * Adds some basic packet support.
 */
public class PacketModule extends AbstractModule {

    /**
     * Creates a new PacketModule
     */
    public PacketModule() {
        super(getModulePropertiesFromJar(PacketModule.class));
    }
}
