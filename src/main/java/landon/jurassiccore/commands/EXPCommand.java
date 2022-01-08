package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.EXPUtil;
import landon.jurassiccore.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EXPCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public EXPCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (args.length == 0) {
        player.sendMessage(
            ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.EXP.EXP.Player.Message"))
            .replace("%exp", NumberUtil.formatNumberByCommas(EXPUtil.getTotalExperience(player)))
            .replace("%xp", NumberUtil.formatNumberByCommas(player.getLevel()))
            .replace("%required", NumberUtil.formatNumberByCommas(EXPUtil.getExpUntilNextLevel(player))));
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        return true;
      } 
      if (!player.hasPermission("jurassiccore.exp") && !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXP.Permission.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    if (args.length < 2) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.EXP.Invalid.Message")));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
    if (targetPlayer == null) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.EXP.Connected.Message"))
          .replace("%player", args[1]));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    if (args.length == 3) {
      if (!args[2].matches("[0-9]+")) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXP.Numerical.Message")));
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        } 
        return true;
      } 
      int amount = Integer.valueOf(args[2]).intValue();
      if (args[0].equalsIgnoreCase("give")) {
        EXPUtil.setTotalExperience(targetPlayer, EXPUtil.getTotalExperience(targetPlayer) + amount);
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.EXP.Given.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(amount))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXP.Given.Target.Message")
              .replace("%player", targetPlayer.getName())
              .replace("%amount", NumberUtil.formatNumberByCommas(amount))));
        return true;
      } 
      if (args[0].equalsIgnoreCase("take")) {
        EXPUtil.setTotalExperience(targetPlayer, EXPUtil.getTotalExperience(targetPlayer) - amount);
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.EXP.Taken.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(amount))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXP.Taken.Target.Message")
              .replace("%player", targetPlayer.getName())
              .replace("%amount", NumberUtil.formatNumberByCommas(amount))));
        return true;
      } 
      if (args[0].equalsIgnoreCase("set")) {
        EXPUtil.setTotalExperience(targetPlayer, amount);
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.EXP.Set.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(amount))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXP.Set.Yourself.Message")
              .replace("%player", targetPlayer.getName())
              .replace("%amount", NumberUtil.formatNumberByCommas(amount))));
        return true;
      } 
    } else if (args.length == 2 && 
      args[0].equalsIgnoreCase("show")) {
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          player.sendMessage(
              ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.EXP.EXP.Player.Message"))
              .replace("%exp", NumberUtil.formatNumberByCommas(EXPUtil.getTotalExperience(player)))
              .replace("%xp", NumberUtil.formatNumberByCommas(player.getLevel())).replace("%required", 
                NumberUtil.formatNumberByCommas(EXPUtil.getExpUntilNextLevel(player))));
          return true;
        } 
      } 
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.EXP.EXP.Target.Message"))
          .replace("%exp", NumberUtil.formatNumberByCommas(EXPUtil.getTotalExperience(targetPlayer)))
          .replace("%xp", NumberUtil.formatNumberByCommas(targetPlayer.getLevel())).replace("%required", 
            NumberUtil.formatNumberByCommas(EXPUtil.getExpUntilNextLevel(targetPlayer))));
      return true;
    } 
    sender.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.EXP.Invalid.Message")));
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    } 
    return true;
  }
}
