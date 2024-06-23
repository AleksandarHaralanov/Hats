package me.beezle.hats;

import org.bukkit.plugin.java.JavaPlugin;

public class Hats extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("hat").setExecutor(new HatsCommand());
        System.out.print("[Hats v2.0.0] Enabled.");
    }

    @Override
    public void onDisable() {
        System.out.print("[Hats v2.0.0] Disabled.");
    }
}