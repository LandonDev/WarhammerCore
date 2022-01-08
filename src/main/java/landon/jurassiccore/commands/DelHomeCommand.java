package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.ExpiryType;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor, TabCompleter {
  private JurassicCore instance;
  
  public DelHomeCommand(JurassicCore instance) {
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
            configLoad.getString("Commands.DelHome.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    if (!playerData.hasHome(args[0])) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.DelHome.Exist.Message"))
          .replace("%home", args[0]));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (playerData.getExpiry(ExpiryType.Home).getTime() != 0L && playerData.getPendingHome() != null && 
      playerData.getPendingHome().equalsIgnoreCase(args[0])) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.DelHome.Remove.Message"))
          .replace("%home", args[0]));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    Home home = playerData.getHome(args[0]);
    playerData.removeHome(home);
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.DelHome.Removed.Message"))
        .replace("%home", home.getName()));
    player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
    List<String> matches = new ArrayList<>();
    if (!(sender instanceof Player))
      return matches; 
    if (args.length != 1)
      return matches; 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData((Player)sender);
    for (Home home : playerData.getHomes()) {
      if (home.getName().toLowerCase().startsWith(args[0].toLowerCase()))
        matches.add(home.getName()); 
    } 
    return matches;
  }
}
