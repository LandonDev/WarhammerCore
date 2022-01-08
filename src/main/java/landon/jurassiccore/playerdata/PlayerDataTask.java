package landon.jurassiccore.playerdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDataTask extends BukkitRunnable {
  private PlayerDataManager playerDataManager;
  
  public PlayerDataTask(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }
  
  public void run() {
    for (Player all : Bukkit.getOnlinePlayers()) {
      if (!this.playerDataManager.hasPlayerData(all))
        continue; 
      this.playerDataManager.savePlayerData(this.playerDataManager.getPlayerData(all));
    } 
  }
}
