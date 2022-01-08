package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public IgnoreCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    Player player = (Player)sender;
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Ignore.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args[0].equalsIgnoreCase(player.getName())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Ignore.Yourself.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    if (playerData.isIgnored(args[0].toLowerCase())) {
      playerData.removeIgnore(args[0].toLowerCase());
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Ignore.Ignore.Removed.Message"))
          .replace("%player", args[0]));
      player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
    } else {
      playerData.addIgnore(args[0].toLowerCase());
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Ignore.Ignore.Added.Message"))
          .replace("%player", args[0]));
      player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
    } 
    return true;
  }
}
