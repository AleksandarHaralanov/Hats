package com.haralanov.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.bukkit.Bukkit.getLogger;

public class PdfUtil {

    private static final String YAML = "plugin.yml";
    private static final String ATTRIBUTE = "apiGithub";

    /**
     * Retrieves the API GitHub URL from the plugin's YAML configuration file.
     * <p><b>Warning: </b>Make sure, if you have other attributes/values containing {@code apiGithub} text within them, to put the {@code apiGithub} attribute as the first instance.</p>
     * @return The API GitHub URL as a string, or {@code null} if the file is not found or an error occurs during reading.
     * <p>E.g., 'https://api.github.com/repos/USER/REPO/releases/latest'.</p>
     * <hr>
     * <b>Note:</b> Ensure that the {@code plugin.yml} file is correctly placed in the {@code src/main/resources} directory
     *             and follows the expected format.
     */
    public static String getApiGithub(String NAME) {
        try {
            InputStream inputStream = PdfUtil.class.getClassLoader().getResourceAsStream(YAML);
            if (inputStream == null) {
                getLogger().severe(String.format("[%s] File not found: %s", NAME, YAML));
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder yamlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                yamlContent.append(line).append("\n");
            }

            return parseYaml(yamlContent.toString());
        } catch (IOException e) {
            getLogger().severe(String.format("[%s] IOException occurred while reading %s: %s", NAME, YAML, e.getMessage()));
            return null;
        }
    }

    private static String parseYaml(String yamlContent) {
        String[] lines = yamlContent.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith(ATTRIBUTE + ":")) {
                int index = line.indexOf(":");
                return line.substring(index + 1).trim();
            }
        }
        return null;
    }
}
