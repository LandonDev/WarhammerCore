package landon.warhammercore.patchapi.patches.combattag;

import org.bukkit.scheduler.BukkitRunnable;

final class PlayerLogoutMonitorTask extends BukkitRunnable {
    public int logouts = 0;

    public void run() {
        this.logouts = 0;
    }
}
