package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EnderChestCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public EnderChestCommand(JurassicCore instance) {
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
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.enderchest") && 
      !player.hasPermission("jurassiccore.enderchest.*") && 
      !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.EnderChest.Permission.Command.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Player targetPlayer = player;
    if (args.length != 0) {
      if (!player.hasPermission("jurassiccore.enderchest.other") && 
        !player.hasPermission("jurassiccore.enderchest.*") && 
        !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EnderChest.Permission.Other.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (args.length == 1) {
        targetPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.EnderChest.Connected.Message").replace("%player", args[0])));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
      } else if (args.length != 0) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EnderChest.Invalid.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    player.openInventory(targetPlayer.getEnderChest());
    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
    return true;
  }
}
