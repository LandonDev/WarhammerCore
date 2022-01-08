package landon.jurassiccore.listeners;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Respawn implements Listener {
  private final JurassicCore instance;
  
  public Respawn(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    FileManager fileManager = this.instance.getFileManager();
    if (fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration()
      .getBoolean("Listeners.Death.Spawn.Enable")) {
      FileConfiguration configLoadLocations = fileManager
        .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
      if (configLoadLocations.getString("Spawn") != null) {
        event.setRespawnLocation(this.instance.getLocationManager().getLocation(configLoadLocations, "Spawn"));
        player.setFallDistance(0.0F);
      } 
    } 
  }
}
