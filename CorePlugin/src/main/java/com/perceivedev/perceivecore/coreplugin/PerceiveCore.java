package com.perceivedev.perceivecore.coreplugin;

import java.nio.file.Path;
import java.util.Locale;
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
            printError(
                    "Module 'Utilities' not found",
                    "The Utilities module is not installed.",
                    String.format(Locale.ROOT,
                            "I searched in my modules folder (%s) and sadly couldn't find this module.",
                            MODULE_PATH.toString()
                    ),
                    String.format(Locale.ROOT,
                            "Copy the 'UtilitiesModule.jar' file into the modules folder (%s)",
                            MODULE_PATH.toString()
                    )
            );
            getPluginLoader().disablePlugin(this);
            return;
        }

        disableManager = new DisableManager(this);
    }

    @Override
    public void onDisable() {
        if (ModuleManager.INSTANCE.getModuleByName("Utilities").isPresent()) {
            disableManager.disable();
        }

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


    /**
     * Prints an error. You can leave any value at {@code null} to make it not appear
     *
     * @param error The error
     * @param reason The reason for the error
     * @param description A more detailed description of the error
     * @param solution The solution.
     */
    @SuppressWarnings("SameParameterValue")
    private void printError(String error, String reason, String description, String solution) {
        if (error != null) {
            getLogger().severe("Error: " + ChatColor.stripColor(error));
            getLogger().severe("More details follow in a formatted, colored way.");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        builder.append(color("&c==== Start of PerceiveCore error ===="));
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        boolean prevPrinted = false;
        if (error != null) {
            builder.append(System.lineSeparator());
            builder.append("  ").append(color("&4Error: &6" + System.lineSeparator() + error));
            prevPrinted = true;
        }

        if (reason != null) {
            if (prevPrinted) {
                builder.append(System.lineSeparator());
            }
            builder.append(System.lineSeparator());
            builder.append("  ").append(color("&cReason: &6" + System.lineSeparator() + reason));
            prevPrinted = true;
        }
        else {
            prevPrinted = false;
        }
        if (description != null) {
            if (prevPrinted) {
                builder.append(System.lineSeparator());
            }
            builder.append(System.lineSeparator());
            builder.append("  ").append(color("&bDescription: &6" + System.lineSeparator() + description));
            prevPrinted = true;
        }
        else {
            prevPrinted = false;
        }
        if (solution != null) {
            if (prevPrinted) {
                builder.append(System.lineSeparator());
            }
            builder.append(System.lineSeparator());
            builder.append("  ").append(color("&aSolution: &6" + System.lineSeparator() + solution));
        }
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(color("&c==== End of PerceiveCore error ===="));
        Bukkit.getConsoleSender().sendMessage(builder.toString());
    }

    /**
     * Colors a String
     *
     * @param message The message to color
     *
     * @return The colored message
     */
    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
