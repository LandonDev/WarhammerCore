package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.EXPUtil;
import landon.jurassiccore.utils.InventoryUtil;
import landon.jurassiccore.utils.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class XPBottleCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public XPBottleCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    ItemStack is;
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    Player player = (Player)sender;
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (!args[0].matches("[0-9]+")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Numerical.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (args[0].length() >= fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
      .getFileConfiguration().getInt("XPBottle.MaxLength")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Length.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    int EXP = Integer.valueOf(args[0]).intValue();
    if (EXP == 0) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Zero.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    int currentEXP = EXPUtil.getTotalExperience(player);
    if (currentEXP < EXP) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Balance.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    List<String> itemLore = new ArrayList<>();
    if (configLoad.getString("Items.XPBottle.Item.Data") == null) {
      is = new ItemStack(Material.valueOf(configLoad.getString("Items.XPBottle.Item.Material")));
    } else {
      is = new ItemStack(Material.valueOf(configLoad.getString("Items.XPBottle.Item.Material")), 1, 
          (short)configLoad.getInt("Items.XPBottle.Item.Data"));
    } 
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Items.XPBottle.Displayname")));
    for (String itemLoreLine : configLoad.getStringList("Items.XPBottle.Lore"))
      itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreLine.replace("%player", player.getName())
            .replace("%experience", NumberUtil.formatNumberByCommas(EXP)))); 
    im.setLore(itemLore);
    is.setItemMeta(im);
    if (InventoryUtil.isInventoryFull((Inventory)player.getInventory(), 0, 1, is)) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.XPBottle.Inventory.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    EXPUtil.setTotalExperience(player, currentEXP - EXP);
    player.getInventory().addItem(new ItemStack[] { is });
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.XPBottle.Extracted.Message")
          .replace("%experience", NumberUtil.formatNumberByCommas(EXP))));
    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
    return true;
  }
}
