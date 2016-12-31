package com.perceivedev.perceivecore.updater.impl.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.updater.UpdateStrategy;
import com.perceivedev.perceivecore.updater.Updater;
import com.perceivedev.perceivecore.updater.UpdaterEntry;

/**
 * An {@link Updater} using the Curse API
 */
public class CurseAPIUpdater extends AbstractUpdater {

    private static final Gson GSON = new Gson();
    private static final String BASE_URL = "https://api.curseforge.com/servermods/files";

    private long slug;

    private List<UpdaterEntry> entryList;

    private Function<String, String> versionFromName = Function.identity();

    /**
     * @param plugin The {@link JavaPlugin} this updater belongs to
     * @param slug The slug to use
     */
    public CurseAPIUpdater(JavaPlugin plugin, long slug) {
        super(plugin);
        this.slug = slug;
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
     *            from the name
     */
    public void setVersionFromName(Function<String, String> versionFromName) {
        this.versionFromName = versionFromName;
    }

    /**
     * Checks for updates.
     * <p>
     * A blocking operation
     *
     * @return The result of the check
     */
    @Override
    public UpdateCheckResult searchForUpdate() {
        String fullUrl = BASE_URL + "?projectIds=" + slug;

        CurseProjectResponse[] elements = getCurseResponse(fullUrl);

        LocalDateTime lastTime = LocalDateTime.now().minusDays(20);

        entryList = new ArrayList<>(elements.length);

        for (CurseProjectResponse element : elements) {
            URL url;
            try {
                url = new URL(element.downloadUrl.replace("servermods.cursecdn.com", "addons-origin.cursecdn.com"));
            } catch (MalformedURLException e) {
                getPlugin().getLogger().log(
                        Level.WARNING,
                        "[Updater] Got illegal URL from curse: '" + element.downloadUrl + "'");
                continue;
            }

            String version = versionFromName.apply(element.name);
            UpdaterEntry entry = new UpdaterEntry(element.name, version, lastTime, url);

            // filter invalid
            if (getUpdateStrategy().identifierFromEntry(entry) == null) {
                getPlugin().getLogger().log(Level.FINE, "Skipped invalid entry: '" + entry.getName() + "'");
                continue;
            }

            entryList.add(entry);
            lastTime = lastTime.plusSeconds(10);
        }

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

        if (entryList.isEmpty()) {
            return UpdateCheckResult.NO_NEW_VERSION;
        }

        String pluginVersion = getPlugin().getDescription().getVersion();

        UpdaterEntry newestEntry = entryList.get(0);

        // it is exactly the same
        if (newestEntry.getVersion().equalsIgnoreCase(pluginVersion)) {
            return UpdateCheckResult.NO_NEW_VERSION;
        }

        File pluginJar = returnPluginJar(getPlugin());

        UpdaterEntry thisPluginJar = new UpdaterEntry(
                getPlugin().getName(),
                getPlugin().getDescription().getVersion(),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(pluginJar.lastModified()), ZoneId.systemDefault()),
                null);

        {
            Object newestIdentifier = strategy.identifierFromEntry(newestEntry);
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
     * Updates this plugin, by downloading and copying the file
     *
     * @return The result of updating
     *
     * @throws IllegalStateException if {@link #searchForUpdate()} wasn't called
     *             before
     */
    @Override
    public UpdateResult update() {
        return downloadAndCopy(getEntryList().get(0), Function.identity());
    }

    /**
     * @return All entries pulled in the last {@link #searchForUpdate()} method
     *         call
     */
    public List<UpdaterEntry> getEntryList() {
        return Collections.unmodifiableList(entryList);
    }

    /**
     * Reads a website and returns its contents as JSON
     * 
     * @param urlString The url of the website
     * @return The parsed JSONArray
     */
    private CurseProjectResponse[] getCurseResponse(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }

        try (InputStream inputStream = url.openStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader)) {

            return GSON.fromJson(reader, CurseProjectResponse[].class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from the curse API", e);
        }
    }

    private static class CurseProjectResponse {
        private String downloadUrl, fileName, fileUrl, gameVersion, md5, name, releaseType;
        private long projectId;

        @Override
        public String toString() {
            return "\nCurseProjectResponse{" +
                    "\n\tdownloadUrl='" + downloadUrl + '\'' +
                    "\n\t, fileName='" + fileName + '\'' +
                    "\n\t, fileUrl='" + fileUrl + '\'' +
                    "\n\t, gameVersion='" + gameVersion + '\'' +
                    "\n\t, md5='" + md5 + '\'' +
                    "\n\t, name='" + name + '\'' +
                    "\n\t, releaseType='" + releaseType + '\'' +
                    "\n\t, projectId=" + projectId +
                    "}\n";
        }
    }
}
