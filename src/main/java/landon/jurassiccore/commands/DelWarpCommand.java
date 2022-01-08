package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DelWarpCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public DelWarpCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    FileConfiguration configLoadLocation = fileManager
      .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.delwarp") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.DelWarp.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.DelWarp.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    for (String warpName : configLoadLocation.getConfigurationSection("Warps").getKeys(false)) {
      if (warpName.equalsIgnoreCase(args[0])) {
        configLoadLocation.set("Warps." + warpName, null);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoadLanguage.getString("Commands.DelWarp.Removed.Message").replace("%warp", warpName)));
        player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        return true;
      } 
    } 
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoadLanguage.getString("Commands.DelWarp.Exist.Message").replace("%warp", args[0])));
    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    return true;
  }
}
