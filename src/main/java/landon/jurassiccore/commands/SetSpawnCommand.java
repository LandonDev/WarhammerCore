package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.location.LocationManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public SetSpawnCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    LocationManager locationManager = this.instance.getLocationManager();
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.setspawn") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.SetSpawn.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    locationManager.setLocation(
        fileManager.getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration(), 
        "Spawn", player.getLocation());
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.SetSpawn.Set.Message")));
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
    return true;
  }
}
