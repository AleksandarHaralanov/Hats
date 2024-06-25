package me.beezle.extras;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.Bukkit.getLogger;

public class UpdateUtil {

    // Template for parsing the variables:
    // https://api.github.com/repos/USER/REPO/releases/latest | plugin.yml Version Attribute Value | plugin.yml Name Attribute Value
    public static void checkForUpdates(String stringUrl, String currentVersion, String pluginName) {
        currentVersion = "v" + currentVersion;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                getLogger().warning(String.format("[%s] Unexpected code: %s", pluginName, responseCode));
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String responseBody = content.toString();
            String latestVersion = getLatestVersion(responseBody);
            compareVersions(pluginName, currentVersion, latestVersion, stringUrl);
        } catch (IOException e) {
            getLogger().severe(String.format("[%s] IOException occurred: %s", pluginName, e.getMessage()));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getLatestVersion(String responseBody) {
        String tagNameField = "\"tag_name\":\"";

        int tagIndex = responseBody.indexOf(tagNameField);
        if (tagIndex == -1) {
            return null;
        }

        int startIndex = tagIndex + tagNameField.length();
        int endIndex = responseBody.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }

        return responseBody.substring(startIndex, endIndex);
    }

    private static void compareVersions(String pluginName, String currentVersion, String latestVersion, String downloadLink) {
        if (latestVersion == null) {
            getLogger().warning(String.format("[%s] Could not determine the latest version.", pluginName));
            return;
        }

        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            getLogger().info(String.format("[%s] New %s available, you are running an OUTDATED %s!", pluginName, latestVersion, currentVersion));
            downloadLink = downloadLink.replace("api.github.com/repos", "github.com");
            getLogger().info(String.format("[%s] Download the latest version from: %s", pluginName, downloadLink));
        } else {
            getLogger().info(String.format("[%s] You are running the latest version.", pluginName));
        }
    }
}