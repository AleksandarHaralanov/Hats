package io.github.aleksandarharalanov.hats.command;

import io.github.aleksandarharalanov.hats.handler.LightLevel;
import io.github.aleksandarharalanov.hats.handler.LightRadius;
import org.bukkit.Effect;
import org.bukkit.Location;
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

import static io.github.aleksandarharalanov.hats.Hats.*;
import static io.github.aleksandarharalanov.hats.handler.EffectsHandler.areEffectsEnabled;
import static io.github.aleksandarharalanov.hats.handler.EffectsHandler.getPlayerEffects;
import static io.github.aleksandarharalanov.hats.handler.LightHandler.*;
import static io.github.aleksandarharalanov.hats.util.AboutUtil.about;
import static io.github.aleksandarharalanov.hats.util.AccessUtil.commandInGameOnly;
import static io.github.aleksandarharalanov.hats.util.AccessUtil.hasPermission;
import static io.github.aleksandarharalanov.hats.util.ColorUtil.translate;
import static io.github.aleksandarharalanov.hats.util.LoggerUtil.logInfo;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hat")) {
            if (getPluginConfig().getBoolean("hats.toggle", true) || hasPermission(sender, "hats.settings")) {
                switch (args.length) {
                    case 0:
                        hatCommand(sender);
                        break;
                    case 1:
                        switch (args[0].toLowerCase()) {
                            case "effects":
                                effectsCommand(sender);
                                break;
                            case "light":
                                lightCommand(sender);
                                break;
                            case "about":
                                about(sender, getInstance());
                                break;
                            case "settings":
                                helpSettingsCommand(sender);
                                break;
                            default:
                                helpCommand(sender);
                                break;
                        }
                        break;
                    case 2:
                        switch (args[0].toLowerCase()) {
                            case "settings":
                                if (args[1].equalsIgnoreCase("reload")) {
                                    reloadCommand(sender);
                                } else {
                                    helpSettingsCommand(sender);
                                }
                                break;
                            case "light":
                                if (args[1].equalsIgnoreCase("view")) {
                                    viewCommand(sender);
                                } else {
                                    helpCommand(sender);
                                }
                                break;
                            default:
                                helpCommand(sender);
                                break;
                        }
                        break;
                    case 3:
                        if (args[0].equalsIgnoreCase("settings")) {
                            switch (args[1].toLowerCase()) {
                                case "toggle":
                                    if (args[2].equalsIgnoreCase("hats") ||
                                            args[2].equalsIgnoreCase("effects") ||
                                                args[2].equalsIgnoreCase("light")) {
                                        toggleCommand(sender, args[2].toLowerCase());
                                    } else {
                                        helpSettingsCommand(sender);
                                    }
                                    break;
                                case "alter":
                                    if (args[2].equalsIgnoreCase("radius") ||
                                            args[2].equalsIgnoreCase("level")) {
                                        alterCommand(sender, args[2].toLowerCase());
                                    } else {
                                        helpSettingsCommand(sender);
                                    }
                                    break;
                                case "add":
                                case "remove":
                                    try {
                                        int blockId = Integer.parseInt(args[2]);
                                        if (blockId >= 1 && blockId <= 96) {
                                            modifyCommand(sender, blockId, args[1].equalsIgnoreCase("add"));
                                        } else {
                                            helpSettingsCommand(sender);
                                        }
                                    } catch (NumberFormatException e) {
                                        helpSettingsCommand(sender);
                                    }
                                    break;
                                default:
                                    helpSettingsCommand(sender);
                                    break;
                            }
                        } else {
                            helpCommand(sender);
                        }
                        break;
                    default:
                        if (args[0].equalsIgnoreCase("settings")) {
                            helpSettingsCommand(sender);
                        } else {
                            helpCommand(sender);
                        }
                        break;
                }
            } else {
                sender.sendMessage(translate("&cHats are currently disabled."));
            }
        }

        return true;
    }

    private static void helpCommand(CommandSender sender) {
        String[] messages = {
                "&bHats' commands:",
                "&e/hat &7- Wear the block in hand as a hat.",
                "&e/hat effects &7- Toggle personal effects.",
                "&e/hat light &7- Toggle personal hat light.",
                "&e/hat light view &7- View hat light source blocks.",
                "&e/hat about &7- See Hats' information.",
                "&e/hat settings &7- Manage Hats' settings. (Staff)"
        };

        for (String message : messages) {
            if (sender instanceof Player) {
                sender.sendMessage(translate(message));
            } else {
                logInfo(message.replaceAll("&.", ""));
            }
        }
    }

    private static void helpSettingsCommand(CommandSender sender) {
        String[] messages = {
                "&bHats' settings commands:",
                "&e/hat settings <args...> &7- Manage Hats' settings.",
                "&bArguments:",
                "&ereload &7- Reload Hats' configuration.",
                "&etoggle <hats | effects | light> &7- Toggle features.",
                "&ealter <radius | level> &7- Alter hat light behavior.",
                "&e<add | remove> <1-96> &7- Modify hat light source blocks."
        };

        for (String message : messages) {
            if (sender instanceof Player) {
                sender.sendMessage(translate(message));
            } else {
                logInfo(message.replaceAll("&.", ""));
            }
        }
    }

    private static void hatCommand(CommandSender sender) {
        if (commandInGameOnly(sender)) {
            return;
        }

        if (!hasPermission(sender, "hats.wear", "You don't have permission to wear hats.")) {
            return;
        }

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(translate("&cYou aren't holding anything."));
            return;
        }

        String itemName = normalizeName(item);
        if (item.getTypeId() < 1 || item.getTypeId() > 96) {
            player.sendMessage(translate(String.format("&e%s &ccan't be worn as a hat.", itemName)));
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack helmet = inventory.getHelmet();
        if (helmet.getType() == item.getType() && helmet.getDurability() == item.getDurability()) {
            player.sendMessage(translate(String.format("&e%s &cis already your hat.", itemName)));
            return;
        }

        ItemStack hat = new ItemStack(item.getType(), 1, item.getDurability());
        if (item.getData() != null) {
            hat.setData(item.getData());
        }

        if (helmet.getType() == Material.AIR) {
            player.sendMessage(translate(String.format("&e%s &7worn as a hat.", itemName)));
        } else {
            String helmetName = normalizeName(helmet);
            if (inventory.firstEmpty() != -1) {
                inventory.addItem(helmet);
                player.sendMessage(translate(String.format("&e%s &7replaced with &e%s &7as the new hat.", helmetName, itemName)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                player.sendMessage(translate(String.format("&cDropped &e%s &chat due to no pocket space.", helmetName)));
                player.sendMessage(translate(String.format("&e%s &7worn as the new hat.", itemName)));
            }
        }

        if (areEffectsEnabled(player)) {
            Location location = player.getLocation();
            location.setY(location.getY() + player.getEyeHeight());
            player.playEffect(location, Effect.STEP_SOUND, item.getType().getId());
        }

        inventory.setHelmet(hat);
        removeExact(inventory, hat);

        ArrayList<Integer> source = (ArrayList<Integer>) getPluginConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 52, 62, 89, 90, 91)
        );
        if (!source.contains(hat.getTypeId())) {
            clearSpecificLight(player);
        }
    }

    private static void effectsCommand(CommandSender sender) {
        if (commandInGameOnly(sender)) {
            return;
        }

        if (!getPluginConfig().getBoolean("hats.effects", true)) {
            sender.sendMessage(translate("&cEffects feature is currently disabled."));
            return;
        }

        if (!hasPermission(sender, "hats.wear", "You don't have permission to wear hats.")) {
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        location.setY(location.getY() + player.getEyeHeight());
        if (getPlayerEffects().contains(player.getName())) {
            getPlayerEffects().remove(player.getName());
            player.sendMessage(translate("&7Effects &aEnabled&7."));
            player.playEffect(location, Effect.CLICK1, 0);
        } else {
            getPlayerEffects().add(player.getName());
            player.sendMessage(translate("&7Effects &cDisabled&7."));
            player.playEffect(location, Effect.CLICK2, 0);
        }

        getPlayersConfig().setProperty("players.effects-disabled", getPlayerEffects());
        getPlayersConfig().saveConfig();
    }

    private static void lightCommand(CommandSender sender) {
        if (commandInGameOnly(sender)) {
            return;
        }

        if (!getPluginConfig().getBoolean("hats.light.toggle", true)) {
            sender.sendMessage(translate("&cDynamic hat light feature is currently disabled."));
            return;
        }

        if (!hasPermission(sender, "hats.light", "You don't have permission to emit hat light.")) {
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        location.setY(location.getY() + player.getEyeHeight());
        if (getPlayerLight().contains(player.getName())) {
            getPlayerLight().remove(player.getName());
            clearSpecificLight(player);
            player.sendMessage(translate("&7Light &cDisabled&7."));
            if (areEffectsEnabled(player)) {
                player.playEffect(location, Effect.CLICK2, 0);
            }
        } else {
            getPlayerLight().add(player.getName());
            player.sendMessage(translate("&7Light &aEnabled&7."));
            if (areEffectsEnabled(player)) {
                player.playEffect(location, Effect.CLICK1, 0);
            }
        }

        getPlayersConfig().setProperty("players.light-enabled", getPlayerLight());
        getPlayersConfig().saveConfig();
    }

    private static void viewCommand(CommandSender sender) {
        ArrayList<Integer> source = (ArrayList<Integer>) getPluginConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 52, 62, 89, 90, 91)
        );

        if (sender instanceof Player) {
            sender.sendMessage(translate(String.format("&7Hat Light Source(s): &e%s", source.toString())));
        } else {
            logInfo(String.format("Hat Light Source(s): %s", source.toString()));
        }
    }

    private static void reloadCommand(CommandSender sender) {
        if (!hasPermission(sender, "hats.settings", "You don't have permission to reload the Hats config.")) {
            return;
        }

        getPluginConfig().loadConfig();
        getPlayersConfig().loadConfig();
        clearAllLight();

        if (sender instanceof Player) {
            sender.sendMessage(translate("&aHats config reloaded."));
        }
    }

    private static void modifyCommand(CommandSender sender, int blockId, boolean check) {
        ArrayList<Integer> source = (ArrayList<Integer>) getPluginConfig().getIntList(
                "hats.light.source", Arrays.asList(10, 11, 50, 51, 52, 62, 89, 90, 91)
        );
        ItemStack block = new ItemStack(blockId, 1);
        String blockName = normalizeName(block);

        if (!hasPermission(sender, "hats.settings", "You don't have permission to change the Hats config.")) {
            return;
        }

        if (check) {
            if (source.contains(blockId)) {
                if (sender instanceof Player) {
                    sender.sendMessage(translate(String.format("&e%s &cis already a light source.", blockName)));
                } else {
                    logInfo(String.format("%s is already a light source.", blockName));
                }
            } else {
                source.add(blockId);
                getPluginConfig().setProperty("hats.light.source", source);
                getPluginConfig().saveConfig();
                if (sender instanceof Player) {
                    sender.sendMessage(translate(String.format("&e%s &aadded as a light source.", blockName)));
                } else {
                    logInfo(String.format("%s added as a light source.", blockName));
                }
            }
        } else {
            if (!source.contains(blockId)) {
                if (sender instanceof Player) {
                    sender.sendMessage(translate(String.format("&e%s &cis currently not a light source.", blockName)));
                } else {
                    logInfo(String.format("%s is currently not a light source.", blockName));
                }
            } else {
                source.remove((Integer) blockId);
                getPluginConfig().setProperty("hats.light.source", source);
                getPluginConfig().saveConfig();
                if (sender instanceof Player) {
                    sender.sendMessage(translate(String.format("&e%s &aremoved as a light source.", blockName)));
                } else {
                    logInfo(String.format("%s removed as a light source.", blockName));
                }
            }
        }

        clearAllLight();
    }

    private static void toggleCommand(CommandSender sender, String args) {
        if (!hasPermission(sender, "hats.settings", "You don't have permission to change the Hats config.")) {
            return;
        }

        String configKey;
        String messageKey;
        switch (args) {
            case "hats":
                configKey = "hats.toggle";
                messageKey = "Hats plugin toggled";
                break;
            case "effects":
                configKey = "hats.effects";
                messageKey = "Hats effects feature toggled";
                break;
            case "light":
                configKey = "hats.light.toggle";
                messageKey = "Hats light feature toggled";
                break;
            default:
                return;
        }

        boolean current = getPluginConfig().getBoolean(configKey, true);
        getPluginConfig().setProperty(configKey, !current);
        getPluginConfig().saveConfig();
        clearAllLight();

        String result;
        if (!current) {
            result = "&aON";
        } else {
            result = "&cOFF";
        }

        if (sender instanceof Player) {
            sender.sendMessage(translate(String.format("&7%s %s&7.", messageKey, result)));
        } else {
            logInfo(String.format("%s: %s", messageKey, result));
        }
    }

    private static void alterCommand(CommandSender sender, String args) {
        if (!hasPermission(sender, "hats.settings", "You don't have permission to change the Hats config.")) {
            return;
        }

        String configKey;
        String messageKey;
        String newValue;
        switch (args.toLowerCase()) {
            case "radius":
                configKey = "hats.light.radius";
                messageKey = "Hat light radius altered to";

                String currentRadius = getPluginConfig().getString(configKey, "NARROW");
                LightRadius radius = LightRadius.valueOf(currentRadius.toUpperCase());
                LightRadius newRadius = (radius == LightRadius.NARROW) ? LightRadius.WIDE : LightRadius.NARROW;
                newValue = newRadius.name();
                break;
            case "level":
                configKey = "hats.light.level";
                messageKey = "Hat light level altered to";

                String currentLevel = getPluginConfig().getString(configKey, "LOW");
                LightLevel level = LightLevel.valueOf(currentLevel.toUpperCase());
                LightLevel newLevel = LightLevel.getNextLevel(level);
                newValue = newLevel.name();
                break;
            default:
                return;
        }

        getPluginConfig().setProperty(configKey, newValue);
        getPluginConfig().saveConfig();

        String result = newValue.substring(0, 1).toUpperCase() + newValue.substring(1).toLowerCase();
        if (sender instanceof Player) {
            sender.sendMessage(translate(String.format("&7%s: &e%s", messageKey, result)));
        } else {
            logInfo(String.format("%s: %s", messageKey, result));
        }

        clearAllLight();
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
            if (item != null && item.getType() == hat.getType() && item.getDurability() == hat.getDurability()) {
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