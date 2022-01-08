package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
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

public class PayCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public PayCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Economy economy = this.instance.getVaultManager().getEconomy();
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    Player player = (Player)sender;
    PlayerData playerData = playerDataManager.getPlayerData(player);
    if (!playerData.isPayEnabled()) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Pay.Disabled.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length != 2) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Connected.Message"))
          .replace("%player", args[0]));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Yourself.Message"))
          .replace("%player", args[0]));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
    if (!targetPlayerData.isPayEnabled()) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.Pay.Disabled.Target.Message").replace("%player", targetPlayer.getName())));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (!args[1].matches("[0-9]+") && !args[1].matches("([0-9]*)\\.([0-9]*)")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Pay.Numerical.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    double payMoney = Double.valueOf(args[1]).doubleValue();
    if (payMoney > fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration()
      .getDouble("Pay.MaxValue")) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Length.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (payMoney == 0.0D) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Zero.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (payMoney > economy.getBalance((OfflinePlayer)player)) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Enough.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    economy.withdrawPlayer((OfflinePlayer)player, payMoney);
    economy.depositPlayer((OfflinePlayer)targetPlayer, payMoney);
    String moneyFormat = NumberUtil.formatNumberByCommas(String.valueOf(Double.valueOf(payMoney).longValue()));
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Paid.Player.Message")
          .replace("%player", targetPlayer.getName()).replace("%money", moneyFormat)));
    targetPlayer.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Pay.Paid.Target.Message")
          .replace("%player", player.getName()).replace("%money", moneyFormat)));
    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    return true;
  }
}
