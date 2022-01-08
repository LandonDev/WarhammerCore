package landon.jurassiccore.listeners;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class Item implements Listener {
  private final JurassicCore instance;
  
  public Item(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event) {
    if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Listeners.Item.Invulnerable.Enable"))
      return; 
    org.bukkit.entity.Item item = event.getEntity();
    try {
      Object Entity = item.getClass().getMethod("getHandle", new Class[0]).invoke(item, new Object[0]);
      Field invulnerableField = Entity.getClass().getSuperclass().getDeclaredField("invulnerable");
      invulnerableField.setAccessible(true);
      invulnerableField.set(Entity, Boolean.valueOf(true));
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException|NoSuchFieldException e) {
      e.printStackTrace();
    } 
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
    ItemStack is = event.getItem();
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
    for (String itemName : configLoad.getConfigurationSection("Listeners.Item.Consume.Blacklist").getKeys(false)) {
      if (configLoad.getString("Listeners.Item.Consume.Blacklist." + itemName + ".Data") == null) {
        if (is.getType() == Material.getMaterial(
            configLoad.getString("Listeners.Item.Consume.Blacklist." + itemName + ".Material"))) {
          event.setCancelled(true);
          break;
        } 
        continue;
      } 
      if (is.getType() == 
        Material.getMaterial(configLoad.getString("Listeners.Item.Consume.Blacklist." + itemName + ".Material")))
        if (is.getDurability() == configLoad
          .getInt("Listeners.Item.Consume.Blacklist." + itemName + ".Data")) {
          event.setCancelled(true);
          break;
        }  
    } 
  }
  
  @EventHandler
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    org.bukkit.entity.Item item = event.getItem();
    if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("ItemProtect.Enable"))
      return; 
    if (!item.hasMetadata("ItemProtect"))
      return; 
    List<MetadataValue> itemMetadataValues = item.getMetadata("ItemProtect");
    if (itemMetadataValues == null || itemMetadataValues.size() == 0)
      return; 
    String killerName = ((MetadataValue)itemMetadataValues.get(0)).value().toString();
    if (killerName == null)
      return; 
    if (!killerName.equalsIgnoreCase(player.getName()))
      event.setCancelled(true); 
  }
}
