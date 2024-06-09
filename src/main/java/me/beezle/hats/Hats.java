package me.beezle.hats;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Hats extends JavaPlugin {

    private static Hats instance;
    private HatsConfig config;

    @Override
    public void onEnable() {
        instance = this;
        config = new HatsConfig(new File(getDataFolder(), "config.yml"));
        getCommand("hat").setExecutor(new HatsCommand());
        System.out.print("[Hats v1.1.0] Enabled.");
    }

    @Override
    public void onDisable() {
        System.out.print("[Hats v1.1.0] Disabled.");
    }

    public static Hats getInstance() {
        return instance;
    }

    public HatsConfig getConfig() {
        return config;
    }
}