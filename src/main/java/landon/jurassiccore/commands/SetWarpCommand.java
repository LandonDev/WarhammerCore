package landon.jurassiccore.commands;

import java.io.File;
import java.util.Arrays;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public SetWarpCommand(JurassicCore instance) {
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
    if (!player.hasPermission("jurassiccore.setwarp") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.SetWarp.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.SetWarp.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (configLoadLocation.getString("Warps") != null)
      for (String warpName : configLoadLocation.getConfigurationSection("Warps").getKeys(false)) {
        if (warpName.equalsIgnoreCase(args[0])) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoadLanguage.getString("Commands.SetWarp.Exist.Message").replace("%warp", warpName)));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
      }  
    configLoadLocation.set("Warps." + args[0] + ".Name", "&b" + args[0]);
    configLoadLocation.set("Warps." + args[0] + ".Description", Arrays.asList(new String[] { "&7This is an example description." }));
    configLoadLocation.set("Warps." + args[0] + ".Item.Material", "GRASS");
    configLoadLocation.set("Warps." + args[0] + ".Instant", Boolean.valueOf(false));
    this.instance.getLocationManager().setLocation(configLoadLocation, "Warps." + args[0] + ".Location", 
        player.getLocation());
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoadLanguage.getString("Commands.SetWarp.Set.Message").replace("%warp", args[0])));
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
    return true;
  }
}
