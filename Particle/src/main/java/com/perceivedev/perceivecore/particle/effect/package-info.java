/**
 * This package holds some base classes for displaying particles.
 * <p>
 * All classes require Bukkit 1.9+, as they use the Particle and
 * {@code World#sendParticle} introduced then
 * <p>
 * <br>
 * The <i><b>sending</b></i> of particles seems to be thread safe, so you can
 * probably tick the effects Async (using e.g. the
 * {@link com.perceivedev.perceivecore.particle.ticker.StandardTicker#THREAD
 * StandardTicker#THREAD} or
 * {@link com.perceivedev.perceivecore.particle.ticker.StandardTicker#ASYNC_BUKKIT_RUNNABLE
 * StandardTicker#ASYNC_BUKKIT_RUNNABLE})
 */
package com.perceivedev.perceivecore.particle.effect;