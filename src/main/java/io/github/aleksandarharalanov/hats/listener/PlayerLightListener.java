package io.github.aleksandarharalanov.hats.listener;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.aleksandarharalanov.hats.handler.LightHandler.*;

public class PlayerLightListener extends PlayerListener {

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!isLightEnabled(event.getPlayer())) {
            return;
        }

        clearSpecificLight(event.getPlayer());
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isLightEnabled(player)) {
            clearSpecificLight(player);
            return;
        }

        Block block = player.getLocation().getBlock();
        if (getPlayerLastBlock().get(player) != block) {
            getPlayerLastBlock().put(player, block);
            CraftBlock craftBlock = (CraftBlock) block;
            if (craftBlock != null) {
                CraftWorld world = (CraftWorld) craftBlock.getWorld();
                int x = craftBlock.getX();
                int y = craftBlock.getY();
                int z = craftBlock.getZ();
                lightUp(x, y + 1, z, world, player);
            }
        }
    }
}
