package com.haralanov.utilities;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;

public class UpdateUtil {

    /**
     * Checks for updates by querying a given GitHub API URL and comparing the current version with the latest available version.
     * <p>
     * This method appends 'v' to the front of the current version to comply with GitHub's semantic versioning conventions.
     * If your plugin's release tag does not include 'v' in front of the version number, this method will not recognize it as an update.
     * </p>
     * <p><b>Caution:</b> This method only works with GitHub repositories. You will need to modify it if you use another platform.</p>
     * <p><b>Warning:</b> Ensure that the GitHub API URL points to the latest release information of your repository.</p>
     *
     * @param pluginName     The name of the plugin.
     * @param currentVersion The current version of the plugin.
     * @param githubApiUrl   The GitHub API URL to query for the latest release information.
     *                       <p>Example: {@code https://api.github.com/repos/USER/REPO/releases/latest}</p>
     */
    public static void checkForUpdates(final String pluginName, final String currentVersion, final String githubApiUrl) {
        HttpURLConnection connection = null;
        try {
            final URL url = new URL(githubApiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                getLogger().warning(String.format("[%s] Unexpected code: %s", pluginName, responseCode));
                getLogger().warning(String.format("[%s] Unable to check if plugin has a newer version.", pluginName));
                return;
            }

            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            final String responseBody = content.toString();
            final String latestVersion = getLatestVersion(responseBody);
            final String formattedCurrentVersion = "v" + currentVersion;
            compareVersions(pluginName, formattedCurrentVersion, latestVersion, githubApiUrl);
        } catch (final IOException e) {
            getLogger().severe(String.format("[%s] IOException occurred while checking for a new version: %s", pluginName, e.getMessage()));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getLatestVersion(final String responseBody) {
        final String tagNameField = "\"tag_name\":\"";
        final int tagIndex = responseBody.indexOf(tagNameField);
        if (tagIndex == -1) {
            return null;
        }

        final int startIndex = tagIndex + tagNameField.length();
        final int endIndex = responseBody.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }

        return responseBody.substring(startIndex, endIndex);
    }

    private static void compareVersions(final String pluginName, final String currentVersion, final String latestVersion, final String githubApiUrl) {
        if (latestVersion == null) {
            getLogger().warning(String.format("[%s] Could not determine the latest version.", pluginName));
            return;
        }

        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            final String downloadLink = githubApiUrl.replace("api.github.com/repos", "github.com");
            getLogger().info(String.format("[%s] New %s available, you are running an OUTDATED %s!", pluginName, latestVersion, currentVersion));
            getLogger().info(String.format("[%s] Download the latest version from: %s", pluginName, downloadLink));
        } else {
            getLogger().info(String.format("[%s] You are running the latest version.", pluginName));
        }
    }
}