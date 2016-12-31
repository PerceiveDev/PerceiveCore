package com.perceivedev.perceivecore.updater.impl.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.updater.UpdateStrategy;
import com.perceivedev.perceivecore.updater.Updater;
import com.perceivedev.perceivecore.updater.UpdaterEntry;
import com.perceivedev.perceivecore.updater.impl.other.StandardUpdateStrategy;

/**
 * A skeleton for the {@link Updater}
 */
public abstract class AbstractUpdater implements Updater {

    private UpdateStrategy<?> updateStrategy = StandardUpdateStrategy.TIME;

    private JavaPlugin plugin;

    /**
     * @param plugin The plugin that owns this updater
     */
    public AbstractUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return The {@link UpdateStrategy}
     */
    protected UpdateStrategy<?> getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * @param strategy The strategy used to update the plugin
     */
    @Override
    public void setUpdateStrategy(UpdateStrategy<?> strategy) {
        this.updateStrategy = strategy;
    }

    /**
     * @return The plugin that owns this updater
     */
    protected JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Downloads and copies the jar to the
     * 
     * @param entry The entry to download and copy
     * @param nameTransformation The transformation to apply to
     *            {@link UpdaterEntry#getName()}. The final name of the jar
     * @return The result of updating
     */
    protected UpdateResult downloadAndCopy(UpdaterEntry entry, Function<String, String> nameTransformation) {
        Objects.requireNonNull(entry, "entry can not be null!");
        Objects.requireNonNull(nameTransformation, "nameTransformation can not be null!");

        URL downloadUrl = entry.getDownloadUrl();

        Preconditions.checkArgument(downloadUrl != null, "Download URL was null!");

        Path updateFolder = PerceiveCore.getInstance()
                .getDataFolder()
                .getAbsoluteFile()
                .getParentFile()
                .toPath()
                .resolve("update");
        try {
            Files.createDirectories(updateFolder);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[Updater] Error creating the 'update' folder", e);
            return UpdateResult.ERROR_WHILE_CREATING_OUTPUT_FOLDER;
        }

        try (InputStream inputStream = resolveRedirects(downloadUrl.toExternalForm()).openStream()) {
            String name = nameTransformation.apply(entry.getName());
            if (!name.endsWith(".jar")) {
                name += ".jar";
            }
            Path file = updateFolder.resolve(name);

            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);

            return UpdateResult.SUCCESSFULLY_COPIED_TO_UPDATE_DIR;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[Updater] Error downloading the update", e);
            return UpdateResult.ERROR_WHILE_DOWNLOADING;
        }
    }

    private static URL resolveRedirects(String url) throws IOException {
        URL currentURL, base, next;
        HttpURLConnection connection;

        String currentUrlString = url;

        while (true) {
            currentURL = new URL(currentUrlString);

            connection = (HttpURLConnection) currentURL.openConnection();

            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setInstanceFollowRedirects(false);

            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP: {
                    String target = connection.getHeaderField("Location");
                    base = new URL(currentUrlString);
                    next = new URL(base, target);
                    currentUrlString = next.toExternalForm();
                    continue;
                }
            }
            break;
        }

        return connection.getURL();
    }
}
