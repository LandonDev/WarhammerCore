package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public WorkbenchCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.workbench") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "language.yml"))
            .getFileConfiguration().getString("Commands.Workbench.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    player.openWorkbench(null, true);
    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
    return true;
  }
}
