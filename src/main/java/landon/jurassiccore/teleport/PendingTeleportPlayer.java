package landon.jurassiccore.teleport;

import org.bukkit.entity.Player;

public class PendingTeleportPlayer {
  private final Player player;
  
  private long time;
  
  public PendingTeleportPlayer(Player player, long time) {
    this.player = player;
    this.time = time;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public long getTime() {
    return this.time;
  }
}
