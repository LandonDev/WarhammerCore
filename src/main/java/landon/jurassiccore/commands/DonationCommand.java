package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DonationCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public DonationCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.donation") && !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Donation.Permission.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    if (args.length != 0) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Donation.Invalid.Message")));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    for (Player all : Bukkit.getOnlinePlayers()) {
      for (String donationLine : configLoad.getStringList("Commands.Donation.Donation.Lines"))
        all.sendMessage(ChatColor.translateAlternateColorCodes('&', donationLine)); 
    } 
    return true;
  }
}
