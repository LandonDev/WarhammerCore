package landon.core.patchapi.patches.fpoints.struct;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PointChangeLogs {
    public List<PointChangeLog> getLogs() {
        return this.logs;
    }

    private List<PointChangeLog> logs = Lists.newArrayList();

    public void logChange(Faction faction, Player player, String reason, int amount) {
        this.logs.add(new PointChangeLog(amount, reason, (player != null) ? player.getName() : null, System.currentTimeMillis()));
    }

    public void cleanupOldLogs() {
        Iterator<PointChangeLog> logIterator = this.logs.iterator();
        int expired = 0;
        while (logIterator.hasNext()) {
            PointChangeLog log = logIterator.next();
            if (log == null || System.currentTimeMillis() - log.getTime() > TimeUnit.DAYS.toMillis(30L)) {
                expired++;
                logIterator.remove();
            }
        }
        if (expired > 0)
            Bukkit.getLogger().info("[FactionPoints] Expired " + expired + " Faction Point Logs for being > 30d old.");
    }
}
