package io.github.aleksandarharalanov.hats.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import static io.github.aleksandarharalanov.hats.handler.LightHandler.clearSpecificLight;
import static io.github.aleksandarharalanov.hats.handler.LightHandler.isLightEnabled;

public class EntityLightListener extends EntityListener {

    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!isLightEnabled((Player) event.getEntity())) {
            return;
        }

        clearSpecificLight((Player) event.getEntity());
    }
}
