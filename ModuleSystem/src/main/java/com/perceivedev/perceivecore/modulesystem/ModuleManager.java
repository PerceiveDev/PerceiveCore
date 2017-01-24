package com.perceivedev.perceivecore.modulesystem;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages the modules
 */
public enum ModuleManager {
    INSTANCE;

    private UrlInjector pluginInjector;
    private DependencyManager dependencyManager = new DependencyManager();

    // @formatter:off
    /**
     * <ul>
     *     <li>
     *         <b>Key:</b> The "{@link Class#getCanonicalName()}" of the {@link Module}
     *     </li>
     *     <li>
     *         <b>Value:</b> The {@link Module}
     *     </li>
     * </ul>
     */
    // @formatter:on
    private Map<String, Module> moduleMap = new HashMap<>();

    /**
     * Gets a module by its {@link Class}
     *
     * @param moduleClass The {@link Class} of the module
     *
     * @return The module, if found
     *
     * @see #getModule(String)
     */
    @SuppressWarnings("unused")
    public Optional<Module> getModule(Class<? extends Module> moduleClass) {
        return getModule(moduleClass.getCanonicalName());
    }

    /**
     * Gets a module by its {@link Class#getCanonicalName()}
     *
     * @param moduleClassName The {@link Class#getCanonicalName()} of the module
     *
     * @return The module, if found
     */
    public Optional<Module> getModule(String moduleClassName) {
        return Optional.ofNullable(moduleMap.get(moduleClassName));
    }

    /**
     * Gets a module by its {@link Module#getModuleName()}
     *
     * @param moduleName The {@link Module#getModuleName()}
     *
     * @return The module, if found
     */
    @SuppressWarnings("unused")
    public Optional<Module> getModuleByName(String moduleName) {
        return moduleMap.values().stream().filter(module -> module.getModuleName().equals(moduleName)).findAny();
    }

    /**
     * Checks if there is a module with the given name
     *
     * @param moduleName The name of the module
     *
     * @return True if there is a module with the name
     */
    public boolean hasModuleWithName(String moduleName) {
        return getModuleByName(moduleName).isPresent();
    }

    /**
     * Adds the module, overwriting ones with the same class name
     *
     * @param module The module to add
     */
    public void registerModule(Module module) {
        moduleMap.put(module.getClass().getCanonicalName(), module);
        addToClassPath(module.getClass().getProtectionDomain().getCodeSource().getLocation());
    }

    /**
     * Adds the modules, overwriting ones with the same class name
     *
     * @param modules The modules to add
     *
     * @see #registerModule(Module)
     */
    public void registerModules(Collection<Module> modules) {
        modules.forEach(this::registerModule);
    }

    /**
     * Registers a JavaPlugin, downloading all needed dependencies
     *
     * @param plugin The {@link JavaPlugin} to register
     *
     * @return True if the plugin was registered successfully
     */
    public boolean registerPlugin(JavaPlugin plugin) {
        return dependencyManager.registerPlugin(plugin);
    }

    /**
     * @return The amount of modules in the manager
     */
    public int getModuleAmount() {
        return moduleMap.size();
    }

    /**
     * Specifies the JavaPlugin whose ClassPath will be modified.
     * <p>
     * <br>
     * <b><i>Unless you have a <u>very</u> good reason, do not call this method</i></b>
     *
     * @param plugin The {@link JavaPlugin} to inject the classes to
     */
    public void setCorePlugin(JavaPlugin plugin) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (!(classLoader instanceof URLClassLoader)) {
            throw new IllegalArgumentException("ClassLoader does not extend URLClassLoader: " + classLoader);
        }
        pluginInjector = new UrlInjector((URLClassLoader) classLoader);
    }

    /**
     * Removes all references to the modules.
     * <p>
     * The manager can not be reused after this call. A new instance will need to be created.
     * <p>
     * <br>
     * <b><i>Unless you have a <u>very</u> good reason, do not call this method</i></b>
     */
    public void nullify() {
        moduleMap = null;
        pluginInjector = null;
    }


    /**
     * @param url The {@link URL} to add to the ClassPath
     */
    void addToClassPath(URL url) {
        pluginInjector.addUrl(url);
    }
}
