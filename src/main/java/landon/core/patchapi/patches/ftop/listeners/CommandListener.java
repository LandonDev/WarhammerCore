package landon.core.patchapi.patches.ftop.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String lower = event.getMessage().toLowerCase();
        if (event.getMessage().toLowerCase().startsWith("/f top")) {
            event.setMessage(event.getMessage().replace("/f top", "/ftop"));
        } else if (lower.startsWith("/f wealth")) {
            event.setMessage(lower.replace("/f wealth", "/ftop wealth"));
        }
    }
}
