package com.haralanov.hats;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.material.Wool;

import static org.bukkit.Bukkit.getLogger;

public class HatsCommand implements CommandExecutor {

    private final String NAME;
    private final String VERSION;
    private final String AUTHOR;
    private final String SOURCE;

    public HatsCommand(final String NAME, final String VERSION, final String AUTHOR, final String SOURCE) {
        this.NAME = NAME;
        this.VERSION = VERSION;
        this.AUTHOR = AUTHOR;
        this.SOURCE = SOURCE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = (sender instanceof Player) ? (Player) sender : null;

        if (command.getName().equalsIgnoreCase("hat")) {
            if (args.length == 0) {
                if (player instanceof Player) {
                    if (!(player.hasPermission("hats.wear") || player.isOp())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&cYou don't have permission to wear hats."));
                    } else {
                        wearHat(player, player.getItemInHand());
                    }
                } else {
                    getLogger().info("You must be in-game to run this command.");
                }
            } else if (args.length == 1 &&
                    (args[0].equalsIgnoreCase("v") ||
                    args[0].equalsIgnoreCase("ver") ||
                    args[0].equalsIgnoreCase("version"))) {
                if (player instanceof Player) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&e%s v%s &7by &e%s", NAME, VERSION, AUTHOR)));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&7Source: &e%s", SOURCE)));
                } else {
                    getLogger().info(String.format("%s v%s by %s", NAME, VERSION, AUTHOR));
                    getLogger().info(String.format("Source: %s", SOURCE));
                }
            } else {
                if (player instanceof Player) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&cInvalid usage. Use &e/hat &cor &e/hat v&c."));
                } else {
                    getLogger().info("Invalid usage. Use '/hat' or '/hat v'.");
                }
            }
        }

        return true;
    }

    private static void wearHat(final Player player, final ItemStack item) {
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cYou aren't holding anything."));
            return;
        }

        final String itemName = normalizeName(item);
        if (item.getTypeId() < 1 || item.getTypeId() > 96) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&e%s &ccan't be worn as a hat.", itemName)));
            return;
        }

        if (player.getInventory().getHelmet().getType() == item.getType()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&e%s &cis already your hat.", itemName)));
            return;
        }

        final ItemStack hat = new ItemStack(item.getType(), 1, item.getDurability());
        if (item.getData() != null) {
            hat.setData(item.getData());
        }

        if (player.getInventory().getHelmet().getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&e%s &7worn as a hat.", itemName)));
        } else {
            final String helmetName = normalizeName(player.getInventory().getHelmet());

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(player.getInventory().getHelmet());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&e%s &7replaced with &e%s &7as the new hat.", helmetName, itemName)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getHelmet());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&cDropped &e%s &chat due to no pocket space.", helmetName)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&e%s &7worn as the new hat.", itemName)));
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