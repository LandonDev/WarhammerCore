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

public class SudoCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public SudoCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.sudo") && !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Sudo.Permission.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    if (args.length == 0) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Sudo.Invalid.Message")));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
    if (targetPlayer == null) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Sudo.Connected.Message").replace("%player", args[0])));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Sudo.Yourself.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
    } 
    String sudoCommand = "";
    for (int i = 1; i < args.length; i++) {
      if (i + 1 == args.length) {
        sudoCommand = String.valueOf(sudoCommand) + args[i];
      } else {
        sudoCommand = String.valueOf(sudoCommand) + args[i] + " ";
      } 
    } 
    Bukkit.getServer().dispatchCommand((CommandSender)targetPlayer, sudoCommand);
    sender.sendMessage(
        ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.Sudo.Sudo.Message").replace("%player", targetPlayer.getName()))
        .replace("%command", sudoCommand));
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
    } 
    return true;
  }
}
