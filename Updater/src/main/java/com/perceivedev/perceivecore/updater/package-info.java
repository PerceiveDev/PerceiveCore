// @formatter:off
/**
 * This package contains some updater
 * <p>
 * <br><b>Classes:</b>
 * <ul>
 *     <li>
 *         <b>{@link com.perceivedev.perceivecore.updater.impl.updater.CurseAPIUpdater CurseAPIUpdater}</b>
 *         <br>Allows you to update plugins uploaded to BukkitDev
 *     </li>
 *     <li>
 *         <b>{@link com.perceivedev.perceivecore.updater.impl.updater.SpigetUpdater SpigetUpdater}</b>
 *         <br>Allows you to update plugins uploaded to Spigot
 *     </li>
 * </ul>
 * 
 * <p>
 * <br><b>General workflow:</b>
 * <ol>
 *     <li>
 *         <b>{@link com.perceivedev.perceivecore.updater.Updater#searchForUpdate() Updater#searchForUpdate()}</b>
 *         <br>This will return an {@link com.perceivedev.perceivecore.updater.Updater.UpdateCheckResult UpdateCheckResult}.
 *             Decide what you want to do with it, maybe notify, check if auto-update is on.
 *         <br>You can also check {@link com.perceivedev.perceivecore.updater.Updater#getUpdateCheckSettings() Updater#getUpdateCheckSettings()}, which returns
 *             the global settings for PcCore, but both updaters will respect that anyways.
 *     </li>
 *     <li>
 *         <b>{@link com.perceivedev.perceivecore.updater.Updater#update() Updater#update()}</b>
 *         <br>This will download the file and copy it in the {@code "update"} folder.
 *             If it has the same name as the current jar, it will be replaced at an reload/restart.
 *         <br>You can enforce that by providing a final name transformation in both Updaters 
 *     </li>
 * </ol>
 */
// @formatter:on
package com.perceivedev.perceivecore.updater;