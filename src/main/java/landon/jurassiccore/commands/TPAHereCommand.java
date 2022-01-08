package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.timeout.TimeoutType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TPAHereCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public TPAHereCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    Player player = (Player)sender;
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Offline.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayer.getName().equals(player.getName())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    Expiry pendingExpiry = playerData.getPendingExpiry();
    if (pendingExpiry != null && pendingExpiry.getType() == ExpiryType.Teleport) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Teleporting.Player.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (playerData.getTimeout(TimeoutType.Teleport).getTime() != 0L) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Request.Player.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
    pendingExpiry = targetPlayerData.getPendingExpiry();
    if (pendingExpiry != null && pendingExpiry.getType() == ExpiryType.Teleport) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Teleporting.Player.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayerData.getTimeout(TimeoutType.Teleport).getTime() != 0L) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPAHere.Request.Target.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    playerData.setTeleport(targetPlayer.getUniqueId());
    playerData.setTeleportToPlayer(false);
    playerData.getTimeout(TimeoutType.Teleport).setTime(System.currentTimeMillis() + 30000L);
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
          .getString("Commands.TPAHere.Notify.Player.Message").replace("%player", targetPlayer.getName())));
    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
    targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.TPAHere.Notify.Receiver.Message").replace("%player", player.getName())));
    return true;
  }
}
