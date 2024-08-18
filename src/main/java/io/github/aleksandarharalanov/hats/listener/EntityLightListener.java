package io.github.aleksandarharalanov.hats.listener;

import io.github.aleksandarharalanov.hats.Hats;
import io.github.aleksandarharalanov.hats.handler.LightHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityLightListener extends EntityListener {

    public void onPlayerDeath(EntityDeathEvent event) {
        Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if (player == null) {
            return;
        }

        boolean enabled = Hats.getConfig().getBoolean("hats.light.toggle", true);
        if (!enabled) {
            return;
        }

        boolean permission = (player.hasPermission("hats.light") || player.isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.getPlayerLight().contains(player.getName())) {
            return;
        }

        LightHandler.clearSpecificLight(player);
    }
}
