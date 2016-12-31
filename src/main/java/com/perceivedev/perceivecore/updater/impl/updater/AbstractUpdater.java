package com.perceivedev.perceivecore.updater.impl.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.perceivedev.perceivecore.updater.UpdateStrategy;
import com.perceivedev.perceivecore.updater.Updater;
import com.perceivedev.perceivecore.updater.UpdaterEntry;

/**
 * A skeleton for the {@link Updater}
 */
public abstract class AbstractUpdater implements Updater {

    private UpdateStrategy<?> updateStrategy;

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

    protected UpdateResult downloadAndCopy(UpdaterEntry entry, Function<String, String> nameTransformation) {
        Objects.requireNonNull(entry, "entry can not be null!");
        Objects.requireNonNull(nameTransformation, "nameTransformation can not be null!");

        URL downloadUrl = entry.getDownloadUrl();

        Preconditions.checkArgument(downloadUrl != null, "Download URL was null!");

        try (InputStream inputStream = downloadUrl.openStream()) {

        } catch (IOException e) {
            e.printStackTrace();
            return UpdateResult.ERROR_WHILE_DOWNLOADING;
        }
    }
}
