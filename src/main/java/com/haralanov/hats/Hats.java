package com.haralanov.hats;

import com.haralanov.hats.commands.HatCommand;
import com.haralanov.hats.events.BlockLightListener;
import com.haralanov.hats.events.EntityLightListener;
import com.haralanov.hats.events.PlayerLightListener;
import com.haralanov.hats.utils.ConfigUtil;
import com.haralanov.hats.utils.UpdateUtil;

import org.bukkit.event.Event.Type;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Hats extends JavaPlugin {

    private static Hats plugin;
    private static ConfigUtil config;

    @Override
    public void onEnable() {
        UpdateUtil.checkForUpdates(getDescription().getName(), getDescription().getVersion(),
                "https://api.github.com/repos/AleksandarHaralanov/Hats/releases/latest");

        plugin = this;
        config = new ConfigUtil(new File(getDataFolder(), "config.yml"), this);
        config.loadConfig();

        LightHandler.toggle.addAll(config.getStringList("hat-light.players", Collections.singletonList(null)));

        getCommand("hat").setExecutor(new HatCommand());

        getServer().getPluginManager().registerEvent(
                Type.BLOCK_PLACE, new BlockLightListener(), Priority.Normal, this);
        getServer().getPluginManager().registerEvent(
                Type.BLOCK_BREAK, new BlockLightListener(), Priority.Normal, this);
        getServer().getPluginManager().registerEvent(
                Type.ENTITY_DEATH, new EntityLightListener(), Priority.Normal, this);
        getServer().getPluginManager().registerEvent(
                Type.PLAYER_MOVE, new PlayerLightListener(), Priority.Normal, this);
        getServer().getPluginManager().registerEvent(
                Type.PLAYER_QUIT, new PlayerLightListener(), Priority.Normal, this);

        getServer().getLogger().info(String.format("[%s] v%s Enabled.",
                getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        config.setProperty("hat-light.players", new ArrayList<>(LightHandler.toggle));
        config.saveConfig();

        LightHandler.clearAllLight();

        getServer().getLogger().info(String.format("[%s] v%s Disabled.",
                getDescription().getName(), getDescription().getVersion()));
    }

    public static Hats getInstance() {
        return plugin;
    }

    public static ConfigUtil getConfig() {
        return config;
    }
}
