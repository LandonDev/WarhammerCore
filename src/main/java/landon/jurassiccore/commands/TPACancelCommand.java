package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TPACancelCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public TPACancelCommand(JurassicCore instance) {
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
            configLoad.getString("Commands.TPACancel.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPACancel.Offline.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayer.getName().equals(player.getName())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPACancel.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);
    Timeout timeout = playerData.getTimeout(TimeoutType.Teleport);
    if (timeout.getTime() != 0L && playerData.getTeleport() != null && 
      playerData.getTeleport().equals(targetPlayer.getUniqueId())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPACancel.Cancelled.Request.Player.Message").replace("%player", 
              targetPlayer.getName())));
      player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
      targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.TPACancel.Cancelled.Request.Target.Message").replace("%player", 
              player.getName())));
      playerData.setTeleport(null);
      timeout.setTime(0L);
      return true;
    } 
    PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
    if (targetPlayerData.hasPendingExpiry()) {
      Expiry pendingExpiry = targetPlayerData.getPendingExpiry();
      if (pendingExpiry.getType() == ExpiryType.Teleport && targetPlayerData.getTeleport() != null && 
        targetPlayerData.getTeleport().equals(player.getUniqueId())) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TPACancel.Cancelled.Teleport.Player.Message").replace("%player", 
                targetPlayer.getName())));
        player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
        targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.TPACancel.Cancelled.Teleport.Target.Message").replace("%player", 
                player.getName())));
        targetPlayerData.setTeleport(null);
        targetPlayerData.getPendingExpiry().setTime(0L);
        return true;
      } 
    } 
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.TPACancel.None.Message")));
    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    return true;
  }
}
