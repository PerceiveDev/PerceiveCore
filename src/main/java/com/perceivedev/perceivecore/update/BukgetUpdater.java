package com.perceivedev.perceivecore.update;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
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
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + slug);
        JSONObject jsonObject = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                jsonObject = (JSONObject) parser.parse(data);
            } catch (Exception localException) {
            }
        }
        String version = null;
        if ((jsonObject.get("versions") instanceof JSONArray)) {
            JSONArray versions = (JSONArray) jsonObject.get("versions");
            for (int i = 0; i < versions.size(); i++) {
                if (version != null) {
                    if ((Long.parseLong(version) < Long.parseLong(((JSONObject) versions.get(i)).get("version").toString()))) {
                        version = ((JSONObject) versions.get(i)).get("version").toString();
                    }
                } else {
                    version = ((JSONObject) versions.get(i)).get("version").toString();
                }
            }
        }
        return version;
    }

    @Override
    String getDownload(String versionString) {
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + slug);
        JSONObject jsonObject = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                jsonObject = (JSONObject) parser.parse(data);
            } catch (Exception localException) {
            }
        }
        JSONArray versions = (JSONArray) jsonObject.get("versions");
        /*List<JSONObject> validVersions = new ArrayList<>();
        for (Object versionObj : versions) {
            JSONObject version = (JSONObject) versionObj;
            if (((JSONArray) version.get("game_versions")).get(0).toString().contains(getMCVersion())) {
                validVersions.add(version);
            }
        }*/
        JSONObject latest = null;
        /*for (JSONObject version : validVersions) {
            if (latest == null) {
                latest = version;
            }
            if (Long.parseLong(version.get("version").toString()) > Long.parseLong(latest.get("version").toString())) {
                latest = version;
            }
        }*/
        for (Object versionObj : versions) {
            if (latest == null) {
                latest = (JSONObject) versionObj;
            }
            if (Long.parseLong(((JSONObject) versionObj).get("version").toString()) > Long.parseLong(latest.get("version").toString())) {
                latest = (JSONObject) versionObj;
            }
        }
        return latest.get("download").toString();
    }

}
