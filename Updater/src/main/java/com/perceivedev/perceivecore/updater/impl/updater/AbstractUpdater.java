package com.perceivedev.perceivecore.updater.impl.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.perceivedev.perceivecore.coreplugin.PerceiveCore;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
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
    private Function<String, String> finalNameTransform = Function.identity();
    private Function<String, String> versionFromName = Function.identity();

    private List<UpdaterEntry> entryList;

    /**
     * @param plugin The plugin that owns this updater
     */
    @SuppressWarnings("WeakerAccess")
    public AbstractUpdater(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin can not be null!");

        this.plugin = plugin;
    }

    /**
     * @return The {@link UpdateStrategy}
     */
    @SuppressWarnings("WeakerAccess")
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
     * @param finalNameTransform The function transforming the file name to the
     * final name of the jar
     */
    @SuppressWarnings("unused")
    public void setFinalNameTransform(Function<String, String> finalNameTransform) {
        this.finalNameTransform = finalNameTransform;
    }

    /**
     * @return The function transforming the file name to the final name of the
     * jar
     */
    @SuppressWarnings("WeakerAccess")
    protected Function<String, String> getFinalNameTransform() {
        return finalNameTransform;
    }

    /**
     * This extracts the raw version (XX.XX.XX) from the name
     * <p>
     * <br>
     * <b>Example:</b>
     * <br>
     * "DecoHeads v1.4" {@code ==>} "1.4"
     * <br>
     * "Test plugin version 1.6.3" {@code ==>} "1.6.3"
     *
     * @param versionFromName This function extracts the raw version (XX.XX.XX)
     * from the name
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setVersionFromName(Function<String, String> versionFromName) {
        this.versionFromName = versionFromName;
    }

    /**
     * @return The function extracting the raw version (XX.XX.XX) from the name
     *
     * @see #setVersionFromName(Function)
     */
    @SuppressWarnings("WeakerAccess")
    protected Function<String, String> getVersionFromName() {
        return versionFromName;
    }

    /**
     * @return The plugin that owns this updater
     */
    @SuppressWarnings("WeakerAccess")
    protected JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public List<UpdaterEntry> getEntryList() {
        return Collections.unmodifiableList(entryList);
    }

    /**
     * @return All entries pulled in the last {@link #searchForUpdate()} method
     * call
     */
    @SuppressWarnings("WeakerAccess")
    protected List<UpdaterEntry> getEntryListModifiable() {
        return entryList;
    }

    /**
     * @param entryList A list containing all the {@link UpdaterEntry}s this
     * updater fetched. Modifiable.
     */
    @SuppressWarnings("WeakerAccess")
    protected void setEntryList(List<UpdaterEntry> entryList) {
        this.entryList = entryList;
    }

    /**
     * Sorts them according to the {@link #getUpdateStrategy()}, the highest
     * being the first
     *
     * @param entryList The list with {@link UpdaterEntry}s to sort
     */
    @SuppressWarnings("WeakerAccess")
    protected void sortUpdaterEntries(List<UpdaterEntry> entryList) {
        @SuppressWarnings("unchecked")
        UpdateStrategy<Object> strategy = (UpdateStrategy<Object>) getUpdateStrategy();

        entryList.sort((o1, o2) -> {
            Object identifierOne = strategy.identifierFromEntry(o1);
            Object identifierTwo = strategy.identifierFromEntry(o2);
            if (identifierOne == null && identifierTwo == null) {
                return 0;
            }
            if (identifierOne == null) {
                return -1;
            }
            if (identifierTwo == null) {
                return 1;
            }

            // sort highest to lowest
            return strategy.compare(identifierTwo, identifierOne);
        });
    }

    /**
     * Compares the given entry with the currently running version of the
     * plugin, returning {@link UpdateCheckResult#UPDATE_FOUND}, if the entry is
     * newer
     *
     * @param entry The entry to compare
     *
     * @return The result of the comparison
     */
    @SuppressWarnings("WeakerAccess")
    protected UpdateCheckResult compareEntryWithCurrentlyRunning(UpdaterEntry entry) {
        String pluginVersion = getPlugin().getDescription().getVersion();

        // it is exactly the same
        if (entry.getVersion().equalsIgnoreCase(pluginVersion)) {
            return UpdateCheckResult.NO_NEW_VERSION;
        }

        File pluginJar = returnPluginJar(getPlugin());

        UpdaterEntry thisPluginJar = new UpdaterEntry(
                getPlugin().getName(),
                getPlugin().getDescription().getVersion(),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(pluginJar.lastModified()), ZoneId.systemDefault()),
                null);

        @SuppressWarnings("unchecked")
        UpdateStrategy<Object> strategy = (UpdateStrategy<Object>) getUpdateStrategy();
        {
            Object newestIdentifier = strategy.identifierFromEntry(entry);
            Object thisIdentifier = strategy.identifierFromEntry(thisPluginJar);

            if (strategy.compare(thisIdentifier, newestIdentifier) >= 0) {
                return UpdateCheckResult.NO_NEW_VERSION;
            }
        }

        return UpdateCheckResult.UPDATE_FOUND;
    }

    private File returnPluginJar(JavaPlugin plugin) {
        return (File) ReflectionUtil
                .invokeMethod(JavaPlugin.class, new MethodPredicate().withName("getFile"), plugin)
                .getValueOrThrow();
    }

    /**
     * Downloads and copies the jar to the
     *
     * @param entry The entry to download and copy
     * @param nameTransformation The transformation to apply to
     * {@link UpdaterEntry#getName()}. The final name of the jar
     *
     * @return The result of updating
     */
    @SuppressWarnings("WeakerAccess")
    protected UpdateResult downloadAndCopy(UpdaterEntry entry, Function<String, String> nameTransformation) {
        if (getUpdateCheckSettings() != UpdateCheckSettings.CHECK_AND_UPDATE) {
            return UpdateResult.DISABLED;
        }

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

    /**
     * Resolves the redirects for an URL
     *
     * @param url The URL to query
     *
     * @return The url at the end of the redirect chain
     *
     * @throws IOException if an IO error occurred
     */
    @SuppressWarnings("WeakerAccess")
    protected URL resolveRedirects(String url) throws IOException {
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

    /**
     * Reads a website's contents
     *
     * @param urlString The URL to the website
     *
     * @return The website content
     *
     * @throws RuntimeException wrapping a {@link IOException}, if any occurs
     * @throws RuntimeException wrapping a {@link MalformedURLException} if the
     *                          passed {@code urlString} is
     *                          malformed
     */
    @SuppressWarnings("WeakerAccess")
    protected static String readWebsiteContent(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }

        try (InputStream inputStream = url.openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            StringBuilder result = new StringBuilder();
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                result.append(tmp)
                        .append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
