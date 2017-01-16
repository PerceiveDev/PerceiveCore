package com.perceivedev.bukkitpluginutilities.particle;

import com.perceivedev.bukkitpluginutilities.modulesystem.AbstractModule;

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
}
