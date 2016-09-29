package com.perceivedev.perceivecore.update;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * API for updating plugins using both the Spiget and Bukget APIs
 *
 * @author ZP4RKER
 */
public class PluginUpdater {

    private JavaPlugin plugin;
    private Updater updater;

    /**
     * Constructor for a SpigotMC plugin.
     *
     * @param plugin JavaPlugin instance
     * @param resourceId SpigotMC resource ID
     */
    public PluginUpdater(JavaPlugin plugin, long resourceId) {
        this.plugin = plugin;
        //this.updater = new SpigetUpdater();
    }

    /**
     * Constructor for a BukkitDev plugin.
     *
     * @param plugin JavaPlugin instance
     */
    public PluginUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
        this.updater = new BukgetUpdater(plugin, plugin.getDescription().getName());
    }

    /**
     * Checks if an update is available.
     *
     * @return Whether or not there is an update available
     */
    public boolean checkForUpdates() {
        return updater.updateAvailable();
    }

    /**
     * Updates the plugin and sends response/output to Console only.
     */
    public void update() {
        updater.update(updater.getLatestVersion(), Bukkit.getConsoleSender());
    }

    /**
     * Updates the plugin and sends response/output to all senders
     *
     * @param senders Each ({@link CommandSender}) you want to send the output to
     */
    public void update(CommandSender... senders) {
        updater.update(updater.getLatestVersion(), senders);
    }

}
