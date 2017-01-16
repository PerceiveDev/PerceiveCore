package com.perceivedev.bukkitpluginutilities.modulesystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Throwables;

/**
 * Manages plugins depending on the core
 */
class DependencyManager {

    private static final Logger LOGGER = Logger.getLogger("DependencyManager");
    private static final String LOGGER_PREFIX = "[DependencyManager] ";

    private static Method getFileMethod;

    static {
        try {
            getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, LOGGER_PREFIX + "Couldn't obtain reference to getFile method", e);
        }
    }

    /**
     * Adds a JavaPlugin, resolving all needed dependencies
     *
     * @param plugin The {@link JavaPlugin} to add
     */
    void registerPlugin(JavaPlugin plugin) {
        YamlConfiguration pluginYML = getPluginYML(plugin);
        if (!pluginYML.isList("module.dependencies")) {
            LOGGER.warning(LOGGER_PREFIX +
                    "Plugin '" + plugin.getName() + "' does not declare the dependencies in the plugin.yml " +
                    "under the key 'module.dependencies'"
            );
            return;
        }
        for (String dependency : pluginYML.getStringList("module.dependencies")) {
            if (!ModuleManager.INSTANCE.hasModuleWithName(dependency)) {
                LOGGER.warning(LOGGER_PREFIX +
                        "[" + plugin.getName() + "]"
                        + " Dependency '" + dependency + "' not found. Shutting plugin down!"
                );
                // TODO: 13.01.2017 Download it? 
                plugin.getPluginLoader().disablePlugin(plugin);
                return;
            }
        }
        String message = "Loaded plugin '" + plugin.getName() + "' with the following dependencies: ";
        message += String.join(", ", pluginYML.getStringList("module.dependencies"));
        LOGGER.info(LOGGER_PREFIX + message);
    }

    /**
     * Obtains a plugins plugin.yml
     *
     * @param plugin The {@link JavaPlugin} to get the plugin.yml for
     *
     * @return The plugin.yml
     */
    private YamlConfiguration getPluginYML(JavaPlugin plugin) {
        File pluginJar = invoke(getFileMethod, plugin);

        if (pluginJar == null) {
            return new YamlConfiguration();
        }

        YamlConfiguration pluginYML = new YamlConfiguration();

        try (JarFile jarFile = new JarFile(pluginJar)) {
            JarEntry pluginYMLEntry = jarFile.getJarEntry("plugin.yml");

            NioStreamReadUtil.doWithZipEntryStreamReader(jarFile, pluginYMLEntry, reader -> {
                try {
                    pluginYML.load(reader);
                } catch (IOException | InvalidConfigurationException e) {
                    throw Throwables.propagate(e);
                }
            });

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        return pluginYML;
    }

    /**
     * Invokes a method
     *
     * @param method The method to invoke
     * @param handle The handle to invoke it on
     * @param params The parameters
     * @param <T> The type of the return value
     *
     * @return The return value
     */
    private <T> T invoke(Method method, Object handle, Object... params) {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(handle, params);
            return result;
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, LOGGER_PREFIX + "Error invoking method " + method, e);
        }

        return null;
    }
}
