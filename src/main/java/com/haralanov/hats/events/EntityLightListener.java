package com.haralanov.hats.events;

import com.haralanov.hats.Hats;
import com.haralanov.hats.LightHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityLightListener extends EntityListener {

    public void onPlayerDeath(EntityDeathEvent event) {
        Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if (player == null) {
            return;
        }

        boolean enabled = Hats.getConfig().getBoolean("hat-light.enabled", true);
        if (!enabled) {
            return;
        }

        boolean permission = (player.hasPermission("hats.light") || player.isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.toggle.contains(player.getName())) {
            return;
        }

        LightHandler.clearSpecificLight(player);
    }
}
