package landon.jurassiccore.commands;

import java.io.File;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.menus.Warps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public WarpsCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    final Player player = (Player)sender;
    FileManager fileManager = this.instance.getFileManager();
    if (fileManager.getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration()
      .getString("Warps") == null) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration()
            .getString("Commands.Warp.Warps.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    } else {
      this.instance.getPlayerDataManager().getPlayerData(player).setPage(1);
      Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
              Warps.getInstance().open(player);
            }
          });
      player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
    } 
    return true;
  }
}
