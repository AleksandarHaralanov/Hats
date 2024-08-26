package io.github.aleksandarharalanov.hats.handler;

import net.minecraft.server.EnumSkyBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;


import java.util.*;

import static io.github.aleksandarharalanov.hats.Hats.getPlayersConfig;
import static io.github.aleksandarharalanov.hats.Hats.getPluginConfig;
import static io.github.aleksandarharalanov.hats.util.AccessUtil.hasPermission;
import static org.bukkit.Bukkit.getServer;

public class LightHandler {

    private static final ArrayList<String> playerLight = new ArrayList<>(
            getPlayersConfig().getStringList("players.light-enabled", Collections.singletonList(null))
    );
    private static final HashMap<Player, Block> playerLastBlock = new HashMap<>();
    private static final HashMap<String, HashMap<Location, Integer>> oldPlayerBlocks = new HashMap<>();

    public static void lightUp(int x, int y, int z, CraftWorld world, Player player) {
        HashMap<Location, Integer> playerBlocks = oldPlayerBlocks.get(player.getName());
        if (playerBlocks != null) {
            resetLight(playerBlocks, world);
            playerBlocks.clear();
        } else {
            playerBlocks = new HashMap<>();
            oldPlayerBlocks.put(player.getName(), playerBlocks);
        }

        int radius = LightRadius.fromValue(getPluginConfig().getString("hats.light.radius", "NARROW"));
        int level = LightLevel.fromValue(getPluginConfig().getString("hats.light.level", "LOW"));
        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                for (int k = -radius; k <= radius; ++k) {
                    int oldLevel = world.getHandle().getLightLevel(x + i, y + j, z + k);
                    int actLevel = level - (Math.abs(i) + Math.abs(j) + Math.abs(k));
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

    public static boolean isLightEnabled(Player player) {
        ArrayList<Integer> source = (ArrayList<Integer>) getPluginConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 52, 62, 89, 90, 91));

        boolean allowed = getPluginConfig().getBoolean("hats.light.toggle", true);
        boolean permission = hasPermission(player, "hats.light");
        boolean enabled = playerLight.contains(player.getName());
        boolean hat = source.contains(player.getInventory().getHelmet().getTypeId());
        return allowed && permission && enabled && hat;
    }

    public static ArrayList<String> getPlayerLight() {
        return playerLight;
    }

    public static HashMap<Player, Block> getPlayerLastBlock() {
        return playerLastBlock;
    }
}
