package com.haralanov.utilities;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.bukkit.plugin.java.JavaPlugin;

public class PdfUtil {

    private static Map<String, String> parseYaml(InputStream inputStream) {
        Map<String, String> configMap = new HashMap<>();
        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim().replace("\"", "");
                    configMap.put(key, value);
                }
            }
        }
        return configMap;
    }

    /**
     * This method loads the 'plugin.yml' file using the plugin's class loader and extracts the value associated
     * with the custom 'github' attribute.
     * <p><b>Note: </b>The 'plugin.yml' file comes with comments for the explanation. Go check it out.</p>
     *
     * @param plugin The instance of the plugin. This is used to get the class loader for loading the 'plugin.yml' file.
     * @return The GitHub URL specified in the 'plugin.yml' file, or {@code null} if the attribute is not found.
     * <hr>
     * <b>How to initialize: </b>{@code String github = PdfUtil.getGithub(this);} inside your 'Main' class.
     */
    public static String getGithub(JavaPlugin plugin) {
        InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream("plugin.yml");
        if (inputStream != null) {
            Map<String, String> configMap = parseYaml(inputStream);
            return configMap.get("github");
        }
        return null;
    }

}