package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MSGCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public MSGCommand(JurassicCore instance) {
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
    PlayerData playerData = playerDataManager.getPlayerData(player);
    if (!playerData.isMSGEnabled()) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSG.Disabled.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length < 2) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.MSG.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.MSG.Connected.Message"))
          .replace("%player", args[0]));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.MSG.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
    if (!targetPlayerData.isMSGEnabled()) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.MSG.Disabled.Target.Message").replace("%player", targetPlayer.getName())));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (playerData.isIgnored(targetPlayer.getName().toLowerCase())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.MSG.Ignored.Yourself.Message").replace("%player", targetPlayer.getName())));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (targetPlayerData.isIgnored(player.getName().toLowerCase())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.MSG.Ignored.Target.Message").replace("%player", targetPlayer.getName())));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    playerData.setReply(targetPlayer.getName());
    targetPlayerData.setReply(player.getName());
    String privateMessage = "";
    for (int i = 1; i < args.length; i++) {
      if (i + 1 == args.length) {
        privateMessage = String.valueOf(privateMessage) + args[i];
      } else {
        privateMessage = String.valueOf(privateMessage) + args[i] + " ";
      } 
    } 
    Chat chat = this.instance.getVaultManager().getChat();
    String playerPrefix = chat.getPlayerPrefix(targetPlayer);
    if (playerPrefix == null || 
      ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', playerPrefix)).isEmpty()) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.MSG.Message.To.Empty.Message").replace("%player", targetPlayer.getName()))
          .replace("%message", privateMessage));
    } else {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSG.Message.To.Prefix.Message")
            .replace("%prefix", playerPrefix).replace("%player", targetPlayer.getName()))
          .replace("%message", privateMessage));
    } 
    playerPrefix = chat.getPlayerPrefix(player);
    if (playerPrefix == null || 
      ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', playerPrefix)).isEmpty()) {
      targetPlayer
        .sendMessage(
          
          ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSG.Message.From.Empty.Message")
            .replace("%player", player.getName()))
          .replace("%message", privateMessage));
    } else {
      targetPlayer.sendMessage(
          ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSG.Message.From.Prefix.Message")
            .replace("%prefix", playerPrefix).replace("%player", player.getName()))
          .replace("%message", privateMessage));
    } 
    return true;
  }
}
