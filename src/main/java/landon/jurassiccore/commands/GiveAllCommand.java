package landon.jurassiccore.commands;

import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class GiveAllCommand implements CommandExecutor {
    private JurassicCore instance;

    public GiveAllCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.giveall") && !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.GiveAll.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (args.length > 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.GiveAll.Invalid.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        int amount = 1;
        if (args.length == 1)
            if (args[0].matches("[0-9]+")) {
                amount = Integer.valueOf(args[0]).intValue();
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.GiveAll.Numerical.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return true;
            }
        ItemStack is = player.getItemInHand().clone();
        if (is == null) {
            is = new ItemStack(Material.AIR);
        } else {
            is.setAmount(1);
        }
        String itemName = ItemUtil.getItemName(is);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getUniqueId().equals(player.getUniqueId()))
                continue;
            if (is.getType() != Material.AIR)
                for (int i = 0; i < amount; i++) {
                    all.getInventory().addItem(new ItemStack[]{is});
                }
            all.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.GiveAll.Given.Target.Message")
                                    .replace("%amount", amount + "").replace("%item", itemName))
                            .replace("%player", player.getName()));
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Commands.GiveAll.Given.Player.Message").replace("%amount", amount + "")
                        .replace("%item", itemName)));
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        return true;
    }
}
