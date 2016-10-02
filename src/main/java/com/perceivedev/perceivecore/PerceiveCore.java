package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GUIListener;
import com.perceivedev.perceivecore.guisystem.PlayerGuiManager;
import com.perceivedev.perceivecore.guisystem.TestListener;

public class PerceiveCore extends JavaPlugin {
    private static PerceiveCore instance;

    private Logger      logger;

    @SuppressWarnings("unused")
    private GUIListener guiListener;

    private PlayerGuiManager playerGuiManager;
    
    @Override
    public void onEnable() {
        instance = this;

        logger = getLogger();

        guiListener = new GUIListener(this);

        logger.info(versionText() + " enabled");

        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
        Bukkit.getPluginManager().registerEvents((playerGuiManager = new PlayerGuiManager()), this);
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
