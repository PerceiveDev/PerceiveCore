package com.perceivedev.perceivecore;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GUIListener;

public class PerceiveCore extends JavaPlugin {

    private Logger      logger;

    @SuppressWarnings("unused")
    private GUIListener guiListener;

    @Override
    public void onEnable() {
        logger = getLogger();

        guiListener = new GUIListener(this);

        logger.info(versionText() + " enabled");
    }

    @Override
    public void onDisable() {
        logger.info(versionText() + " disabled");
    }

    public String versionText() {
        return getName() + " v" + getDescription().getVersion();
    }
}
