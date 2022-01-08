package landon.jurassiccore.listeners;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class Craft implements Listener {
  private final JurassicCore instance;
  
  public Craft(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {
    ItemStack is = event.getRecipe().getResult();
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
    for (String itemName : configLoad.getConfigurationSection("Listeners.Craft.Blacklist").getKeys(false)) {
      if (configLoad.getString("Listeners.Craft.Blacklist." + itemName + ".Data") == null) {
        if (is.getType() == 
          Material.getMaterial(configLoad.getString("Listeners.Craft.Blacklist." + itemName + ".Material"))) {
          event.getInventory().setResult(new ItemStack(Material.AIR));
          break;
        } 
        continue;
      } 
      if (is.getType() == 
        Material.getMaterial(configLoad.getString("Listeners.Craft.Blacklist." + itemName + ".Material")))
        if (is.getDurability() == configLoad.getInt("Listeners.Craft.Blacklist." + itemName + ".Data")) {
          event.getInventory().setResult(new ItemStack(Material.AIR));
          break;
        }  
    } 
  }
}
