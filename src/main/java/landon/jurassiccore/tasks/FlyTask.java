package landon.jurassiccore.tasks;

import landon.jurassiccore.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FlyTask extends BukkitRunnable {
  private PlayerDataManager playerDataManager;
  
  public FlyTask(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }
  
  public void run() {
    for (Player all : Bukkit.getOnlinePlayers()) {
      if (all.getGameMode() == GameMode.CREATIVE)
        continue; 
      if (this.playerDataManager.hasPlayerData(all) && !this.playerDataManager.getPlayerData(all).isFlying())
        continue; 
      all.spigot().playEffect(all.getLocation().clone().subtract(0.0D, 0.5D, 0.0D), Effect.CLOUD, 0, 0, 0.0F, 0.0F, 0.0F, 
          0.0F, 1, 0);
    } 
  }
}
