package me.beezle.hats;

import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getLogger;

public class Hats extends JavaPlugin {
    private static String version;

    @Override
    public void onEnable() {
        version = this.getDescription().getVersion();
        getCommand("hat").setExecutor(new HatsCommand(version));
        getLogger().info(String.format("[Hats] v%s Enabled.", version));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[Hats] v%s Disabled.", version));
    }
}
