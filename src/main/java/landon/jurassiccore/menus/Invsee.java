package landon.jurassiccore.menus;

import java.io.File;

import com.massivecraft.factions.P;
import landon.jurassiccore.utils.inventory.nInventoryUtil;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Invsee {
  private static Invsee instance;
  
  public static Invsee getInstance() {
    if (instance == null)
      instance = new Invsee(); 
    return instance;
  }
  
  public void open(final Player player, final Player targetPlayer) {
    final JurassicCore instance = JurassicCore.getInstance();
    Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
          public void run() {
            final FileConfiguration configLoad = instance.getFileManager()
              .getConfig(new File(instance.getDataFolder(), "language.yml")).getFileConfiguration();
            final nInventoryUtil inv = new nInventoryUtil(player, new nInventoryUtil.ClickEventHandler() {
                  public void onClick(nInventoryUtil.ClickEvent event) {
                    ItemStack is = event.getItem();
                    if (is.getType() == Material.STAINED_GLASS_PANE && is.hasItemMeta() && 
                      is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', 
                          configLoad.getString("Menus.Invsee.Items.Glass.Displayname")))) {
                      player.playSound(player.getLocation(), Sound.GLASS, 1.0F, 1.0F);
                    } else {
                      player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
                    } 
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                  }
                });
            for (int i = 0; i < targetPlayer.getInventory().getSize(); i++) {
              ItemStack is = targetPlayer.getInventory().getItem(i);
              if (is != null)
                inv.addItemStack(is, new int[] { i }); 
            } 
            if (targetPlayer.getInventory().getHelmet() != null)
              inv.addItemStack(targetPlayer.getInventory().getHelmet(), new int[] { 38 }); 
            if (targetPlayer.getInventory().getChestplate() != null)
              inv.addItemStack(targetPlayer.getInventory().getChestplate(), new int[] { 39 }); 
            if (targetPlayer.getInventory().getLeggings() != null)
              inv.addItemStack(targetPlayer.getInventory().getLeggings(), new int[] { 41 }); 
            if (targetPlayer.getInventory().getBoots() != null)
              inv.addItemStack(targetPlayer.getInventory().getBoots(), new int[] { 42 }); 
            inv.addItem(inv.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15), 
                  ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Menus.Invsee.Items.Glass.Displayname")), 
                  null, null, null, null), new int[] { 36, 37, 40, 43, 44 });
            inv.setTitle(ChatColor.translateAlternateColorCodes('&', 
                  configLoad.getString("Menus.Invsee.Title").replace("%player", targetPlayer.getName())));
            inv.setRows(5);
            Bukkit.getServer().getScheduler().runTask(P.p, new Runnable() {
                  public void run() {
                    inv.open();
                  }
                });
          }
        });
  }
}
