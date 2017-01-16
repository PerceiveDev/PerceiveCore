package me.ialistannen.bukkitpluginutilities.updater.impl.updater;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import me.ialistannen.bukkitpluginutilities.updater.Updater;
import me.ialistannen.bukkitpluginutilities.updater.UpdaterEntry;

/**
 * An {@link Updater} using the Curse API
 */
public class CurseAPIUpdater extends AbstractUpdater {

    private static final Gson GSON = new Gson();
    private static final String BASE_URL = "https://api.curseforge.com/servermods/files";

    private long slug;

    /**
     * @param plugin The {@link JavaPlugin} this updater belongs to
     * @param slug The slug to use
     */
    @SuppressWarnings("unused")
    public CurseAPIUpdater(JavaPlugin plugin, long slug) {
        super(plugin);

        Preconditions.checkArgument(slug > 0, "Slug must be greater than 0!");

        this.slug = slug;
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
        if (getUpdateCheckSettings() == UpdateCheckSettings.DISABLED) {
            return UpdateCheckResult.DISABLED;
        }
        String fullUrl = BASE_URL + "?projectIds=" + slug;

        CurseProjectResponse[] elements = getCurseResponse(fullUrl);

        LocalDateTime lastTime = LocalDateTime.now().minusDays(20);

        List<UpdaterEntry> entryList = new ArrayList<>(elements.length);

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

            String version = getVersionFromName().apply(element.name);
            UpdaterEntry entry = new UpdaterEntry(element.name, version, lastTime, url);

            // filter invalid
            if (getUpdateStrategy().identifierFromEntry(entry) == null) {
                getPlugin().getLogger().log(Level.FINE, "Skipped invalid entry: '" + entry.getName() + "'");
                continue;
            }

            entryList.add(entry);
            lastTime = lastTime.plusSeconds(10);
        }

        sortUpdaterEntries(entryList);

        setEntryList(entryList);

        if (entryList.isEmpty()) {
            return UpdateCheckResult.NO_NEW_VERSION;
        }

        return compareEntryWithCurrentlyRunning(entryList.get(0));
    }

    /**
     * Updates this plugin, by downloading and copying the file
     *
     * @return The result of updating
     *
     * @throws IllegalStateException if {@link #searchForUpdate()} wasn't called
     *                               before
     */
    @Override
    public UpdateResult update() {
        return getEntryList().isEmpty()
               ? UpdateResult.NO_UPDATE_FOUND
               : downloadAndCopy(getEntryList().get(0), getFinalNameTransform());
    }

    /**
     * Reads a website and returns its contents as JSON
     *
     * @param urlString The url of the website
     *
     * @return The parsed JSONArray
     *
     * @throws RuntimeException Wrapping a {@link JsonSyntaxException}, if the
     *                          JSON is malformed
     * @see #readWebsiteContent(String)
     */
    private CurseProjectResponse[] getCurseResponse(String urlString) {
        try {
            return GSON.fromJson(readWebsiteContent(urlString), CurseProjectResponse[].class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Error reading from Curse API, JSON malformed", e);
        }
    }

    @SuppressWarnings("unused")
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
