package landon.core.patchapi.patches.anticrash;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

final class RebootListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (AntiCrash.shutting_down || AntiCrash.reboot_task_id != -1 || CommandReboot.restart_task_exists) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + "You cannot join the server, it is currently rebooting.");
        }
    }
}
