package com.perceivedev.perceivecore.update;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Handles updating using the Spiget API
 *
 * @author ZP4RKER
 */
public class SpigetUpdater extends Updater {

    private long id;

    public SpigetUpdater(JavaPlugin plugin, String slug) {
        super(plugin);
        this.id = Long.parseLong(slug);
    }

    @Override
    String getLatestVersion() {
        String data = getJSON("https://api.spiget.org/v2/resources/" + id + "/versions?spiget__ua=SpigetDocs");
        JSONObject json;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                json = (JSONObject) parser.parse(data);
                return json.get("name").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    String getDownload() {
        String data = getJSON("https://api.spiget.org/v2/resources/" + id + "?spiget__ua=SpigetDocs");
        JSONObject json;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                json = (JSONObject) parser.parse(data);
                JSONObject file = (JSONObject) json.get("file");
                return "https://spigotmc.org/" + file.get("url").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
