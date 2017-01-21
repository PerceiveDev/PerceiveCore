package com.perceivedev.perceivecore.coreplugin;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.modulesystem.Module;
import com.perceivedev.perceivecore.modulesystem.ModuleLoader;
import com.perceivedev.perceivecore.modulesystem.ModuleLoader.PostponedMessage;
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

        saveDefaultConfig();

        getLogger().info("I have loaded "
                + ModuleManager.INSTANCE.getModuleAmount()
                + " module(s) at class construction.");

        if (!moduleLoader.getPostponedMessages().isEmpty()) {
            printPostponed(moduleLoader);
        }

        if (!ModuleManager.INSTANCE.getModuleByName("Utilities").isPresent()) {
            printError("error",
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
     * Prints all {@link PostponedMessage}s
     *
     * @param loader The {@link ModuleLoader} to get the {@link PostponedMessage}s from
     */
    private void printPostponed(ModuleLoader loader) {
        if (getConfig().getBoolean("print-raw")) {
            getLogger().warning("I have a few things to tell you:");
        }
        else {
            Bukkit.getConsoleSender().sendMessage(color("&cI got a few things to tell you!"));
        }
        for (PostponedMessage postponedMessage : loader.getPostponedMessages()) {
            String type = postponedMessage.getLevel().toString().toLowerCase();
            printError(
                    type,
                    postponedMessage.getError(),
                    postponedMessage.getReason(),
                    postponedMessage.getDescription(),
                    postponedMessage.getSolution()
            );
        }
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
    private void printError(String type, String error, String reason, String description, String solution) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        builder.append(color("&c==== Start of PerceiveCore " + type + " ===="));
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
        builder.append(color("&c==== End of PerceiveCore " + type + " ===="));

        if (getConfig().getBoolean("print-raw")) {
            getLogger().warning(builder.toString());
        }
        else {
            Bukkit.getConsoleSender().sendMessage(builder.toString());
        }
    }

    /**
     * Colors a String
     *
     * @param message The message to color
     *
     * @return The colored message
     */
    private String color(String message) {
        if (getConfig().getBoolean("print-raw")) {
            return stripColor(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * @param input The input, with '&' as color char
     *
     * @return A clean version of it input, ready to be passed to {@link Logger#log(Level, String)}
     */
    private String stripColor(String input) {
        return input.replaceAll("(?i)&[0-9a-fklmno]", "");
    }
}
