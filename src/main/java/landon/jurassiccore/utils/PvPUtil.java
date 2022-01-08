package landon.jurassiccore.utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PvPUtil {
  public static float getKDRRation(Player player) {
    int deaths = getDeaths(player);
    if (deaths == 0)
      deaths = 1; 
    return getKills(player) / deaths;
  }
  
  public static int getKills(Player player) {
    return player.getStatistic(Statistic.PLAYER_KILLS);
  }
  
  public static int getDeaths(Player player) {
    return player.getStatistic(Statistic.DEATHS);
  }
}
