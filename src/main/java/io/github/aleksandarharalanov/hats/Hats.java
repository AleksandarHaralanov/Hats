package io.github.aleksandarharalanov.hats;

import io.github.aleksandarharalanov.hats.command.HatCommand;
import io.github.aleksandarharalanov.hats.handler.LightHandler;
import io.github.aleksandarharalanov.hats.listener.BlockLightListener;
import io.github.aleksandarharalanov.hats.listener.EntityLightListener;
import io.github.aleksandarharalanov.hats.listener.PlayerLightListener;
import io.github.aleksandarharalanov.hats.util.ConfigUtil;
import io.github.aleksandarharalanov.hats.util.UpdateUtil;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Hats extends JavaPlugin {

    private static Hats plugin;
    private static ConfigUtil config;
    private static PluginDescriptionFile pdf;

    @Override
    public void onEnable() {
        pdf = getDescription();

        UpdateUtil.checkForUpdates(pdf.getName(), pdf.getVersion(),
                "https://api.github.com/repos/AleksandarHaralanov/Hats/releases/latest"
        );

        plugin = this;
        config = new ConfigUtil(new File(getDataFolder(), "config.yml"), this);
        config.loadConfig();

        LightHandler.getPlayerLight().addAll(config.getStringList(
                "hats.light.players", Collections.singletonList(null))
        );

        getCommand("hat").setExecutor(new HatCommand());

        PluginManager pluginManager = getServer().getPluginManager();
        final BlockLightListener blockLightListener = new BlockLightListener();
        final EntityLightListener entityLightListener = new EntityLightListener();
        final PlayerLightListener playerLightListener = new PlayerLightListener();
        pluginManager.registerEvent(Type.BLOCK_PLACE, blockLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.BLOCK_BREAK, blockLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.ENTITY_DEATH, entityLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.PLAYER_MOVE, playerLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.PLAYER_QUIT, playerLightListener, Priority.Normal, this);

        getServer().getLogger().info(String.format("[%s] v%s Enabled.", pdf.getName(), pdf.getVersion()));
    }

    @Override
    public void onDisable() {
        config.setProperty("hats.light.players", new ArrayList<>(LightHandler.getPlayerLight()));
        config.saveConfig();

        LightHandler.clearAllLight();

        getServer().getLogger().info(String.format("[%s] v%s Disabled.", pdf.getName(), pdf.getVersion()));
    }

    public static Hats getInstance() {
        return plugin;
    }

    public static ConfigUtil getConfig() {
        return config;
    }
}