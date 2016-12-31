package com.perceivedev.perceivecore.updater.impl.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.perceivedev.perceivecore.updater.Updater;
import com.perceivedev.perceivecore.updater.impl.other.StandardUpdateStrategy;

/**
 * An {@link Updater} using the Curse API
 */
public class CurseAPIUpdater extends AbstractUpdater {

    private static final Gson GSON = new Gson();
    private static final String BASE_URL = "https://api.curseforge.com/servermods/files";

    private long slug;

    /**
     * @param slug The slug to use
     */
    public CurseAPIUpdater(long slug) {
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
        String fullUrl = BASE_URL + "?projectIds=" + slug;

        CurseProjectResponse[] elements = getCurseResponse(fullUrl);

        System.out.println(Arrays.toString(elements));

        return UpdateCheckResult.NO_NEW_VERSION;
    }

    public static void main(String[] args) {
        CurseAPIUpdater updater = new CurseAPIUpdater(101108);
        updater.setUpdateStrategy(StandardUpdateStrategy.TIME);
        updater.searchForUpdate();
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
