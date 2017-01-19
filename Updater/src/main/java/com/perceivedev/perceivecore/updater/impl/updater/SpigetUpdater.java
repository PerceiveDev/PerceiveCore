package com.perceivedev.perceivecore.updater.impl.updater;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.perceivedev.perceivecore.updater.Updater;
import com.perceivedev.perceivecore.updater.UpdaterEntry;

/**
 * An updater for Spiget
 */
public class SpigetUpdater extends AbstractUpdater {

    /**
     * The base url. Format with
     * {@link String#format(Locale, String, Object...)}, first int is the
     * slug
     */
    private static final String BASE_URL = "https://api.spiget.org/v2/resources/%d/versions?size=1000";
    /**
     * The base url for downloading the latest version. Format with
     * {@link String#format(Locale, String, Object...)}, first int is the
     * slug
     */
    private static final String BASE_DOWNLOAD_URL = "https://api.spiget.org/v2/resources/%d/download";
    private static final Gson GSON = new Gson();

    private long slug;

    /**
     * @param plugin The plugin that owns this updater
     * @param slug The id of this plugin
     */
    @SuppressWarnings("unused")
    public SpigetUpdater(JavaPlugin plugin, long slug) {
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
        if (getUpdateCheckSettings() == Updater.UpdateCheckSettings.DISABLED) {
            return Updater.UpdateCheckResult.DISABLED;
        }

        String finalUrl = String.format(Locale.ROOT, BASE_URL, slug);
        String websiteContent = readWebsiteContent(finalUrl);
        SpigetResponse[] spigetResponses = GSON.fromJson(websiteContent, SpigetResponse[].class);

        setEntryList(new ArrayList<>(spigetResponses.length));
        for (SpigetResponse spigetResponse : spigetResponses) {
            LocalDateTime releaseDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(spigetResponse.releaseDate),
                    ZoneId
                            .systemDefault());
            URL url;
            try {
                url = new URL("http://www.spigotmc.org/" + spigetResponse.url);
            } catch (MalformedURLException e) {
                getPlugin().getLogger().log(
                        Level.WARNING,
                        "[Updater] Got illegal URL from SpiGet: '" + spigetResponse.url + "'");
                continue;
            }
            UpdaterEntry entry = new UpdaterEntry(spigetResponse.name, spigetResponse.name, releaseDate, url);
            getEntryListModifiable().add(entry);
        }

        if (getEntryList().isEmpty()) {
            return Updater.UpdateCheckResult.NO_NEW_VERSION;
        }

        sortUpdaterEntries(getEntryListModifiable());

        return compareEntryWithCurrentlyRunning(getEntryList().get(0));
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
    public Updater.UpdateResult update() {
        if (getEntryList().isEmpty()) {
            return Updater.UpdateResult.NO_UPDATE_FOUND;
        }

        UpdaterEntry realEntry = getEntryList().get(0);
        String fakedUrlString = String.format(Locale.ROOT, BASE_DOWNLOAD_URL, slug);
        URL fakedUrl;
        try {
            fakedUrl = new URL(fakedUrlString);
        } catch (MalformedURLException e) {
            getPlugin().getLogger()
                    .log(Level.WARNING, "Malformed URL, Url was STATICALLY added. This shouldn't happen!", e);
            return Updater.UpdateResult.ERROR_WHILE_DOWNLOADING;
        }
        UpdaterEntry fakedSpigetUrl = new UpdaterEntry(realEntry.getName(), realEntry.getVersion(), realEntry
                .getReleaseTime(), fakedUrl);

        return downloadAndCopy(fakedSpigetUrl, getFinalNameTransform());
    }

    @SuppressWarnings("unused")
    private static class SpigetResponse {
        private String name, url;
        private long id, releaseDate;
        private int downloads;
        private Rating rating;

        private static class Rating {
            private int count;
            private double average;

            @Override
            public String toString() {
                return "Rating{" +
                        "\n\t\tcount=" + count +
                        "\n\t\t, average=" + average +
                        "\n\t}";
            }
        }

        @Override
        public String toString() {
            return "SpigetResponse{" +
                    "\n\tname='" + name + '\'' +
                    "\n\t, url='" + url + '\'' +
                    "\n\t, id=" + id +
                    "\n\t, releaseDate=" + releaseDate +
                    "\n\t, downloads=" + downloads +
                    "\n\t, rating=" + rating +
                    "\n}";
        }
    }
}