package landon.warhammercore.listeners;

import landon.warhammercore.util.c;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderChestListener implements Listener {
    @EventHandler
    public void onEchest(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(c.c("&c&l(!) &cEnder chests are &ndisabled&c on this server."));
        }
    }
}
