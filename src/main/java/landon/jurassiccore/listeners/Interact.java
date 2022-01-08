package landon.jurassiccore.listeners;

import java.io.File;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.utils.EXPUtil;
import landon.jurassiccore.utils.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Interact implements Listener {
  private JurassicCore instance;
  
  public Interact(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack is = event.getItem();
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      is != null && is.getType() != Material.AIR) {
      ItemMeta im = is.getItemMeta();
      if (is.getType() == Material.valueOf(configLoad.getString("Items.Withdraw.Item.Material")) && im.hasLore() && 
        im.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Items.Withdraw.Displayname")))) {
        String money = getVariableFromItemLore("%money", configLoad.getStringList("Items.Withdraw.Lore"), 
            im.getLore());
        String signature = getVariableFromItemLore("%player", configLoad.getStringList("Items.Withdraw.Lore"), 
            im.getLore());
        if (money == null && signature == null)
          return; 
        this.instance.getVaultManager().getEconomy().depositPlayer((OfflinePlayer)player, Double.valueOf(money.replace(",", "")).doubleValue());
        if (is.getAmount() == 1) {
          player.getInventory().setItemInHand(null);
        } else {
          is.setAmount(is.getAmount() - 1);
        } 
        player.updateInventory();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Listeners.Interact.Money.Message").replace("%player", signature)
              .replace("%money", money)));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 0.5F);
      } else if (is.getType() == Material.valueOf(configLoad.getString("Items.XPBottle.Item.Material")) && 
        im.hasLore() && im.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Items.XPBottle.Displayname")))) {
        event.setCancelled(true);
        String experience = getVariableFromItemLore("%experience", 
            configLoad.getStringList("Items.XPBottle.Lore"), im.getLore()).replace(",", "");
        String signature = getVariableFromItemLore("%player", configLoad.getStringList("Items.XPBottle.Lore"), 
            im.getLore());
        if (experience == null && signature == null)
          return; 
        EXPUtil.setTotalExperience(player, EXPUtil.getTotalExperience(player) + Integer.valueOf(experience).intValue());
        if (is.getAmount() == 1) {
          player.getInventory().setItemInHand(null);
        } else {
          is.setAmount(is.getAmount() - 1);
        } 
        player.updateInventory();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Listeners.Interact.Experience.Message").replace("%player", signature)
              .replace("%experience", NumberUtil.formatNumberByCommas(experience))));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 0.5F);
      } 
    } 
  }
  
  private String getVariableFromItemLore(String variable, List<String> originalItemLore, List<String> itemLore) {
    String itemLoreLineFromFile = null, itemLoreLineFromItem = null;
    for (String itemLoreLine : originalItemLore) {
      if (itemLoreLine.contains(variable))
        itemLoreLineFromFile = ChatColor.translateAlternateColorCodes('&', itemLoreLine); 
    } 
    if (itemLoreLineFromFile == null)
      return null; 
    for (String itemLoreLine : itemLore) {
      if (itemLoreLine.contains(itemLoreLineFromFile.split(variable)[0]))
        itemLoreLineFromItem = itemLoreLine; 
    } 
    if (itemLoreLineFromItem == null)
      return null; 
    if ((itemLoreLineFromFile.split(variable)).length == 1)
      return ChatColor.stripColor(itemLoreLineFromItem.replace(itemLoreLineFromFile.split(variable)[0], "")); 
    return ChatColor.stripColor(itemLoreLineFromItem.replace(itemLoreLineFromFile.split(variable)[0], "")
        .replace(itemLoreLineFromFile.split(variable)[1], ""));
  }
}
