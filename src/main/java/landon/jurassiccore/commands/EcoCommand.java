package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.NumberUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public EcoCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    Economy economy = this.instance.getVaultManager().getEconomy();
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.eco") && !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Eco.Permission.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    if (args.length < 2) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Eco.Invalid.Message")));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
    if (targetPlayer == null) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Eco.Connected.Message"))
          .replace("%player", args[1]));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    if (args.length == 3) {
      if (!args[2].matches("[0-9]+") && !args[2].matches("([0-9]*)\\.([0-9]*)")) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Eco.Numerical.Message")));
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        } 
        return true;
      } 
      double amount = Double.valueOf(args[2]).doubleValue();
      if (args[0].equalsIgnoreCase("give")) {
        economy.depositPlayer((OfflinePlayer)targetPlayer, amount);
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.Eco.Given.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(
                      String.valueOf(Double.valueOf(amount).longValue())))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
              .getString("Commands.Eco.Given.Target.Message").replace("%player", targetPlayer.getName())
              .replace("%amount", 
                NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(amount).longValue())))));
        return true;
      } 
      if (args[0].equalsIgnoreCase("take")) {
        economy.withdrawPlayer((OfflinePlayer)targetPlayer, amount);
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.Eco.Taken.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(
                      String.valueOf(Double.valueOf(amount).longValue())))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
              .getString("Commands.Eco.Taken.Target.Message").replace("%player", targetPlayer.getName())
              .replace("%amount", 
                NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(amount).longValue())))));
        return true;
      } 
      if (args[0].equalsIgnoreCase("set")) {
        double balance = economy.getBalance((OfflinePlayer)targetPlayer);
        if (balance >= 0.0D) {
          economy.withdrawPlayer((OfflinePlayer)targetPlayer, balance);
          economy.depositPlayer((OfflinePlayer)targetPlayer, amount);
        } else {
          economy.depositPlayer((OfflinePlayer)targetPlayer, Math.abs(balance) + amount);
        } 
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
          if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Commands.Eco.Set.Yourself.Message").replace("%amount", 
                    NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(amount).longValue())))));
            return true;
          } 
        } 
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
              .getString("Commands.Eco.Set.Yourself.Message").replace("%player", targetPlayer.getName())
              .replace("%amount", 
                NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(amount).longValue())))));
        return true;
      } 
    } else if (args.length == 2 && 
      args[0].equalsIgnoreCase("reset")) {
      double amount = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
        .getFileConfiguration().getDouble("Balance.Default");
      double balance = economy.getBalance((OfflinePlayer)targetPlayer);
      if (balance >= 0.0D) {
        economy.withdrawPlayer((OfflinePlayer)targetPlayer, balance);
        economy.depositPlayer((OfflinePlayer)targetPlayer, amount);
      } else {
        economy.depositPlayer((OfflinePlayer)targetPlayer, Math.abs(balance) + amount);
      } 
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Eco.Reset.Yourself.Message").replace("%amount", 
                  NumberUtil.formatNumberByCommas(
                    String.valueOf(Double.valueOf(amount).longValue())))));
          return true;
        } 
      } 
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.Eco.Reset.Set.Message").replace("%player", targetPlayer.getName())
            .replace("%amount", 
              NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(amount).longValue())))));
      return true;
    } 
    sender.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Eco.Invalid.Message")));
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    } 
    return true;
  }
}
