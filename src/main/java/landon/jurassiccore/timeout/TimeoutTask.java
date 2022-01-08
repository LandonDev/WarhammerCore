package landon.jurassiccore.timeout;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeoutTask extends BukkitRunnable {
  private JurassicCore instance;
  
  public TimeoutTask(JurassicCore instance) {
    this.instance = instance;
  }
  
  public void run() {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    for (Player all : Bukkit.getOnlinePlayers()) {
      if (!playerDataManager.hasPlayerData(all))
        continue; 
      PlayerData playerData = playerDataManager.getPlayerData(all);
      byte b;
      int i;
      TimeoutType[] arrayOfTimeoutType;
      for (i = (arrayOfTimeoutType = TimeoutType.values()).length, b = 0; b < i; ) {
        TimeoutType timeoutType = arrayOfTimeoutType[b];
        Timeout timeout = playerData.getTimeout(timeoutType);
        if (timeout.getTime() != 0L) {
          long time = System.currentTimeMillis() - timeout.getTime();
          if (time >= 0L) {
            timeout.setTime(0L);
            if (timeoutType == TimeoutType.Teleport) {
              Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getTeleport());
              if (targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Tasks.Timeout.Teleport.Receiver.Message")
                      .replace("%player", all.getName())));
                all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Tasks.Timeout.Teleport.Player.Message").replace("%player", 
                        targetPlayer.getName())));
              } 
              playerData.setTeleport(null);
            } 
          } 
        } 
        b++;
      } 
    } 
  }
}
