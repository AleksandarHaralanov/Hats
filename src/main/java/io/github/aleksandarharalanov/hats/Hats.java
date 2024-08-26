package io.github.aleksandarharalanov.hats;

import io.github.aleksandarharalanov.hats.command.HatCommand;
import io.github.aleksandarharalanov.hats.listener.BlockLightListener;
import io.github.aleksandarharalanov.hats.listener.EntityLightListener;
import io.github.aleksandarharalanov.hats.listener.PlayerLightListener;
import io.github.aleksandarharalanov.hats.util.ConfigUtil;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static io.github.aleksandarharalanov.hats.handler.EffectsHandler.getPlayerEffects;
import static io.github.aleksandarharalanov.hats.handler.LightHandler.clearAllLight;
import static io.github.aleksandarharalanov.hats.handler.LightHandler.getPlayerLight;
import static io.github.aleksandarharalanov.hats.util.LoggerUtil.logInfo;
import static io.github.aleksandarharalanov.hats.util.UpdateUtil.checkForUpdates;

public class Hats extends JavaPlugin {

    private static Hats plugin;
    private static ConfigUtil config;
    private static ConfigUtil players;
    private static PluginDescriptionFile pdf;

    @Override
    public void onEnable() {
        // Shorten plugin description file reference for easier access throughout the class
        pdf = getDescription();

        // Check if a newer update is available, and notify if an update is found
        checkForUpdates(pdf.getName(), pdf.getVersion(),
                "https://api.github.com/repos/AleksandarHaralanov/Hats/releases/latest"
        );

        // Set the plugin instance for static access
        plugin = this;

        // Initialize and load configuration files
        config = new ConfigUtil(this, "config.yml");
        config.loadConfig();

        players = new ConfigUtil(this, "players.yml");
        players.loadConfig();

        // Register the Hat command and associate it with the HatCommand executor
        getCommand("hat").setExecutor(new HatCommand());

        // Register event listeners for handling specific game events related to light and player actions
        PluginManager pluginManager = getServer().getPluginManager();
        final BlockLightListener blockLightListener = new BlockLightListener();
        final EntityLightListener entityLightListener = new EntityLightListener();
        final PlayerLightListener playerLightListener = new PlayerLightListener();
        pluginManager.registerEvent(Type.BLOCK_PLACE, blockLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.BLOCK_BREAK, blockLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.ENTITY_DEATH, entityLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.PLAYER_MOVE, playerLightListener, Priority.Normal, this);
        pluginManager.registerEvent(Type.PLAYER_QUIT, playerLightListener, Priority.Normal, this);

        // Log the successful enablement of the plugin
        logInfo(String.format("[%s] v%s Enabled.", pdf.getName(), pdf.getVersion()));
    }

    @Override
    public void onDisable() {
        // Save configs and settings
        config.saveConfig();

        players.setProperty("players.light-enabled", new ArrayList<>(getPlayerLight()));
        players.setProperty("players.effects-disabled", new ArrayList<>(getPlayerEffects()));
        players.saveConfig();

        // Clear all active lights to prevent any lingering light effects after the plugin is disabled
        clearAllLight();

        // Log the successful disablement of the plugin
        logInfo(String.format("[%s] v%s Disabled.", pdf.getName(), pdf.getVersion()));
    }

    public static Hats getInstance() {
        return plugin;
    }

    public static ConfigUtil getPluginConfig() {
        return config;
    }

    public static ConfigUtil getPlayersConfig() {
        return players;
    }
}
