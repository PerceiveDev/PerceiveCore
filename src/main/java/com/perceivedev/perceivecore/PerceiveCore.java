package com.perceivedev.perceivecore;

import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.gui.GUIListener;

public class PerceiveCore extends JavaPlugin {

    @SuppressWarnings("unused")
    private GUIListener guiListener;

    @Override
    public void onEnable() {
        guiListener = new GUIListener(this);
    }
}
