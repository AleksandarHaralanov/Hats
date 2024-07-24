package com.haralanov.hats;

import com.haralanov.hats.utils.UpdateUtil;

import org.bukkit.plugin.java.JavaPlugin;

public class Hats extends JavaPlugin {

    @Override
    public void onEnable() {
        UpdateUtil.checkForUpdates(getDescription().getName(), getDescription().getVersion(), "https://api.github.com/repos/AleksandarHaralanov/Hats/releases/latest");

        final HatsCommand hatsCommand = new HatsCommand(this);
        getCommand("hat").setExecutor(hatsCommand);

        getServer().getLogger().info(String.format("[%s] v%s Enabled.", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info(String.format("[%s] v%s Disabled.", getDescription().getName(), getDescription().getVersion()));
    }
}
