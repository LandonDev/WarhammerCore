package net.minelink.ctplus.task;

import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForceFieldTask extends BukkitRunnable {
    private final CombatTagPlus plugin;

    private final Map<UUID, Location> validLocations = new HashMap<>();

    private ForceFieldTask(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (!this.plugin.getSettings().denySafezone())
            return;
        for (Player player : this.plugin.getPlayerCache().getPlayers()) {
            UUID playerId = player.getUniqueId();
            if (!this.plugin.getTagManager().isTagged(playerId))
                continue;
            Location loc = player.getLocation();
            if (this.plugin.getHookManager().isPvpEnabledAt(loc)) {
                this.validLocations.put(playerId, loc);
                continue;
            }
            if (this.validLocations.containsKey(playerId))
                player.teleport(this.validLocations.get(playerId));
        }
    }

    public static void run(CombatTagPlus plugin) {
        (new ForceFieldTask(plugin)).runTaskTimer(P.p, 1L, 1L);
    }
}
