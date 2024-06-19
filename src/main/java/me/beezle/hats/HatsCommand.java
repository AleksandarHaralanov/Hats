package me.beezle.hats;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

public class HatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("[Hats v1.0.1] You cannot run this command from the terminal.");
            return true;
        }

        if (!(commandSender.hasPermission("hats.wear") || commandSender.isOp()) && strings[0].equalsIgnoreCase("hat")) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permissions to wear hats.");
            return true;
        }

        PlayerInventory inventory = ((Player) commandSender).getInventory();
        if (!(inventory.getItemInHand().getType() != Material.AIR && inventory.getItemInHand().getType() != null)) {
            commandSender.sendMessage(ChatColor.RED + "You are not holding any item.");
            return true;
        }

        ItemStack inHand = inventory.getItemInHand().clone();
        boolean allowArmor = Hats.getInstance().getConfig().get("allowArmor", false);
        if (!allowArmor && isArmorType(inHand)) {
            commandSender.sendMessage(ChatColor.RED + String.format("You are not allowed to wear %s as a hat!", inHand.getType()));
            return true;
        }

        if (inventory.getHelmet().getType() != Material.AIR && inventory.getHelmet().getType() != null) {
            inventory.setItemInHand(new ItemStack(inventory.getHelmet().getType(), inventory.getHelmet().getAmount()));
            inventory.setHelmet(inHand);
            commandSender.sendMessage(ChatColor.AQUA + String.format("Hat swapped for %s!", inHand.getType()));
        }
        else {
            inventory.setItemInHand(null);
            inventory.setHelmet(inHand);
            commandSender.sendMessage(ChatColor.AQUA + String.format("%s worn as a hat!", inHand.getType()));
        }
        return true;
    }

    public boolean isArmorType(ItemStack itemStack) {
        return isType(itemStack,
                Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
                Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
                Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
    }

    public boolean isType(ItemStack itemStack, Material... materials) {
        for (Material mat : materials) {
            if (itemStack.getType() == mat) {
                return true;
            }
        }
        return false;
    }
}