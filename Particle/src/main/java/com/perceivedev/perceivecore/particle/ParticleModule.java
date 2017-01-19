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
}
