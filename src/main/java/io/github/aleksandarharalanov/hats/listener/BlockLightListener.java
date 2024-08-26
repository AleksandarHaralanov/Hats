package io.github.aleksandarharalanov.hats.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import static io.github.aleksandarharalanov.hats.handler.LightHandler.clearSpecificLight;
import static io.github.aleksandarharalanov.hats.handler.LightHandler.isLightEnabled;

public class BlockLightListener extends BlockListener {

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isLightEnabled(event.getPlayer())) {
            return;
        }

        isLightBlock(event.getBlock().getTypeId(), event.getPlayer());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isLightEnabled(event.getPlayer())) {
            return;
        }

        isLightBlock(event.getBlock().getTypeId(), event.getPlayer());
    }

    private static void isLightBlock(int typeId, Player player) {
        switch (typeId) {
            case 10:
            case 11:
            case 50:
            case 51:
            case 52:
            case 62:
            case 74:
            case 76:
            case 89:
            case 90:
            case 91:
            case 95:
                clearSpecificLight(player);
                break;
            default:
                break;
        }
    }
}
