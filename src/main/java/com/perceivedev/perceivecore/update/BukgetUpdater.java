package com.perceivedev.perceivecore.update;

import java.util.ArrayList;
import java.util.List;

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

    private String name;

    public BukgetUpdater(JavaPlugin plugin, String pluginName) {
        super(plugin);
        this.name = pluginName;
    }

    @Override
    String getLatestVersion() {
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + name);
        JSONObject jsonObject = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                jsonObject = (JSONObject) parser.parse(data);
            } catch (Exception localException) {}
        }
        String version = null;
        if ((jsonObject.get("versions") instanceof JSONArray)) {
            JSONArray versions = (JSONArray) jsonObject.get("versions");
            for (int i = 0; i < versions.size(); i++) {
                if (version != null) {
                    if ((((JSONArray) ((JSONObject) versions.get(i)).get("game_versions")).get(0).toString().contains(getMCVersion())) &&
                              (Long.parseLong(version) < Long.parseLong(((JSONObject) versions.get(i)).get("version").toString()))) {
                        version = ((JSONObject) versions.get(i)).get("version").toString();
                    }
                } else if (((JSONArray) ((JSONObject) versions.get(i)).get("game_versions")).get(0).toString().contains(getMCVersion())) {
                    version = ((JSONObject) versions.get(i)).get("version").toString();
                }
            }
        }
        return version;
    }

    @Override
    String getDownload(String versionString) {
        String data = getJSON("http://api.bukget.org/3/plugins/bukkit/" + name);
        JSONObject jsonObject = null;
        if (data != null) {
            JSONParser parser = new JSONParser();
            try {
                jsonObject = (JSONObject) parser.parse(data);
            } catch (Exception localException) {
            }
        }
        JSONArray versions = (JSONArray) jsonObject.get("versions");
        List<JSONObject> validVersions = new ArrayList<>();
        for (Object versionObj : versions) {
            JSONObject version = (JSONObject) versionObj;
            if (((JSONArray) version.get("game_versions")).get(0).toString().contains(getMCVersion())) {
                validVersions.add(version);
            }
        }
        JSONObject latest = null;
        for (JSONObject version : validVersions) {
            if (latest == null) {
                latest = version;
            }
            if (Long.parseLong(version.get("version").toString()) > Long.parseLong(latest.get("version").toString())) {
                latest = version;
            }
        }
        return latest.get("download").toString();
    }

}
