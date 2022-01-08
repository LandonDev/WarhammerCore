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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TPCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public TPCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.tp") && !player.hasPermission("jurassiccore.tp.*") && 
        !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TP.Permission.Command.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (args.length == 0 || args.length > 2) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TP.Invalid.Player.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (args.length == 1) {
        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
          player.sendMessage(
              ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TP.Connected.Message"))
              .replace("%player", args[0]));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.TP.Yourself.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        player.teleport(targetPlayer.getLocation());
        player.setFallDistance(0.0F);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TP.Teleported.Player.Message").replace("%player", 
                targetPlayer.getName())));
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      } else if (args.length == 2) {
        if (!player.hasPermission("jurassiccore.tp.other") && !player.hasPermission("jurassiccore.tp.*") && 
          !player.hasPermission("jurassiccore.*")) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.TP.Permission.Other.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        Player targetPlayer1 = Bukkit.getServer().getPlayer(args[0]);
        if (targetPlayer1 == null) {
          player.sendMessage(
              ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TP.Connected.Message"))
              .replace("%player", args[0]));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        if (targetPlayer1.getUniqueId().equals(player.getUniqueId())) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.TP.Yourself.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        Player targetPlayer2 = Bukkit.getServer().getPlayer(args[1]);
        if (targetPlayer2 == null) {
          player.sendMessage(
              ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TP.Connected.Message"))
              .replace("%player", args[1]));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        targetPlayer1.teleport((Entity)targetPlayer2);
        targetPlayer1.setFallDistance(0.0F);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TP.Teleported.Target.Message")
              .replace("%player1", targetPlayer1.getName())
              .replace("%player2", targetPlayer2.getName())));
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      } 
    } else {
      if (args.length != 2) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TP.Invalid.Console.Message")));
        return true;
      } 
      Player targetPlayer1 = Bukkit.getServer().getPlayer(args[0]);
      if (targetPlayer1 == null) {
        sender.sendMessage(
            ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TP.Connected.Message"))
            .replace("%player", args[0]));
        return true;
      } 
      Player targetPlayer2 = Bukkit.getServer().getPlayer(args[1]);
      if (targetPlayer2 == null) {
        sender.sendMessage(
            ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TP.Connected.Message"))
            .replace("%player", args[1]));
        return true;
      } 
      targetPlayer1.teleport((Entity)targetPlayer2);
      targetPlayer1.setFallDistance(0.0F);
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TP.Teleported.Target.Message")
            .replace("%player1", targetPlayer1.getName())
            .replace("%player2", targetPlayer2.getName())));
    } 
    return true;
  }
}
