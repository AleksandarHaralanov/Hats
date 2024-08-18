package io.github.aleksandarharalanov.hats.handler;

import io.github.aleksandarharalanov.hats.Hats;
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

    private static final ArrayList<String> playerLight = new ArrayList<>();
    private static final HashMap<Player, Block> playerBlocks = new HashMap<>();
    private static final HashMap<String, HashMap<Location, Integer>> oldPlayerBlocks = new HashMap<>();

    public static void lightUp(int x, int y, int z, CraftWorld world, Player player) {
        HashMap<Location, Integer> playerBlocks = oldPlayerBlocks.get(player.getName());
        int radius = 15 / (Hats.getConfig().getBoolean("hats.light.radius", false) ? 3 : 5);

        if (playerBlocks != null) {
            resetLight(playerBlocks, world);
            playerBlocks.clear();
        } else {
            playerBlocks = new HashMap<>();
            oldPlayerBlocks.put(player.getName(), playerBlocks);
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

    public static void resetLight(HashMap<Location, Integer> oldPlayerBlocks, CraftWorld world) {
        for (Map.Entry<Location, Integer> entry : oldPlayerBlocks.entrySet()) {
            Location location = entry.getKey();
            int lightLevel = entry.getValue();

            if (location.getBlock().getTypeId() != 8 && location.getBlock().getTypeId() != 9) {
                world.getHandle().b(EnumSkyBlock.BLOCK,
                        location.getBlockX(), location.getBlockY(), location.getBlockZ(), lightLevel);
            } else {
                world.getHandle().b(EnumSkyBlock.BLOCK,
                        location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0);
            }
        }
    }

    public static void clearAllLight() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (oldPlayerBlocks.containsKey(player.getName())) {
                HashMap<Location, Integer> playerBlocks = oldPlayerBlocks.get(player.getName());
                if (playerBlocks != null) {
                    resetLight(playerBlocks, (CraftWorld) player.getWorld());
                    playerBlocks.clear();
                }
            }
        }
    }

    public static void clearSpecificLight(Player player) {
        HashMap<Location, Integer> playerBlocks = oldPlayerBlocks.get(player.getName());
        if (playerBlocks != null) {
            resetLight(playerBlocks, (CraftWorld) player.getWorld());
            playerBlocks.clear();
        }
    }

    public static ArrayList<String> getPlayerLight() {
        return playerLight;
    }

    public static HashMap<Player, Block> getPlayerBlocks() {
        return playerBlocks;
    }
}
