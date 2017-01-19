package com.perceivedev.perceivecore.coreplugin;

import java.nio.file.Path;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.modulesystem.Module;
import com.perceivedev.perceivecore.modulesystem.ModuleLoader;
import com.perceivedev.perceivecore.modulesystem.ModuleManager;
import com.perceivedev.perceivecore.utilities.disable.DisableManager;

public final class PerceiveCore extends JavaPlugin {

    private final Path MODULE_PATH = getDataFolder().toPath().resolve("modules");

    private static PerceiveCore instance;
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


        if (!ModuleManager.INSTANCE.getModuleByName("Utilities").isPresent()) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "===="
                            + ChatColor.AQUA + " Start of PerceiveCore error"
                            + ChatColor.RED + "===="
            );
            getLogger().severe("'Utilities' module not installed." +
                    " This plugin will not function correctly and therefore shut down.");
            getLogger().severe("Expect errors following this message.");
            getPluginLoader().disablePlugin(this);
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "===="
                            + ChatColor.AQUA + " End of PerceiveCore error"
                            + ChatColor.RED + "===="
            );
            return;
        }

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
    public static PerceiveCore getInstance() {
        return instance;
    }

}
