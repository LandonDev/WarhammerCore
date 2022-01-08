package landon.core.patchapi.patches.pluginviewer;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PluginCommandListener implements Listener {
    @EventHandler
    public void pluginCommand(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/pl") || e.getMessage().equalsIgnoreCase("/plugin") || e.getMessage().equalsIgnoreCase("/plugin") || e.getMessage().equalsIgnoreCase("/pls") || e.getMessage().equalsIgnoreCase("/bukkit:pl") || e.getMessage().equalsIgnoreCase("/bukkit:plugins") || e.getMessage().equalsIgnoreCase("/bukkit:plugin") || e.getMessage().equalsIgnoreCase("/bukkit:pls") || e.getMessage().equalsIgnoreCase("/bukkit:?") || e.getMessage().equalsIgnoreCase("/?")) {
            e.setCancelled(true);
            PluginListGUI.INVENTORY.open(e.getPlayer());
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        }
    }
}
