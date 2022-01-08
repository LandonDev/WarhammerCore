package landon.jurassiccore.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.utils.NMSUtil;
import landon.jurassiccore.utils.inventory.nInventoryUtil;
import landon.jurassiccore.JurassicCore;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Icons {
  private static Icons instance;
  
  public static Icons getInstance() {
    if (instance == null)
      instance = new Icons(); 
    return instance;
  }
  
  public void open(final Player player, final Home home) {
    JurassicCore instance = JurassicCore.getInstance();
    FileConfiguration configLoadMain = instance.getFileManager()
      .getConfig(new File(instance.getDataFolder(), "config.yml")).getFileConfiguration();
    final FileConfiguration configLoadLanguage = instance.getFileManager()
      .getConfig(new File(instance.getDataFolder(), "language.yml")).getFileConfiguration();
    nInventoryUtil inv = new nInventoryUtil(player, new nInventoryUtil.ClickEventHandler() {
          public void onClick(nInventoryUtil.ClickEvent event) {
            ItemStack is = event.getItem().clone();
            is.setItemMeta(null);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                  configLoadLanguage.getString("Listeners.Inventory.Icon.Message")
                  .replace("%home", home.getName()).replace("%icon", Icons.this.getItemName(is))));
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
            home.setIcon(event.getItem());
            Homes.getInstance().open(player);
            event.setWillClose(false);
          }
        });
    inv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoadLanguage.getString("Menus.Icons.Title")));
    List<String> icons = new ArrayList<>(configLoadMain.getConfigurationSection("Icons").getKeys(false));
    for (int i = 0; i < 54 && 
      icons.size() != i; i++) {
      ItemStack is;
      if (configLoadMain.getString("Icons." + (String)icons.get(i) + ".Data") == null) {
        is = new ItemStack(
            Material.valueOf(configLoadMain.getString("Icons." + (String)icons.get(i) + ".Material")));
      } else {
        is = new ItemStack(
            Material.valueOf(configLoadMain.getString("Icons." + (String)icons.get(i) + ".Material")), 1, 
            (short)configLoadMain.getInt("Icons." + (String)icons.get(i) + ".Data"));
      } 
      inv.addItem(inv.createItem(is, 
            configLoadLanguage.getString("Menus.Icons.Items.Item.Displayname").replace("%displayname", 
              getItemName(is)), 
            configLoadLanguage.getStringList("Menus.Icons.Items.Item.Lore"), null, null, null), new int[] { i });
    } 
    if (icons.size() > 45) {
      inv.setRows(6);
    } else {
      inv.setRows((int)Math.round((icons.size() / 9) + 0.5D));
    } 
    inv.open();
  }
  
  private String getItemName(ItemStack is) {
    try {
      Object NMSItemStack = NMSUtil.getCraftClass("inventory.CraftItemStack")
        .getMethod("asNMSCopy", new Class[] { ItemStack.class }).invoke(null, new Object[] { is });
      return (String)NMSItemStack.getClass().getMethod("getName", new Class[0]).invoke(NMSItemStack, new Object[0]);
    } catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
      return WordUtils.capitalize(is.getType().name().replace("_", " ").toLowerCase());
    } 
  }
}
