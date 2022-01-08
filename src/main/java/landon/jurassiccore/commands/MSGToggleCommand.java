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

public class MSGToggleCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public MSGToggleCommand(JurassicCore instance) {
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
    if (playerData.isMSGEnabled()) {
      playerData.setMSGEnabled(false);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSGToggle.Disabled.Message")));
    } else {
      playerData.setMSGEnabled(true);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.MSGToggle.Enabled.Message")));
    } 
    player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
    return true;
  }
}
