package landon.jurassiccore.cooldown;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownTask extends BukkitRunnable {
  private PlayerDataManager playerDataManager;
  
  public CooldownTask(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }
  
  public void run() {
    for (Player all : Bukkit.getOnlinePlayers()) {
      if (!this.playerDataManager.hasPlayerData(all))
        continue; 
      PlayerData playerData = this.playerDataManager.getPlayerData(all);
      byte b;
      int i;
      CooldownType[] arrayOfCooldownType;
      for (i = (arrayOfCooldownType = CooldownType.values()).length, b = 0; b < i; ) {
        CooldownType cooldownType = arrayOfCooldownType[b];
        Cooldown cooldown = playerData.getCooldown(cooldownType);
        if (cooldown.getTime() > 0)
          cooldown.setTime(cooldown.getTime() - 1); 
        b++;
      } 
    } 
  }
}
