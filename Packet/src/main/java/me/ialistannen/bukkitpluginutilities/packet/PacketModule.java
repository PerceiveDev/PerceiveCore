package me.ialistannen.bukkitpluginutilities.packet;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;

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
