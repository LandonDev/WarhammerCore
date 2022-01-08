package net.minelink.ctplus;

import org.bukkit.Location;

public final class BlockPosition {
  private final String world;
  
  private final int x;
  
  private final int y;
  
  private final int z;
  
  public BlockPosition(String world, int x, int y, int z) {
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public BlockPosition(Location loc) {
    this(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
  }
  
  public String getWorld() {
    return this.world;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getZ() {
    return this.z;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    BlockPosition that = (BlockPosition)o;
    if (this.x != that.x)
      return false; 
    if (this.y != that.y)
      return false; 
    if (this.z != that.z)
      return false; 
    return this.world.equals(that.world);
  }
  
  public int hashCode() {
    int result = this.world.hashCode();
    result = 31 * result + this.x;
    result = 31 * result + this.y;
    result = 31 * result + this.z;
    return result;
  }
}
