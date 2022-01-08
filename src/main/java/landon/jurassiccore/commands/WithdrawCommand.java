package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.InventoryUtil;
import landon.jurassiccore.utils.NumberUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WithdrawCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public WithdrawCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    ItemStack is;
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    Economy economy = this.instance.getVaultManager().getEconomy();
    FileManager fileManager = this.instance.getFileManager();
    FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
      .getFileConfiguration();
    Player player = (Player)sender;
    if (args.length != 1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Invalid.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (!args[0].matches("[0-9]+") && !args[0].matches("([0-9]*)\\.([0-9]*)")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Numerical.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    double withdrawMoney = Double.valueOf(args[0]).doubleValue();
    if (withdrawMoney > fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
      .getFileConfiguration().getInt("Withdraw.MaxValue")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Length.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (withdrawMoney == 0.0D) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Zero.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    if (withdrawMoney > economy.getBalance((OfflinePlayer)player)) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Balance.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    String moneyFormat = NumberUtil.formatNumberByCommas(String.valueOf(withdrawMoney));
    List<String> itemLore = new ArrayList<>();
    if (configLoad.getString("Items.Withdraw.Item.Data") == null) {
      is = new ItemStack(Material.valueOf(configLoad.getString("Items.Withdraw.Item.Material")));
    } else {
      is = new ItemStack(Material.valueOf(configLoad.getString("Items.Withdraw.Item.Material")), 1, 
          (short)configLoad.getInt("Items.Withdraw.Item.Data"));
    } 
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Items.Withdraw.Displayname")));
    for (String itemLoreLine : configLoad.getStringList("Items.Withdraw.Lore"))
      itemLore.add(ChatColor.translateAlternateColorCodes('&', 
            itemLoreLine.replace("%player", player.getName()).replace("%money", moneyFormat))); 
    im.setLore(itemLore);
    is.setItemMeta(im);
    if (InventoryUtil.isInventoryFull((Inventory)player.getInventory(), 0, 1, is)) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Withdraw.Inventory.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    economy.withdrawPlayer((OfflinePlayer)player, withdrawMoney);
    player.getInventory().addItem(new ItemStack[] { is });
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
          configLoad.getString("Commands.Withdraw.Withdrawal.Message").replace("%money", moneyFormat)));
    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
    return true;
  }
}
