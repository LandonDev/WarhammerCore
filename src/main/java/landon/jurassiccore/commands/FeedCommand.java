package landon.jurassiccore.commands;

import java.io.File;
import java.util.concurrent.TimeUnit;

import landon.jurassiccore.cooldown.CooldownType;
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

public class FeedCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public FeedCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    FileConfiguration configLoad = this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.feed") && !player.hasPermission("jurassiccore.feed.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length > 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    int cooldownTime = playerData.getCooldown(CooldownType.Feed).getTime();
    if (cooldownTime != 0) {
      long minute = TimeUnit.SECONDS.toMinutes(cooldownTime) - TimeUnit.SECONDS.toHours(cooldownTime) * 60L;
      long second = TimeUnit.SECONDS.toSeconds(cooldownTime) - TimeUnit.SECONDS.toMinutes(cooldownTime) * 60L;
      if (minute == 0L) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Cooldown.Message").replace("%time", String.valueOf(second) + " " + configLoad.getString("Commands.Feed.Cooldown.Word.Second"))));
      } else {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Cooldown.Message").replace("%time", String.valueOf(minute) + " " + configLoad.getString("Commands.Feed.Cooldown.Word.Minute") + " " + second + " " + configLoad.getString("Commands.Feed.Cooldown.Word.Second"))));
      } 
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    int[] cooldownTimes = { 600, 300, 120, 60, 30, 15 };
    for (int i = 6; i > 0; i--) {
      if (player.hasPermission("jurassiccore.feed." + i)) {
        cooldownTime = cooldownTimes[i - 1];
        Player feedPlayer = player;
        if (i >= 5 && args.length == 1) {
          feedPlayer = Bukkit.getServer().getPlayer(args[0]);
          if (feedPlayer == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Offline.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
          } 
          if (feedPlayer.getName().equalsIgnoreCase(player.getName())) {
            if (configLoad.getString("Commands.Feed.Feed.Yourself.Message") != null) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Feed.Yourself.Message")));
              player.playSound(player.getLocation(), Sound.EAT, 1.0F, 1.0F);
            } 
          } else {
            if (configLoad.getString("Commands.Feed.Feed.Player.Message") != null) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Feed.Player.Message").replace("%player", feedPlayer.getName())));
              player.playSound(player.getLocation(), Sound.EAT, 1.0F, 1.0F);
            } 
            if (configLoad.getString("Commands.Feed.Feed.Receiver.Message") != null) {
              feedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Feed.Receiver.Message").replace("%player", player.getName())));
              feedPlayer.playSound(feedPlayer.getLocation(), Sound.EAT, 1.0F, 1.0F);
            } 
          } 
        } else if (configLoad.getString("Commands.Feed.Feed.Yourself.Message") != null) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Feed.Yourself.Message")));
          player.playSound(player.getLocation(), Sound.EAT, 1.0F, 1.0F);
        } 
        feedPlayer.setFoodLevel(20);
        if (!player.hasPermission("jurassiccore.feed.bypass") && !player.hasPermission("jurassiccore.feed.*") && !player.hasPermission("jurassiccore.*"))
          playerData.getCooldown(CooldownType.Feed).setTime(cooldownTime); 
        return true;
      } 
    } 
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Feed.Permission.Message")));
    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    return true;
  }
}
