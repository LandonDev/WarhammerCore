package landon.jurassiccore.playerdata;

public class Item {
  private final org.bukkit.entity.Item item;
  
  private long time;
  
  public Item(org.bukkit.entity.Item item, long time) {
    this.item = item;
    this.time = time;
  }
  
  public org.bukkit.entity.Item getItem() {
    return this.item;
  }
  
  public long getTime() {
    return this.time;
  }
}
