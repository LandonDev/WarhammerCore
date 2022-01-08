package landon.core.patchapi.patches.chunkgc;

import java.util.concurrent.ConcurrentHashMap;

import com.massivecraft.factions.P;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMoveTask extends BukkitRunnable {
    private P plugin = P.p;

    public void run() {
        for (Player pl : this.plugin.getServer().getOnlinePlayers())
            async_player_locations.put(pl.getName(), pl.getLocation());
    }

    public static volatile ConcurrentHashMap<String, Location> async_player_locations = new ConcurrentHashMap<>();
}
