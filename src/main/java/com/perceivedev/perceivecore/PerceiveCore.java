package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GUIListener;
import com.perceivedev.perceivecore.guisystem.PlayerGuiManager;
import com.perceivedev.perceivecore.other.DisableManager;

public class PerceiveCore extends JavaPlugin {
    private static PerceiveCore instance;

    private Logger logger;

    @SuppressWarnings("unused")
    private GUIListener guiListener;

    private PlayerGuiManager playerGuiManager;
    private DisableManager   disableManager;

    @Override
    public void onEnable() {
        instance = this;

        logger = getLogger();

        guiListener = new GUIListener(this);

        disableManager = new DisableManager();

        logger.info(versionText() + " enabled");

        Bukkit.getPluginManager().registerEvents((playerGuiManager = new PlayerGuiManager()), this);
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
     * @return The {@link PlayerGuiManager}
     */
    public PlayerGuiManager getPlayerGuiManager() {
        return playerGuiManager;
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
