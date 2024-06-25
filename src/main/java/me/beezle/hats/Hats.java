package me.beezle.hats;

import me.beezle.extras.UpdateUtil;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;

import static org.bukkit.Bukkit.getLogger;

public class Hats extends JavaPlugin {

    private static String currentVersion;
    private static String pluginName;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = this.getDescription();
        currentVersion = pdf.getVersion();
        pluginName = pdf.getName();

        getLogger().info(String.format("[%s] v%s Enabled.", pluginName, currentVersion));

        UpdateUtil.checkForUpdates("https://api.github.com/repos/AleksandarHaralanov/Hats/releases/latest", currentVersion, pluginName);

        getCommand("hat").setExecutor(new HatsCommand(currentVersion));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] v%s Disabled.", pluginName, currentVersion));
    }
}
