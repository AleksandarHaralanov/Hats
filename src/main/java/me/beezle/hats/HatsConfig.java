package me.beezle.hats;

import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.IOException;

public class HatsConfig extends Configuration {

    public HatsConfig(File file) {
        super(file);
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directories for config: " + file.getAbsolutePath());
                }
                write();
            }
            load();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing config: " + file.getAbsolutePath(), e);
        }
    }

    public void write() {
        generate("allowArmor", false);
        save();
    }

    private void generate(String key, Boolean defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public boolean get(String key, boolean defaultValue) {
        Object value = this.getProperty(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}
