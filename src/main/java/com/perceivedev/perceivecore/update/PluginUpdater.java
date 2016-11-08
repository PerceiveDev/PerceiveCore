package com.perceivedev.perceivecore.update;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * API for updating plugins using both the Spiget and Bukget APIs
 *
 * @author ZP4RKER
 */
public class PluginUpdater {

	private Updater updater;
	private JavaPlugin plugin;

	/**
     * Default Constructor
     * 
     * @param plugin JavaPlugin instance
     * @param slug Slug of plugin on SpigotMC/BukkitDev
     */
    public PluginUpdater(JavaPlugin plugin, String slug) {
    	this.plugin = plugin;
    	if (NumberUtils.isNumber(slug)) {
    		// SpigotMC Plugin
    		this.updater = new SpigetUpdater(plugin, slug);
    	} else {
    		// BukkitDev Plugin
    		this.updater = new BukgetUpdater(plugin, slug);
    	}
    }

	/**
	 * Checks if an update is available.
	 *
	 * @return Whether or not there is an update available
	 */
	public boolean checkForUpdates() {
		return updater.updateAvailable();
	}

	/** Updates the plugin and sends response/output to Console only. */
	public void update() {
		updater.update(Bukkit.getConsoleSender());
	}

	/**
	 * Updates the plugin and sends response/output to all senders
	 *
	 * @param senders
	 *            Each ({@link CommandSender}) you want to send the output to
	 */
	public void update(CommandSender... senders) {
		updater.update(senders);
	}
	
	/**
	 * Asks the sender to confirm before updating
	 * 
	 * @param sender The {@link CommandSender} to ask
	 */
	public void updateWithConfirmation(CommandSender sender) {
		ConversationFactory cf = new ConversationFactory(plugin);
		ConfirmConversation confirm = new ConfirmConversation(this);
		Conversation conv = cf.withFirstPrompt(confirm)
							.withLocalEcho(true)
							.buildConversation((Conversable) sender);
		conv.begin();
	}

}
