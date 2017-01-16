package me.ialistannen.bukkitpluginutilities.coreplugin;

import java.nio.file.Path;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import me.ialistannen.bukkitpluginutilities.modulesystem.Module;
import me.ialistannen.bukkitpluginutilities.modulesystem.ModuleLoader;
import me.ialistannen.bukkitpluginutilities.modulesystem.ModuleManager;
import me.ialistannen.bukkitpluginutilities.utilities.disable.DisableManager;

public final class CorePlugin extends JavaPlugin {

    private final Path MODULE_PATH = getDataFolder().toPath().resolve("modules");

    private static CorePlugin instance;
    private DisableManager disableManager;
    private ModuleLoader moduleLoader;

    {
        ModuleManager.INSTANCE.setCorePlugin(this);

        moduleLoader = new ModuleLoader(MODULE_PATH, getLogger());
        Set<Module> loadedModules = moduleLoader.load();
        getLogger().info("Loaded " + loadedModules.size() + " module(s).");
        ModuleManager.INSTANCE.registerModules(loadedModules);
    }

    public void onEnable() {
        instance = this;

        getLogger().info("I have loaded "
                + ModuleManager.INSTANCE.getModuleAmount()
                + " module(s) at class construction.");

        disableManager = new DisableManager(this);
    }

    @Override
    public void onDisable() {
        disableManager.disable();

        // prevent the old instance from still being around.
        instance = null;
        moduleLoader = null;

        ModuleManager.INSTANCE.nullify();
    }

    /**
     * @return The {@link DisableManager}
     */
    public DisableManager getDisableManager() {
        return disableManager;
    }

    /**
     * Returns the plugin instance
     *
     * @return The plugin instance
     */
    public static CorePlugin getInstance() {
        return instance;
    }

}
