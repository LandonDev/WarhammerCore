package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.utils.EXPUtil;
import landon.jurassiccore.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EXPGiveAllCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public EXPGiveAllCommand(JurassicCore instance) {
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
    if (!player.hasPermission("jurassiccore.expgiveall") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.EXPGiveAll.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    int amount = 0;
    if (args.length == 1) {
      if (args[0].matches("[0-9]+")) {
        amount = Integer.valueOf(args[0]).intValue();
      } else {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.EXPGiveAll.Numerical.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } else {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.EXPGiveAll.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    String amountFormatted = NumberUtil.formatNumberByCommas(amount);
    for (Player all : Bukkit.getOnlinePlayers()) {
      if (all.getUniqueId().equals(player.getUniqueId()))
        continue; 
      EXPUtil.setTotalExperience(all, EXPUtil.getTotalExperience(all) + amount);
      all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
            .getString("Commands.EXPGiveAll.Given.Target.Message").replace("%amount", amountFormatted))
          .replace("%player", player.getName()));
    } 
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.EXPGiveAll.Given.Player.Message").replace("%amount", amountFormatted)));
    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
    return true;
  }
}
