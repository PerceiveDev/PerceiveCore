package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GUIListener;
import com.perceivedev.perceivecore.other.DisableManager;

public class PerceiveCore extends JavaPlugin {
    private static PerceiveCore instance;

    private Logger              logger;

    @SuppressWarnings("unused")
    private GUIListener         guiListener;

    private DisableManager      disableManager;

    @Override
    public void onEnable() {
        instance = this;

        logger = getLogger();

        guiListener = new GUIListener(this);

        disableManager = new DisableManager();

        logger.info(versionText() + " enabled");
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
     * Returns the plugins instance
     *
     * @return The plugin instance
     */
    public static PerceiveCore getInstance() {
        return instance;
    }
}
