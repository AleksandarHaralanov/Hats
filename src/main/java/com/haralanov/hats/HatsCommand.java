package com.haralanov.hats;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.ChatColor;

import static org.bukkit.Bukkit.getLogger;

public class HatsCommand implements CommandExecutor {

    private final String NAME;
    private final String VERSION;
    private final String AUTHOR;
    private final String SOURCE;

    public HatsCommand(String NAME, String VERSION, String AUTHOR, String SOURCE) {
        this.NAME = NAME;
        this.VERSION = VERSION;
        this.AUTHOR = AUTHOR;
        this.SOURCE = SOURCE;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        Player player = (commandSender instanceof Player) ? (Player) commandSender : null;

        if (command.getName().equalsIgnoreCase("hats")) {
            if (player != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&e%s v%s &bby &e%s", NAME, VERSION, AUTHOR)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&bSource: &e%s", SOURCE)));
            } else {
                getLogger().info(String.format("%s v%s by %s", NAME, VERSION, AUTHOR));
                getLogger().info(String.format("Source: %s", SOURCE));
            }
        } else {
            if (player != null) {
                if (!(player.hasPermission("hats.wear") || player.isOp())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&cYou do not have permission to wear hats."));
                } else {
                    wearHat(player, player.getItemInHand());
                }
            } else {
                getLogger().info("Terminals cannot wear hats.");
            }
        }

        return true;
    }

    private void wearHat(Player player, ItemStack item) {
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cYou are not holding anything."));
        } else {
            int itemId = item.getTypeId();

            if (!(itemId >= 1 && itemId <= 96)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&e%s &ccannot be worn as a hat.", item.getType().name())));
            } else if (player.getInventory().getHelmet().getType() == item.getType()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&e%s &cis already worn as a hat.", item.getType().name())));
            } else {
                PlayerInventory inventory = player.getInventory();
                ItemStack hat = new ItemStack(item.getType(), item.getAmount() < 0 ? item.getAmount() : 1, item.getDurability());
                MaterialData data = item.getData();
                ItemStack helmet = inventory.getHelmet();

                if (data != null) {
                    hat.setData(item.getData());
                }

                if (helmet.getType() == Material.AIR) {
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&e%s &bworn as a hat.", item.getType().name())));
                } else if (inventory.firstEmpty() != -1) {
                    inventory.addItem(helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&e%s &bhat swapped for &e%s &bas the new hat.", helmet.getType().name(), item.getType().name())));
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&cNo pocket space, dropped old &e%s &chat to the ground.", helmet.getType().name())));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&e%s &bworn as the new hat.", item.getType().name())));
                }
            }
        }
    }

    private void removeExact(PlayerInventory inventory, ItemStack item) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].equals(item)) {
                if (contents[i].getAmount() > 1) {
                    contents[i].setAmount(contents[i].getAmount() - 1);
                } else {
                    inventory.setItem(i, null);
                }
                break;
            }
        }
    }
}