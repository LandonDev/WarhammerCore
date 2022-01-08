package landon.jurassiccore.tasks;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearLagTask extends BukkitRunnable {
  private final JurassicCore instance;
  
  public ClearLagTask(JurassicCore instance) {
    this.instance = instance;
  }
  
  public void run() {
    if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("ClearLag.Enable"))
      return; 
    for (World world : Bukkit.getWorlds()) {
      List<LivingEntity> livingEntities = world.getLivingEntities();
      for (Iterator<LivingEntity> it = livingEntities.iterator(); it.hasNext(); ) {
        final LivingEntity livingEntity = it.next();
        if (excludeEntity(livingEntity))
          continue; 
        if (livingEntity instanceof Item && excludeItem((Item)livingEntity))
          continue; 
        Bukkit.getServer().getScheduler().runTask(P.p, new Runnable() {
              public void run() {
                System.out.println("Test");
                livingEntity.remove();
              }
            });
      } 
    } 
  }
  
  public boolean excludeEntity(LivingEntity livingEntity) {
    Iterator<String> iterator = this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getStringList("ClearLag.Exclude.Entities").iterator();
    while (iterator.hasNext()) {
      String entityType = iterator.next();
      if (livingEntity.getType() == EntityType.valueOf(entityType))
        return true; 
    } 
    return false;
  }
  
  public boolean excludeItem(Item item) {
    if (item == null || item.getItemStack() == null)
      return false; 
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
    for (String itemName : configLoad.getStringList("ClearLag.Exclude.Items")) {
      if (configLoad.getString("ClearLag.Exclude.Items." + itemName + ".Data") == null) {
        ItemStack itemStack = new ItemStack(
            Material.getMaterial(configLoad.getString("ClearLag.Exclude.Items." + itemName + ".Material")));
        if (itemStack.getType() == item.getItemStack().getType())
          return true; 
        continue;
      } 
      ItemStack is = new ItemStack(
          Material.getMaterial(configLoad.getString("ClearLag.Exclude.Items." + itemName + ".Material")), 
          1, (short)configLoad.getInt("ClearLag.Exclude.Items." + itemName + ".Data"));
      if (is.getType() == item.getItemStack().getType() && 
        is.getDurability() == item.getItemStack().getDurability())
        return true; 
    } 
    return false;
  }
}
