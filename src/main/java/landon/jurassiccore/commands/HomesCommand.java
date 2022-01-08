package landon.jurassiccore.commands;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.menus.Homes;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public HomesCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    Player player = (Player)sender;
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    if (playerData.getHomes().size() == 0) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "language.yml"))
            .getFileConfiguration().getString("Commands.Home.Homes.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    } else {
      playerData.setPage(1);
      Homes.getInstance().open(player);
      player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
    } 
    return true;
  }
}
