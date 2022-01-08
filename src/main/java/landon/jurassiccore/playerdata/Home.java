package landon.jurassiccore.playerdata;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Home {
  private String name;
  
  private Location location;
  
  private ItemStack icon;
  
  public Home(String name, Location location, ItemStack icon) {
    this.name = name;
    this.location = location;
    this.icon = icon;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Location getLocation() {
    return this.location;
  }
  
  public ItemStack getIcon() {
    return this.icon;
  }
  
  public void setIcon(ItemStack icon) {
    this.icon = icon;
  }
}
