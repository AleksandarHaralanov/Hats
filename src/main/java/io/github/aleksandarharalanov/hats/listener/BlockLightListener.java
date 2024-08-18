package io.github.aleksandarharalanov.hats.listener;

import io.github.aleksandarharalanov.hats.Hats;
import io.github.aleksandarharalanov.hats.handler.LightHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockLightListener extends BlockListener {

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean enabled = Hats.getConfig().getBoolean("hats.light.toggle", true);
        if (!enabled) {
            return;
        }

        boolean permission = (event.getPlayer().hasPermission("hats.light") || event.getPlayer().isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.getPlayerLight().contains(event.getPlayer().getName())) {
            return;
        }

        if (!isLight(event.getBlock().getTypeId())) {
            return;
        }

        LightHandler.clearSpecificLight(event.getPlayer());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        boolean enabled = Hats.getConfig().getBoolean("hats.light.toggle", true);
        if (!enabled) {
            return;
        }

        boolean permission = (event.getPlayer().hasPermission("hats.light") || event.getPlayer().isOp());
        if (!permission) {
            return;
        }

        if (!LightHandler.getPlayerLight().contains(event.getPlayer().getName())) {
            return;
        }

        if (!isLight(event.getBlock().getTypeId())) {
            return;
        }

        LightHandler.clearSpecificLight(event.getPlayer());
    }

    private static boolean isLight(int typeId) {
        switch (typeId) {
            case 10:
            case 11:
            case 50:
            case 51:
            case 62:
            case 74:
            case 76:
            case 89:
            case 90:
            case 91:
            case 95:
                return true;
            default:
                return false;
        }
    }
}
