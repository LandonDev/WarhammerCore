package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FixCommand implements CommandExecutor, TabCompleter {
  private JurassicCore instance;
  
  public FixCommand(JurassicCore instance) {
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
    if (!player.hasPermission("jurassiccore.fix") && !player.hasPermission("jurassiccore.fix.*") && 
      !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Fix.Permission.Command.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    FixType fixType = null;
    if (s.equalsIgnoreCase("fixall")) {
      fixType = FixType.All;
    } else if (args.length == 0) {
      fixType = FixType.Hand;
    } else if (args.length == 1) {
      if (args[0].equalsIgnoreCase("hand")) {
        fixType = FixType.Hand;
      } else if (args[0].equalsIgnoreCase("all")) {
        fixType = FixType.All;
      } 
    } 
    if (fixType == null) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Fix.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (!player.hasPermission("jurassiccore.fix." + fixType.name().toLowerCase()) && 
      !player.hasPermission("jurassiccore.fix.*") && !player.hasPermission("jurassiccore.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Fix.Permission." + fixType.name() + ".Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (fixType == FixType.Hand) {
      ItemStack hand = player.getItemInHand();
      if (hand == null || hand.getType() == Material.AIR) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Fix.Hand.Holding.Message")));
        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
        return true;
      } 
      if (!hand.getType().name().contains("SWORD") && hand.getType() != Material.BOW && 
        !hand.getType().name().contains("HELMET") && !hand.getType().name().contains("CHESTPLATE") && 
        !hand.getType().name().contains("LEGGINGS") && !hand.getType().name().contains("BOOTS") && 
        !hand.getType().name().contains("SPADE") && !hand.getType().name().contains("PICKAXE") && 
        !hand.getType().name().contains("AXE") && !hand.getType().name().contains("HOE") && 
        hand.getType() != Material.FISHING_ROD) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Fix.Hand.Item.Message")));
        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
        return true;
      } 
      hand.setDurability((short)0);
    } else {
      if (player.getInventory().getHelmet() != null && 
        player.getInventory().getHelmet().getType() != Material.AIR)
        player.getInventory().getHelmet().setDurability((short)0); 
      if (player.getInventory().getChestplate() != null && 
        player.getInventory().getChestplate().getType() != Material.AIR)
        player.getInventory().getChestplate().setDurability((short)0); 
      if (player.getInventory().getLeggings() != null && 
        player.getInventory().getLeggings().getType() != Material.AIR)
        player.getInventory().getLeggings().setDurability((short)0); 
      if (player.getInventory().getBoots() != null && 
        player.getInventory().getBoots().getType() != Material.AIR)
        player.getInventory().getBoots().setDurability((short)0); 
      for (int i = 0; i < player.getInventory().getSize(); i++) {
        ItemStack is = player.getInventory().getItem(i);
        if (is != null && is.getType() != Material.AIR)
          if (is.getType().name().contains("SWORD") || is.getType() == Material.BOW || 
            is.getType().name().contains("HELMET") || is.getType().name().contains("CHESTPLATE") || 
            is.getType().name().contains("LEGGINGS") || is.getType().name().contains("BOOTS") || 
            is.getType().name().contains("SPADE") || is.getType().name().contains("PICKAXE") || 
            is.getType().name().contains("AXE") || is.getType().name().contains("HOE") || 
            is.getType() == Material.FISHING_ROD)
            is.setDurability((short)0);  
      } 
    } 
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.Fix.Fixed." + fixType.name() + ".Message")));
    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
    return true;
  }
  
  public enum FixType {
    Hand, All;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
    List<String> matches = new ArrayList<>();
    if (!(sender instanceof Player))
      return matches; 
    if (s.equalsIgnoreCase("fixall"))
      return matches; 
    if (args.length != 1)
      return matches; 
    byte b;
    int i;
    FixType[] arrayOfFixType;
    for (i = (arrayOfFixType = FixType.values()).length, b = 0; b < i; ) {
      FixType fixType = arrayOfFixType[b];
      if (fixType.name().toLowerCase().startsWith(args[0].toLowerCase()))
        matches.add(fixType.name()); 
      b++;
    } 
    return matches;
  }
}
