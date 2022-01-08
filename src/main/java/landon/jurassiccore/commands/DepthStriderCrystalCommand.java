package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.NumberUtil;
import org.bukkit.Bukkit;
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

public class DepthStriderCrystalCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public DepthStriderCrystalCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    ItemStack is;
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoadMain = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
      .getFileConfiguration();
    FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.DepthStriderCrystal.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args.length == 0 || !args[0].equalsIgnoreCase("give") || args.length > 3) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoadLanguage.getString("Commands.DepthStriderCrystal.Invalid.Message")));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
    if (targetPlayer == null) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
            .getString("Commands.DepthStriderCrystal.Connected.Message").replace("%player", args[1])));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    int itemAmount = 1;
    if (args.length == 3)
      if (args[2].matches("[0-9]+")) {
        itemAmount = Integer.valueOf(args[2]).intValue();
      } else {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoadLanguage.getString("Commands.DepthStriderCrystal.Numerical.Message")));
        if (sender instanceof Player) {
          Player player = (Player)sender;
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        } 
        return true;
      }  
    if (configLoadMain.getString("DepthStriderCrystal.Item.Data") == null) {
      is = new ItemStack(Material.getMaterial(configLoadMain.getString("DepthStriderCrystal.Item.Material")), 
          itemAmount);
    } else {
      is = new ItemStack(Material.getMaterial(configLoadMain.getString("DepthStriderCrystal.Item.Material")), 
          itemAmount, (short)configLoadMain.getInt("DepthStriderCrystal.Item.Data"));
    } 
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
          configLoadMain.getString("DepthStriderCrystal.Displayname")));
    List<String> itemLore = new ArrayList<>();
    for (String loreLine : configLoadMain.getStringList("DepthStriderCrystal.Lore"))
      itemLore.add(ChatColor.translateAlternateColorCodes('&', loreLine)); 
    im.setLore(itemLore);
    is.setItemMeta(im);
    targetPlayer.getInventory().addItem(new ItemStack[] { is });
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoadLanguage.getString("Commands.DepthStriderCrystal.Given.Yourself.Message")
              .replace("%amount", NumberUtil.formatNumberByCommas(itemAmount))));
        return true;
      } 
    } 
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoadLanguage.getString("Commands.DepthStriderCrystal.Given.Target.Message")
          .replace("%amount", NumberUtil.formatNumberByCommas(itemAmount))
          .replace("%player", targetPlayer.getName())));
    return true;
  }
}
