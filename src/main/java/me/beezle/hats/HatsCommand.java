package me.beezle.hats;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import static org.bukkit.Bukkit.getLogger;

import java.util.Objects;

public class HatsCommand implements CommandExecutor {

    String version;

    public HatsCommand(String version) {
        this.version = version;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        Player player = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (strings.length > 0 && !strings[0].equalsIgnoreCase("version")) {
            if (player != null) {
                commandSender.sendMessage(ChatColor.RED + "Invalid arguments. Correct usage is " + ChatColor.YELLOW + "/hat [version]" + ChatColor.RED + ".");
            } else {
                getLogger().info("Invalid arguments. Correct usage is /hat [version].");
            }

            return true;
        } else if (strings.length > 0 && strings[0].equalsIgnoreCase("version")) {
            if (player != null) {
                commandSender.sendMessage(String.format(ChatColor.AQUA + "Currently running on version " + ChatColor.YELLOW + "%s" + ChatColor.AQUA + ".", version));
            } else {
                getLogger().info(String.format("[Hats] Currently running on version %s.", version));
            }

            return true;
        } else {
            if (!(commandSender instanceof Player)) {
                getLogger().info("[Hats] Terminals cannot wear hats.");
                return true;
            }

            if (!(commandSender.hasPermission("hats.wear") || commandSender.isOp())) {
                commandSender.sendMessage(ChatColor.RED + "You do not have permissions to wear hats.");
            } else {
                this.hatWear(player, Objects.requireNonNull(player).getItemInHand());
            }

            return true;
        }
    }

    public void hatWear(Player player, ItemStack item) {
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding a block.");
        } else {
            int itemId = item.getTypeId();

            if (!(itemId >= 1 && itemId <= 96)) {
                player.sendMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.RED + " cannot be worn as a hat.", item.getType().name()));
            } else if (player.getInventory().getHelmet().getType() == item.getType()) {
                player.sendMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.RED + " already worn as a hat.", item.getType().name()));
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
                    player.sendMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.AQUA + " worn as a hat.", item.getType().name()));
                } else if (inventory.firstEmpty() != -1) {
                    inventory.addItem(helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.AQUA + " swapped for " + ChatColor.YELLOW + "%s" + ChatColor.AQUA + " as the new hat.", helmet.getType().name(), item.getType().name()));
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                    inventory.setHelmet(hat);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        removeExact(inventory, item);
                    }
                    player.sendMessage(String.format(ChatColor.RED + "No pocket space, dropping old " + ChatColor.YELLOW + "%s" + ChatColor.RED + " hat.", helmet.getType().name()));
                    player.sendMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.AQUA + " worn as the new hat.", item.getType().name()));
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