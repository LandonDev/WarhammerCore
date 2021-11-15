package landon.warhammercore.patchapi.patches.combattag;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatExpireTask extends BukkitRunnable {
    public static final CopyOnWriteArrayList<WeakReference<Player>> combatTagged = new CopyOnWriteArrayList<>();

    public void run() {
        for (WeakReference<Player> p : combatTagged) {
            Player pl = p.get();
            if (pl == null || !pl.isValid()) {
                combatTagged.remove(p);
                continue;
            }
            if (!CombatLog.inCombat(pl) && pl.hasMetadata("cl_combat_monitor_task")) {
                combatTagged.remove(p);
                CombatLog.unflagCombat(pl);
            }
        }
    }
}
