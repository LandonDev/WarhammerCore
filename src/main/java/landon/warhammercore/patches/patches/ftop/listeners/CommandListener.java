package landon.warhammercore.patches.patches.ftop.listeners;

import com.massivecraft.factions.event.LandClaimEvent;
import landon.warhammercore.patches.patches.ftop.FactionsTop;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
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
