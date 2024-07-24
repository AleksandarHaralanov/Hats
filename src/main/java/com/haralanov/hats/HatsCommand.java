package com.haralanov.hats;

import com.haralanov.hats.utils.AboutUtil;
import com.haralanov.hats.utils.ColorUtil;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class HatsCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public HatsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (command.getName().equalsIgnoreCase("hat")) {
            if (args.length == 0) {
                if (player != null) {
                    if (!(player.hasPermission("hats.wear") || player.isOp())) {
                        player.sendMessage(ColorUtil.translate("&cYou don't have permission to wear hats."));
                    } else {
                        wearHat(player, player.getItemInHand());
                    }
                } else {
                    getServer().getLogger().info("You must be in-game to run this command.");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("about")) {
                AboutUtil.about(player, plugin);
            } else {
                if (player != null) {
                    player.sendMessage(ColorUtil.translate("&cInvalid usage. Use &e/hat &cor &e/hat about&c."));
                } else {
                    getServer().getLogger().info("Invalid usage. Use '/hat' or '/hat about'.");
                }
            }
        }

        return true;
    }

    private static void wearHat(Player player, ItemStack item) {
        if (item.getType() == Material.AIR) {
            player.sendMessage(ColorUtil.translate("&cYou aren't holding anything."));
            return;
        }

        String itemName = normalizeName(item);
        if (item.getTypeId() < 1 || item.getTypeId() > 96) {
            player.sendMessage(ColorUtil.translate(String.format("&e%s &ccan't be worn as a hat.", itemName)));
            return;
        }

        if (player.getInventory().getHelmet().getType() == item.getType()) {
            player.sendMessage(ColorUtil.translate(String.format("&e%s &cis already your hat.", itemName)));
            return;
        }

        ItemStack hat = new ItemStack(item.getType(), 1, item.getDurability());
        if (item.getData() != null) {
            hat.setData(item.getData());
        }

        if (player.getInventory().getHelmet().getType() == Material.AIR) {
            player.sendMessage(ColorUtil.translate(String.format("&e%s &7worn as a hat.", itemName)));
        } else {
            String helmetName = normalizeName(player.getInventory().getHelmet());

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(player.getInventory().getHelmet());
                player.sendMessage(ColorUtil.translate(String.format("&e%s &7replaced with &e%s &7as the new hat.", helmetName, itemName)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getHelmet());
                player.sendMessage(ColorUtil.translate(String.format("&cDropped &e%s &chat due to no pocket space.", helmetName)));
                player.sendMessage(ColorUtil.translate(String.format("&e%s &7worn as the new hat.", itemName)));
            }
        }

        player.getInventory().setHelmet(hat);
        player.getInventory().removeItem(hat);
    }

    private static String normalizeName(ItemStack item) {
        Material itemType = item.getType();
        String itemName = itemType.name().toLowerCase().replace('_', ' ');
        StringBuilder normalizedItemName = new StringBuilder();

        if (itemType == Material.WOOL) {
            Wool wool = (Wool) item.getData();
            String colorName = wool.getColor().name().toLowerCase().replace('_', ' ');
            String[] colors = colorName.split(" ");
            for (String color : colors) {
                normalizedItemName
                        .append(Character.toUpperCase(color.charAt(0)))
                        .append(color.substring(1))
                        .append(" ");
            }
            normalizedItemName.append("Wool");
        } else {
            String[] words = itemName.split(" ");
            for (String word : words) {
                normalizedItemName
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return normalizedItemName.toString().trim();
    }
}