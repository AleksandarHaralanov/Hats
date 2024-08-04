package com.haralanov.hats.events;

import com.haralanov.hats.Hats;
import com.haralanov.hats.LightHandler;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerLightListener extends PlayerListener {

    public void onPlayerQuit(PlayerQuitEvent event) {
        boolean enabled = Hats.getConfig().getBoolean("hat-light.enabled", true);
        if (!enabled) {
            return;
        }

        boolean permission = (event.getPlayer().hasPermission("hats.light") || event.getPlayer().isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.toggle.contains(event.getPlayer().getName())) {
            return;
        }

        LightHandler.clearSpecificLight(event.getPlayer());
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        boolean enabled = Hats.getConfig().getBoolean("hat-light.enabled", true);
        if (!enabled) {
            return;
        }

        boolean permission = (event.getPlayer().hasPermission("hats.light") || event.getPlayer().isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.toggle.contains(event.getPlayer().getName())) {
            return;
        }

        if (LightHandler.playerBlocks.get(event.getPlayer()) != event.getPlayer().getLocation().getBlock()) {
            ArrayList<Integer> source = (ArrayList<Integer>) Hats.getConfig().getIntList(
                    "hat-light.source", Arrays.asList(10, 11, 50, 51, 89, 90, 91));
            if (!source.contains(event.getPlayer().getInventory().getHelmet().getTypeId())) {
                LightHandler.clearSpecificLight(event.getPlayer());
            } else {
                LightHandler.playerBlocks.put(event.getPlayer(), event.getPlayer().getLocation().getBlock());
                CraftBlock block = (CraftBlock) event.getPlayer().getLocation().getBlock();
                if (block != null) {
                    CraftWorld world = (CraftWorld) block.getWorld();
                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();
                    LightHandler.lightUp(x, y + 1, z, world, event.getPlayer());
                }
            }
        }
    }
}
