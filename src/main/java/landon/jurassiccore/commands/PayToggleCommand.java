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

public class PayToggleCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public PayToggleCommand(JurassicCore instance) {
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
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    if (playerData.isPayEnabled()) {
      playerData.setPayEnabled(false);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.PayToggle.Disabled.Message")));
    } else {
      playerData.setPayEnabled(true);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.PayToggle.Enabled.Message")));
    } 
    player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
    return true;
  }
}
