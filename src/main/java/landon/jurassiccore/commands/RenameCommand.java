package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public RenameCommand(JurassicCore instance) {
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
    if (!player.hasPermission("jurassiccore.rename") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Rename.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length == 0) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Rename.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    ItemStack hand = player.getItemInHand();
    if (hand == null || hand.getType() == Material.AIR) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Rename.Hand.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    String displayName = String.join(" ", (CharSequence[])args);
    ItemMeta im = hand.getItemMeta();
    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
    hand.setItemMeta(im);
    player.setItemInHand(hand);
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.Rename.Renamed.Message").replace("%displayname", displayName)));
    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
    return true;
  }
}
