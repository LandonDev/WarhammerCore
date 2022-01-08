package landon.jurassiccore.listeners;

import java.io.File;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.ExpiryType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Death implements Listener {
  private final JurassicCore instance;
  
  public Death(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Listeners.Death.Message.Enable"))
      event.setDeathMessage(null); 
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    if (!playerDataManager.hasPlayerData(player))
      return; 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    playerData.setLastLocation(player.getLocation());
    playerData.getExpiry(ExpiryType.Death).setTime(System.currentTimeMillis() + 600000L);
  }
}
