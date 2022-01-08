package landon.core.patchapi.patches.fupgrades.commands;

import landon.core.patchapi.patches.fupgrades.FactionUpgradeAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandFCrystal implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp())
            return true;
        if (args.length == 3) {
            boolean giveHeroic = (args[0].equalsIgnoreCase("giveh") || args[0].equalsIgnoreCase("giveheroic"));
            if (args[0].equalsIgnoreCase("give") || giveHeroic) {
                Player player = Bukkit.getPlayer(args[1]);
                if (!StringUtils.isNumeric(args[2])) {
                    sender.sendMessage(ChatColor.RED + "Please enter a valid number!");
                    return true;
                }
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player!");
                    return true;
                }
                int val = Integer.parseInt(args[2]);
                ItemStack items = giveHeroic ? FactionUpgradeAPI.createHeroicToken(val) : FactionUpgradeAPI.createFactionToken(val);
                player.getInventory().addItem(new ItemStack[] { items });
                sender.sendMessage(ChatColor.RED + "Given " + val + "x " + (giveHeroic ? "heroic" : "") + " faction tokens to " + player.getName());
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "/fcrystal give <player> <amount>");
        return false;
    }
}

