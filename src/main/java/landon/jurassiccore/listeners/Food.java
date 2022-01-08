package landon.jurassiccore.listeners;

import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Food implements Listener {
  private JurassicCore instance;
  
  public Food(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    Player player = (Player)event.getEntity();
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    if (!playerDataManager.hasPlayerData(player))
      return; 
    if (playerDataManager.getPlayerData(player).hasGodMode())
      event.setCancelled(true); 
  }
}
