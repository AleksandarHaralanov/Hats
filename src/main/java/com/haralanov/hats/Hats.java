package com.haralanov.hats;

import com.haralanov.utilities.PdfUtil;
import com.haralanov.utilities.UpdateUtil;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;

import static org.bukkit.Bukkit.getLogger;

public class Hats extends JavaPlugin {

    private static String NAME;
    private static String VERSION;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = this.getDescription();
        VERSION = pdf.getVersion();
        NAME = pdf.getName();
        String AUTHOR = pdf.getAuthors().get(0);
        String SOURCE = pdf.getWebsite();
        String GITHUB = PdfUtil.getApiGithub(NAME);

        getLogger().info(String.format("[%s] v%s Enabled.", NAME, VERSION));

        UpdateUtil.checkForUpdates(NAME, VERSION, GITHUB);

        HatsCommand hatsCommand = new HatsCommand(NAME, VERSION, AUTHOR, SOURCE);
        getCommand("hat").setExecutor(hatsCommand);
        getCommand("hats").setExecutor(hatsCommand);
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] v%s Disabled.", NAME, VERSION));
    }
}
