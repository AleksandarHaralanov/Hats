package com.haralanov.hats;

import net.minecraft.server.EnumSkyBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class LightHandler {

    public static final HashMap<Player, Block> playerBlocks = new HashMap<>();
    public static final HashMap<String, HashMap<Location, Integer>> oldBlocks = new HashMap<>();
    public static final ArrayList<String> toggle = new ArrayList<>();

    public static void lightUp(int x, int y, int z, CraftWorld world, Player player) {
        HashMap<Location, Integer> playerBlocks = oldBlocks.get(player.getName());
        int radius = 15 / (Hats.getConfig().getBoolean("hat-light.wider", false) ? 3 : 5);

        if (playerBlocks != null) {
            resetLight(playerBlocks, world);
            playerBlocks.clear();
        } else {
            playerBlocks = new HashMap<>();
            oldBlocks.put(player.getName(), playerBlocks);
        }

        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                for (int k = -radius; k <= radius; ++k) {
                    int oldLevel = world.getHandle().getLightLevel(x + i, y + j, z + k);
                    int actLevel = 15 - (Math.abs(i) + Math.abs(j) + Math.abs(k));
                    if (actLevel > oldLevel) {
                        playerBlocks.put(new Location(world, (x + i), (y + j), (z + k)), oldLevel);
                        world.getHandle().b(EnumSkyBlock.BLOCK, x + i, y + j, z + k, actLevel);
                    }
                }
            }
        }
    }

    public static void resetLight(HashMap<Location, Integer> oldBlocks, CraftWorld world) {
        for (Map.Entry<Location, Integer> entry : oldBlocks.entrySet()) {
            Location location = entry.getKey();
            int lightLevel = entry.getValue();

            if (location.getBlock().getTypeId() != 8 && location.getBlock().getTypeId() != 9) {
                world.getHandle().b(EnumSkyBlock.BLOCK, location.getBlockX(), location.getBlockY(), location.getBlockZ(), lightLevel);
            } else {
                world.getHandle().b(EnumSkyBlock.BLOCK, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0);
            }
        }
    }

    public static void clearAllLight() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (LightHandler.oldBlocks.containsKey(player.getName())) {
                HashMap<Location, Integer> playerBlocks = LightHandler.oldBlocks.get(player.getName());
                if (playerBlocks != null) {
                    LightHandler.resetLight(playerBlocks, (CraftWorld) player.getWorld());
                    playerBlocks.clear();
                }
            }
        }
    }

    public static void clearSpecificLight(Player player) {
        HashMap<Location, Integer> playerBlocks = LightHandler.oldBlocks.get(player.getName());
        if (playerBlocks != null) {
            LightHandler.resetLight(playerBlocks, (CraftWorld) player.getWorld());
            playerBlocks.clear();
        }
    }
}
