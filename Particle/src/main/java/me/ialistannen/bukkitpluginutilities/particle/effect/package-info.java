/**
 * This package holds some base classes for displaying particles.
 * <p>
 * All classes require Bukkit 1.9+, as they use the Particle and
 * {@code World#sendParticle} introduced then
 * <p>
 * <br>
 * The <i><b>sending</b></i> of particles seems to be thread safe, so you can
 * probably tick the effects Async (using e.g. the
 * {@link me.ialistannen.bukkitpluginutilities.particle.ticker.StandardTicker#THREAD
 * StandardTicker#THREAD} or
 * {@link me.ialistannen.bukkitpluginutilities.particle.ticker.StandardTicker#ASYNC_BUKKIT_RUNNABLE
 * StandardTicker#ASYNC_BUKKIT_RUNNABLE})
 */
package me.ialistannen.bukkitpluginutilities.particle.effect;