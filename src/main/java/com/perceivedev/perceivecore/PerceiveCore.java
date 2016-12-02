package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GuiManager;
import com.perceivedev.perceivecore.other.DisableManager;

public class PerceiveCore extends JavaPlugin {
    private static PerceiveCore instance;

    private Logger logger;
    private DisableManager disableManager;

    @Override
    public void onEnable() {
        instance = this;

        logger = getLogger();

        disableManager = new DisableManager(this);

        logger.info(versionText() + " enabled");

        Bukkit.getPluginManager().registerEvents(GuiManager.INSTANCE, this);
    }

    @Override
    public void onDisable() {
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
     * This is the same as {@link GuiManager#INSTANCE} and only exists for
     * plugins depending on this method
     * 
     * @return The {@link GuiManager}
     */
    public GuiManager getGuiManager() {
        return GuiManager.INSTANCE;
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
