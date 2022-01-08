package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public GodCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.god") && !player.hasPermission("jurassiccore.god.*") && 
        !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.God.Permission.Command.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (args.length != 0) {
        if (!player.hasPermission("jurassiccore.god.other") && 
          !player.hasPermission("jurassiccore.god.*") && 
          !player.hasPermission("jurassiccore.*")) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.God.Permission.Other.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        if (args.length != 1) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.God.Invalid.Message")));
          return true;
        } 
      } 
    } else if (args.length != 1) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.God.Invalid.Message")));
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
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.God.Connected.Message"))
          .replace("%player", args[0]));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(targetPlayer);
    if (playerData.hasGodMode()) {
      playerData.setGodMode(false);
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.God.God.Yourself.Disabled.Message")));
          return true;
        } 
      } 
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.God.God.Target.Disabled.Message").replace("%player", targetPlayer.getName())));
    } else {
      playerData.setGodMode(true);
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.God.God.Yourself.Enabled.Message")));
          return true;
        } 
      } 
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.God.God.Target.Enabled.Message").replace("%player", targetPlayer.getName())));
    } 
    return true;
  }
}
