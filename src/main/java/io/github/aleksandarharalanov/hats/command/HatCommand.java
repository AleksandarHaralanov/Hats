package io.github.aleksandarharalanov.hats.command;

import io.github.aleksandarharalanov.hats.Hats;
import io.github.aleksandarharalanov.hats.handler.LightHandler;
import io.github.aleksandarharalanov.hats.util.AboutUtil;
import io.github.aleksandarharalanov.hats.util.ColorUtil;
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

import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (command.getName().equalsIgnoreCase("hat")) {
            if (Hats.getConfig().getBoolean("hats.toggle", true) ||
                    player == null || player.isOp() || player.hasPermission("hats.settings")) {
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
                            case "settings":
                            case "s":
                                invalidSettingsUsage(player);
                                break;
                            default:
                                invalidUsage(player);
                                break;
                        }
                        break;
                    case 2:
                        if (args[0].equalsIgnoreCase("settings") ||
                                args[0].equalsIgnoreCase("s")) {
                            switch (args[1].toLowerCase()) {
                                case "view":
                                case "v":
                                    viewCommand(player);
                                    break;
                                default:
                                    invalidSettingsUsage(player);
                                    break;
                            }
                        } else {
                            invalidUsage(player);
                        }
                        break;
                    case 3:
                        if (args[0].equalsIgnoreCase("settings") ||
                                args[0].equalsIgnoreCase("s")) {
                            switch (args[1].toLowerCase()) {
                                case "toggle":
                                case "t":
                                    if (args[2].equalsIgnoreCase("h") ||
                                            args[2].equalsIgnoreCase("l") ||
                                            args[2].equalsIgnoreCase("r")) {
                                        toggleCommand(player, args[2].toLowerCase());
                                    } else {
                                        invalidSettingsUsage(player);
                                    }
                                    break;
                                case "add":
                                case "a":
                                case "remove":
                                case "r":
                                    try {
                                        int blockId = Integer.parseInt(args[2]);
                                        if (blockId >= 1 && blockId <= 96) {
                                            if (args[1].equalsIgnoreCase("add") ||
                                                    args[1].equalsIgnoreCase("a")) {
                                                modifyCommand(player, blockId, true);
                                            } else if (args[1].equalsIgnoreCase("remove") ||
                                                    args[1].equalsIgnoreCase("r")) {
                                                modifyCommand(player, blockId, false);
                                            }
                                        } else {
                                            invalidSettingsUsage(player);
                                        }
                                    } catch (NumberFormatException e) {
                                        invalidSettingsUsage(player);
                                    }
                                    break;
                                default:
                                    invalidSettingsUsage(player);
                                    break;
                            }
                        } else {
                            invalidUsage(player);
                        }
                        break;
                    default:
                        invalidUsage(player);
                        break;
                }
            } else {
                player.sendMessage(ColorUtil.translate("&cHats is currently disabled."));
            }
        }

        return true;
    }

    private static void hatCommand(Player player) {
        if (player == null) {
            getServer().getLogger().info("You must be in-game to run this command.");
            return;
        }

        if (!(player.hasPermission("hats.wear") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to wear hats."));
            return;
        }

        wearHat(player, player.getItemInHand());
    }

    private static void lightCommand(Player player) {
        if (player == null) {
            getServer().getLogger().info("You must be in-game to run this command.");
            return;
        }

        if (!Hats.getConfig().getBoolean("hats.light.toggle", true)) {
            player.sendMessage(ColorUtil.translate("&cHats dynamic light is currently disabled."));
            return;
        }

        if (!(player.hasPermission("hats.light") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to emit hat light."));
            return;
        }

        lightHat(player);
    }

    private static void reloadCommand(Player player) {
        if (player == null) {
            Hats.getConfig().loadConfig();
            LightHandler.clearAllLight();
            return;
        }

        if (!(player.hasPermission("hats.settings") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to reload the Hats config."));
            return;
        }

        Hats.getConfig().loadConfig();
        LightHandler.clearAllLight();
        player.sendMessage(ColorUtil.translate("&aHats config reloaded."));
    }

    private static void toggleCommand(Player player, String args) {
        String configKey;
        String messageKey;

        switch (args) {
            case "h":
                configKey = "hats.toggle";
                messageKey = "Hats toggled";
                break;
            case "l":
                configKey = "hats.light.toggle";
                messageKey = "Hats light toggled";
                break;
            case "r":
                configKey = "hats.light.radius";
                messageKey = "Hats wider light radius toggled";
                break;
            default:
                return;
        }

        if (player != null && !(player.hasPermission("hats.settings") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to modify the Hats config."));
            return;
        }

        boolean current = Hats.getConfig().getBoolean(configKey, true);
        Hats.getConfig().setProperty(configKey, !current);
        Hats.getConfig().saveConfig();
        LightHandler.clearAllLight();

        String result = String.valueOf(!current).substring(0, 1).toUpperCase() + String.valueOf(!current).substring(1);
        if (player == null) {
            getServer().getLogger().info(String.format("%s: %s", messageKey, result));
        } else {
            player.sendMessage(ColorUtil.translate(String.format("&7%s: &e%s", messageKey, result)));
        }

    }

    private static void modifyCommand(Player player, int blockId, boolean check) {
        ArrayList<Integer> source = (ArrayList<Integer>) Hats.getConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 89, 90, 91));
        ItemStack block = new ItemStack(blockId, 1);
        String blockName = normalizeName(block);

        if (player != null && !(player.hasPermission("hats.settings") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to modify the Hats config."));
            return;
        }

        if (check) {
            if (source.contains(blockId)) {
                if (player == null) {
                    getServer().getLogger().info(String.format("%s is already a light source.", blockName));
                } else {
                    player.sendMessage(ColorUtil.translate(String.format("&e%s &cis already a light source.", blockName)));
                }
            } else {
                source.add(blockId);
                Hats.getConfig().setProperty("hats.light.source", source);
                Hats.getConfig().saveConfig();
                if (player == null) {
                    getServer().getLogger().info(String.format("%s added as a light source.", blockName));
                } else {
                    player.sendMessage(ColorUtil.translate(String.format("&e%s &aadded as a light source.", blockName)));
                }
            }
        } else {
            if (!source.contains(blockId)) {
                if (player == null) {
                    getServer().getLogger().info(String.format("%s is currently not a light source.", blockName));
                } else {
                    player.sendMessage(ColorUtil.translate(String.format("&e%s &cis currently not a light source.", blockName)));
                }
            } else {
                source.remove((Integer) blockId);
                Hats.getConfig().setProperty("hats.light.source", source);
                Hats.getConfig().saveConfig();
                if (player == null) {
                    getServer().getLogger().info(String.format("%s removed as a light source.", blockName));
                } else {
                    player.sendMessage(ColorUtil.translate(String.format("&e%s &aremoved as a light source.", blockName)));
                }
            }
        }

        LightHandler.clearAllLight();
    }

    private static void viewCommand(Player player) {
        ArrayList<Integer> source = (ArrayList<Integer>) Hats.getConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 89, 90, 91));

        if (player != null && !(player.hasPermission("hats.settings") || player.isOp())) {
            player.sendMessage(ColorUtil.translate("&cYou don't have permission to view the Hats config."));
            return;
        }

        if (player != null) {
            player.sendMessage(ColorUtil.translate(String.format("&7Source(s): &e%s", source.toString())));
        } else {
            getServer().getLogger().info(String.format("Source(s): %s", source.toString()));
        }
    }

    private static void invalidUsage(Player player) {
        if (player == null) {
            getServer().getLogger().info(String.format(
                    "Invalid usage. Use '%s'.", getServer().getPluginCommand("hat").getUsage()));
            return;
        }

        player.sendMessage(ColorUtil.translate(String.format(
                "&cInvalid usage. Use &e%s&c.", getServer().getPluginCommand("hat").getUsage())));
    }

    private static void invalidSettingsUsage(Player player) {
        if (player == null) {
            getServer().getLogger().info(
                    "Invalid usage. See below:");
            getServer().getLogger().info(
                    "/hat s t [h | l | r] - Toggle Hats/Hat Light/Wider Light Radius.");
            getServer().getLogger().info(
                    "/hat s [a | r] [ID 1-96] - Add/Remove ID(s) of light source block hats.");
            getServer().getLogger().info(
                    "/hat s v - View ID(s) of current light source block hats.");
            return;
        }

        player.sendMessage(ColorUtil.translate(
                "&cInvalid usage. See below:"));
        player.sendMessage(ColorUtil.translate(
                "&e/hat s t [h | l | r] &7- Toggle Hats/Hat Light/Wider Light Radius."));
        player.sendMessage(ColorUtil.translate(
                "&e/hat s [a | r] [1-96] &7- Add/Remove ID(s) of light source block hats."));
        player.sendMessage(ColorUtil.translate(
                "&e/hat s v &7- View ID(s) of current light source block hats."));
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
        if (LightHandler.getPlayerLight().contains(player.getName())) {
            LightHandler.getPlayerLight().remove(player.getName());
            LightHandler.clearSpecificLight(player);
            player.sendMessage(ColorUtil.translate("&7Hat light &cDisabled&7."));
        } else {
            LightHandler.getPlayerLight().add(player.getName());
            player.sendMessage(ColorUtil.translate("&7Hat light &aEnabled&7."));
        }

        Hats.getConfig().setProperty("hats.light.players", LightHandler.getPlayerLight());
        Hats.getConfig().saveConfig();
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
}