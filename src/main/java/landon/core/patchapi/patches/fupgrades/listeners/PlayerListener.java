package landon.core.patchapi.patches.fupgrades.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPreProcess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/f upgrade") || event.getMessage().startsWith("/f upgrades"))
            event.setMessage(event.getMessage().replace("/f upgrades", "/fupgrade")
                    .replace("/f upgrade", "/fupgrade"));
    }
}
