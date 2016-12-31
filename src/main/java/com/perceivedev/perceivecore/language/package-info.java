// @formatter:off
/**
 * This package contains a small language system
 * <p>
 * <br>
 * It consists of the
 * {@link com.perceivedev.perceivecore.language.MessageProvider MessageProvider}
 * as the abstract base and the
 * {@link com.perceivedev.perceivecore.language.I18N I18N} as the concrete
 * implementation.
 * <p>
 * <br><b>Usage:</b>
 * <ul>
 *     <li>
 *         <b>Copy of default language files:</b>
 *         <br>You can copy default language files using:
 *         <br>{@link com.perceivedev.perceivecore.language.I18N#copyDefaultFiles(org.bukkit.plugin.java.JavaPlugin, boolean, java.lang.String)
 *              I18N#copyDefaultFiles(JavaPlugin plugin, boolean overwrite, String basePackage)}
 *         <ul>
 *             <li>
 *                 {@code plugin}
 *                 <br>is your plugin
 *             </li>
 *             <li>
 *                 {@code overwrite}
 *                 <br>states whether existing files should be overwritten
 *             </li>
 *             <li>
 *                 {@code basePackage}
 *                 <br>is the path to the default language files (their folder) in the jar file.
 *                 <br>It will be copied to the same path, just relative to the data folder of the plugin.
 *                 <br>If my path in the jar is {@code "language"}, they will be copied to {@code "<data folder>/language"}
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>Translating stuff:</b>
 *         <br>I would advise you to have a look at the <a href="https://docs.oracle.com/javase/tutorial/i18n/">Oracle I18N trail</a>
 *         <br>But as far as this class is concerned, you use any of the {@code "trXX"} methods in the {@link com.perceivedev.perceivecore.language.MessageProvider MessageProvider}
 *             class
 *     </li>
 * </ul>
 */
// @formatter:on
package com.perceivedev.perceivecore.language;