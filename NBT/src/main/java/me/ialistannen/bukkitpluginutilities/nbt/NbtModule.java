package me.ialistannen.bukkitpluginutilities.nbt;

import me.ialistannen.bukkitpluginutilities.modulesystem.AbstractModule;
import me.ialistannen.bukkitpluginutilities.modulesystem.Module;

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
