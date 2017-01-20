package com.perceivedev.perceivecore.particle;

import com.perceivedev.perceivecore.modulesystem.AbstractModule;

/**
 * Allows you to use some particle effects
 */
public class ParticleModule extends AbstractModule {

    /**
     * Creates the Particle module
     */
    public ParticleModule() {
        super(getModulePropertiesFromJar(ParticleModule.class));
    }

    @Override
    public boolean isModuleCompatible() {
        try {
            Class.forName("org.bukkit.Particle");
        } catch (ClassNotFoundException e) {
            getLogger().warning(
                    "[" + getModuleName() + "] " +
                            "Could not find the Particle enum. You are probably running on a version < 1.9"
            );
            return false;
        }
        return true;
    }
}
