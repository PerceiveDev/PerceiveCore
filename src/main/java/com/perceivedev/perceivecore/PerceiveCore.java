package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.guireal.GuiManager;
import com.perceivedev.perceivecore.other.DisableManager;

public class PerceiveCore extends JavaPlugin {
    private static PerceiveCore instance;

    private Logger logger;

    private GuiManager     guiManager;
    private DisableManager disableManager;

    @Override
    public void onEnable() {
        instance = this;

        logger = getLogger();

        disableManager = new DisableManager();

        logger.info(versionText() + " enabled");

        Bukkit.getPluginManager().registerEvents((guiManager = new GuiManager()), this);
    }

    @Override
    public void onDisable() {
        disableManager.disable();
        logger.info(versionText() + " disabled");
        // prevent the old instance from still being around.
        instance = null;
    }

    public String versionText() {
        return getName() + " v" + getDescription().getVersion();
    }

    /**
     * @return The {@link DisableManager}
     */
    public DisableManager getDisableManager() {
        return disableManager;
    }

    /**
     * @return The {@link GuiManager}
     */
    public GuiManager getGuiManager() {
        return guiManager;
    }

    /**
     * Returns the plugins instance
     *
     * @return The plugin instance
     */
    public static PerceiveCore getInstance() {
        return instance;
    }
}
