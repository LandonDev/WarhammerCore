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

public class FlyCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public FlyCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.fly") && !player.hasPermission("jurassiccore.fly.*") && 
        !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Fly.Permission.Command.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (args.length != 0) {
        if (!player.hasPermission("jurassiccore.fly.other") && 
          !player.hasPermission("jurassiccore.fly.*") && 
          !player.hasPermission("jurassiccore.*")) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Fly.Permission.Other.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        if (args.length != 1) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Fly.Invalid.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
      } 
    } else if (args.length != 1) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Fly.Invalid.Message")));
      return true;
    } 
    Player targetPlayer = null;
    if (args.length == 1) {
      targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    } else if (sender instanceof Player) {
      targetPlayer = (Player)sender;
    } 
    if (targetPlayer == null) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Fly.Connected.Message"))
          .replace("%player", args[0]));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    if (targetPlayer.getAllowFlight()) {
      targetPlayer.setFlying(false);
      targetPlayer.setAllowFlight(false);
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Fly.Flight.Yourself.Disabled.Message")));
          return true;
        } 
      } 
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Fly.Flight.Target.Disabled.Message").replace("%player", 
              targetPlayer.getName())));
    } else {
      targetPlayer.setAllowFlight(true);
      targetPlayer.setFlying(true);
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Fly.Flight.Yourself.Enabled.Message")));
          return true;
        } 
      } 
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Fly.Flight.Target.Enabled.Message").replace("%player", 
              targetPlayer.getName())));
    } 
    return true;
  }
}
