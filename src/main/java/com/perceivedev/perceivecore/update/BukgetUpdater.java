package com.perceivedev.perceivecore.update;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Handles updating using the Bukget API
 *
 * @author ZP4RKER
 */
public class BukgetUpdater extends Updater {

    private String slug;

    /**
     * Default Constructor
     * 
     * @param plugin JavaPlugin instance
     * @param slug The plugin slug on BukkitDev
     */
    public BukgetUpdater(JavaPlugin plugin, String slug) {
        super(plugin);
        this.slug = slug;
    }

    @Override
    String getLatestVersion() {
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + slug + "/latest");
        JSONObject json = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                json = (JSONObject) parser.parse(data);
                return json.get("version").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    String getDownload() {
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + slug + "/latest");
        JSONObject json = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                json = (JSONObject) parser.parse(data);
                return json.get("download").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
