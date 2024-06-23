package me.beezle.hats;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.material.MaterialData;

public class HatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        Player player = null;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("[Hats v2.0.0] You cannot run this command from the terminal.");
        }
        else {
            player = (Player) commandSender;
        }

        if (!(commandSender.hasPermission("hats.wear") || commandSender.isOp())) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permissions to wear hats.");
        }
        else {
            this.hatWear(player, player.getItemInHand());
        }

        return true;
    }

    public void hatWear(Player player, ItemStack item) {
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding an item.");
        }
        else {
            int itemId = item.getTypeId();
            if (!(itemId >= 1 && itemId <= 96)) {
                player.sendMessage(ChatColor.RED + String.format("%s cannot be worn as a hat.", item.getType().name()));
            }
            else if (player.getInventory().getHelmet().getType() == item.getType()) {
                player.sendMessage(ChatColor.RED + String.format("%s already worn as a hat.", item.getType().name()));
            }
            else {
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
                    player.sendMessage(ChatColor.AQUA + String.format("%s worn as a hat.", item.getType().name()));
                }
                else if (inventory.firstEmpty() != -1) {
                    inventory.addItem(helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(ChatColor.AQUA + String.format("%s swapped for %s as the new hat.", helmet.getType().name(), item.getType().name()));
                }
                else {
                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(ChatColor.RED + String.format("No pocket space, dropped old %s hat.", helmet.getType().name()));
                    player.sendMessage(ChatColor.AQUA + String.format("%s worn as the new hat.", item.getType().name()));
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