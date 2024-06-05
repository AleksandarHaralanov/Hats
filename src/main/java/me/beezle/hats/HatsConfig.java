package me.beezle.hats;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class HatsConfig extends Configuration {

    public HatsConfig(File file) {
        super(file);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            write();
            save();
        } else {
            load();
        }
    }

    public void write() {
        generateConfigOption();
        save();
    }

    private void generateConfigOption() {
        if (this.getProperty("allowArmor") == null) {
            this.setProperty("allowArmor", false);
        }
    }

    public boolean getBooleanConfigOption(String key, boolean defaultValue) {
        Object value = this.getProperty(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}
