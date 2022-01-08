package landon.jurassiccore.commands;

import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ClearCommand implements CommandExecutor {
    private JurassicCore instance;

    public ClearCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player targetPlayer;
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("jurassiccore.clear") && !player.hasPermission("jurassiccore.clear.*") &&
                    !player.hasPermission("jurassiccore.*")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Clear.Permission.Command.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return true;
            }
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                targetPlayer = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Clear.Invalid.Message")));
                return true;
            }
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("jurassiccore.clear.other") && !player.hasPermission("jurassiccore.clear.*") &&
                        !player.hasPermission("jurassiccore.*")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Clear.Permission.Other.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return true;
                }
            }
            if (args.length == 1) {
                targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Clear.Connected.Message").replace("%player", args[0])));
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Clear.Invalid.Message")));
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                }
                return true;
            }
        }
        targetPlayer.getInventory().clear();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
            if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Clear.Cleared.Yourself.Message")));
                return true;
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
                .getString("Commands.Clear.Cleared.Target.Message").replace("%player", targetPlayer.getName())));
        return true;
    }
}
