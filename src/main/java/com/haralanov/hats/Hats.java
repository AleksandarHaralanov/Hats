package com.haralanov.hats;

import com.haralanov.utilities.PdfUtil;
import com.haralanov.utilities.UpdateUtil;

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
        String pluginAuthor = pdf.getAuthors().get(0);
        String github = PdfUtil.getGithub(this);

        getLogger().info(String.format("[%s] v%s Enabled.", pluginName, currentVersion));

        UpdateUtil.checkForUpdates(pluginName, currentVersion, github);

        getCommand("hat").setExecutor(new HatsCommand(currentVersion, pluginName, pluginAuthor, github));
        getCommand("hats").setExecutor(new HatsCommand(currentVersion, pluginName, pluginAuthor, github));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] v%s Disabled.", pluginName, currentVersion));
    }
}
