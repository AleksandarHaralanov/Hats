package io.github.aleksandarharalanov.hats.handler;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

import static io.github.aleksandarharalanov.hats.Hats.getPlayersConfig;
import static io.github.aleksandarharalanov.hats.Hats.getPluginConfig;
import static io.github.aleksandarharalanov.hats.util.AccessUtil.hasPermission;

public class EffectsHandler {

    private static final ArrayList<String> playerEffects = new ArrayList<>(
            getPlayersConfig().getStringList("players.effects-disabled", Collections.singletonList(null))
    );

    public static boolean areEffectsEnabled(Player player) {
        boolean allowed = getPluginConfig().getBoolean("hats.effects", true);
        boolean permission = hasPermission(player, "hats.wear");
        boolean enabled = !playerEffects.contains(player.getName());
        return allowed && permission && enabled;
    }

    public static ArrayList<String> getPlayerEffects() {
        return playerEffects;
    }
}
