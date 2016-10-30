package com.perceivedev.perceivecore.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author ZP4RKER
 */
public abstract class Updater {

    private JavaPlugin plugin;

    /**
     * Default constructor for Updater.
     *
     * @param plugin JavaPlugin instance
     */
    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a JSON string from a URL
     *
     * @param url The URL to get the string from
     * @return The JSON string
     */
    String getJSON(String url) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.connect();
            int response = connection.getResponseCode();
            switch (response) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Gets the latest plugin version.
     *
     * @return The latest plugin version
     */
    abstract String getLatestVersion();

    /**
     * Gets the download url for version.
     *
     * @param version The version to get the url for
     * @return The download url
     */
    abstract String getDownload(String version);

    /**
     * Updates the plugin.
     *
     * @param version The version to update to
     * @param senders The ({@link CommandSender})s to send the output to
     */
    void update(String version, CommandSender... senders) {
        String updateURL = getDownload(version);
        try {
            File to = new File(this.plugin.getServer().getUpdateFolderFile(), updateURL.substring(updateURL.lastIndexOf('/') + 1, updateURL.length()));
            File tmp = new File(to.getPath() + ".au");
            if (!tmp.exists()) {
                this.plugin.getServer().getUpdateFolderFile().mkdirs();
                tmp.createNewFile();
            }
            URL url = new URL(updateURL);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(tmp);
            byte[] buffer = new byte[1024];
            int fetched;
            while ((fetched = is.read(buffer)) != -1) {
                os.write(buffer, 0, fetched);
            }
            is.close();
            os.flush();
            os.close();
            if (to.exists()) {
                to.delete();
            }
            tmp.renameTo(to);
            sendMessages(TextUtils.colorize("§2Restart server to update!"));
        } catch (Exception e) {
            sendMessages(TextUtils.colorize("§4Failed to update!"), senders);
        }
    }

    /**
     * Sends message to all {@link CommandSender}s
     *
     * @param message The message to send
     * @param senders The {@link CommandSender}s to send to
     */
    private void sendMessages(String message, CommandSender... senders) {
        for (CommandSender sender : senders) {
            sender.sendMessage(message);
        }
    }

    /**
     * Gets the Minecraft version of the server.
     *
     * @return The Minecraft version
     */
    String getMCVersion() {
        return this.plugin.getServer().getBukkitVersion().substring(0, 3);
    }

    /**
     * Checks if there is an update available.
     *
     * @return Whether or not an update is available
     */
    boolean updateAvailable() {
        String currentVersion = plugin.getDescription().getVersion();
        if (!Objects.equals(getLatestVersion(), currentVersion)) {
            if (Double.parseDouble(currentVersion) < Double.parseDouble(getLatestVersion())) {
                return true;
            }
        }
        return false;
    }

}
