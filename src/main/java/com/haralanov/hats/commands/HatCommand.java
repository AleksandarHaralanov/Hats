package com.haralanov.hats.commands;

import com.haralanov.hats.Hats;
import com.haralanov.hats.LightHandler;
import com.haralanov.hats.utils.AboutUtil;
import com.haralanov.hats.utils.ColorUtil;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

import static org.bukkit.Bukkit.getServer;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (command.getName().equalsIgnoreCase("hat")) {
            switch (args.length) {
                case 0:
                    hatCommand(player);
                    break;
                case 1:
                    switch (args[0].toLowerCase()) {
                        case "about":
                        case "a":
                            AboutUtil.about(player, Hats.getInstance());
                            break;
                        case "light":
                        case "l":
                            lightCommand(player);
                            break;
                        case "reload":
                        case "r":
                            reloadCommand(player);
                            break;
                        default:
                            invalidUsage(player);
                            break;
                    }
                    break;
                default:
                    invalidUsage(player);
                    break;
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

        PlayerInventory inventory = player.getInventory();
        ItemStack helmet = inventory.getHelmet();
        if (helmet.getType() == item.getType() && helmet.getDurability() == item.getDurability()) {
            player.sendMessage(ColorUtil.translate(String.format("&e%s &cis already your hat.", itemName)));
            return;
        }

        ItemStack hat = new ItemStack(item.getType(), 1, item.getDurability());
        if (item.getData() != null) {
            hat.setData(item.getData());
        }

        if (helmet.getType() == Material.AIR) {
            player.sendMessage(ColorUtil.translate(String.format("&e%s &7worn as a hat.", itemName)));
        } else {
            String helmetName = normalizeName(helmet);
            if (inventory.firstEmpty() != -1) {
                inventory.addItem(helmet);
                player.sendMessage(ColorUtil.translate(String.format(
                        "&e%s &7replaced with &e%s &7as the new hat.", helmetName, itemName)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                player.sendMessage(ColorUtil.translate(String.format(
                        "&cDropped &e%s &chat due to no pocket space.", helmetName)));
                player.sendMessage(ColorUtil.translate(String.format(
                        "&e%s &7worn as the new hat.", itemName)));
            }
        }

        inventory.setHelmet(hat);
        removeExact(inventory, hat);
    }

    private static void lightHat(Player player) {
        if (LightHandler.toggle.contains(player.getName())) {
            LightHandler.toggle.remove(player.getName());
            LightHandler.clearSpecificLight(player);
            player.sendMessage(ColorUtil.translate("&7Hat light &cDisabled&7."));
        } else {
            LightHandler.toggle.add(player.getName());
            player.sendMessage(ColorUtil.translate("&7Hat light &aEnabled&7."));
        }

        Hats.getConfig().setProperty("hat-light.players", LightHandler.toggle);
        Hats.getConfig().saveConfig();
    }

    private static void hatCommand(Player player) {
        if (player != null) {
            if (!(player.hasPermission("hats.wear") || player.isOp())) {
                player.sendMessage(ColorUtil.translate("&cYou don't have permission to wear hats."));
            } else {
                wearHat(player, player.getItemInHand());
            }
        } else {
            getServer().getLogger().info("You must be in-game to run this command.");
        }
    }

    private static void lightCommand(Player player) {
        if (player != null) {
            if (Hats.getConfig().getBoolean("hat-light.enabled", true)) {
                if (!(player.hasPermission("hats.light") || player.isOp())) {
                    player.sendMessage(ColorUtil.translate("&cYou don't have permission to emit hat light."));
                } else {
                    lightHat(player);
                }
            } else {
                player.sendMessage(ColorUtil.translate("&cThis Hats feature is currently disabled."));
            }
        } else {
            getServer().getLogger().info("You must be in-game to run this command.");
        }
    }

    private static void reloadCommand(Player player) {
        if (player != null) {
            if (!(player.hasPermission("hats.modify") || player.isOp())) {
                player.sendMessage(ColorUtil.translate("&cYou don't have permission to reload Hats config."));
            } else {
                Hats.getConfig().loadConfig();
                LightHandler.clearAllLight();
                player.sendMessage(ColorUtil.translate("&aHats config reloaded."));
            }
        } else {
            Hats.getConfig().loadConfig();
            LightHandler.clearAllLight();
        }
    }

    private static void invalidUsage(Player player) {
        if (player != null) {
            player.sendMessage(ColorUtil.translate(String.format(
                    "&cInvalid usage. Use &e%s&c.", getServer().getPluginCommand("hat").getUsage())));
        } else {
            getServer().getLogger().info(String.format(
                    "Invalid usage. Use '%s'.", getServer().getPluginCommand("hat").getUsage()));
        }
    }

    private static void removeExact(PlayerInventory inventory, ItemStack hat) {
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null
                    && item.getType() == hat.getType()
                    && item.getDurability() == hat.getDurability()) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.setItem(i, null);
                }
                break;
            }
        }
    }

    private static String normalizeName(ItemStack item) {
        Material itemType = item.getType();
        String itemName = itemType.name().toLowerCase().replace('_', ' ');
        StringBuilder normalizedItemName = new StringBuilder();

        switch (itemType) {
            case WOOL:
                Wool wool = (Wool) item.getData();
                String colorName = wool.getColor().name().toLowerCase().replace('_', ' ');
                String[] colorSplit = colorName.split(" ");
                for (String color : colorSplit) {
                    normalizedItemName
                            .append(Character.toUpperCase(color.charAt(0)))
                            .append(color.substring(1))
                            .append(" ");
                }
                normalizedItemName.append("Wool");
                return normalizedItemName.toString();
            case LOG:
            case LEAVES:
            case SAPLING:
                Tree tree = (Tree) item.getData();
                String speciesName = tree.getSpecies().name().toLowerCase();
                normalizedItemName
                        .append(Character.toUpperCase(speciesName.charAt(0)))
                        .append(speciesName.substring(1))
                        .append(" ");
                switch (item.getType()) {
                    case LOG:
                        normalizedItemName.append("Log");
                        break;
                    case LEAVES:
                        normalizedItemName.append("Leaves");
                        break;
                    case SAPLING:
                    default:
                        normalizedItemName.append("Sapling");
                        break;
                }
                return normalizedItemName.toString();
            case STEP:
            case DOUBLE_STEP:
                Step step = (Step) item.getData();
                String materialName = step.getMaterial().name().toLowerCase();
                normalizedItemName
                        .append(Character.toUpperCase(materialName.charAt(0)))
                        .append(materialName.substring(1))
                        .append(" ");
                switch (item.getType()) {
                    case STEP:
                        normalizedItemName.append("Slab");
                        break;
                    case DOUBLE_STEP:
                    default:
                        normalizedItemName.append("Double Slab");
                        break;
                }
                return normalizedItemName.toString();
            case JACK_O_LANTERN:
                normalizedItemName.append("Jack 'o' Lantern");
                return normalizedItemName.toString();
            default:
                String[] words = itemName.split(" ");
                for (String word : words) {
                    normalizedItemName
                            .append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
                return normalizedItemName.toString().trim();
        }
    }
}