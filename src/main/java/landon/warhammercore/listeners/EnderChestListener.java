package landon.warhammercore.listeners;

import landon.warhammercore.util.c;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderChestListener implements Listener {
    @EventHandler
    public void onEchest(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(c.c("&c&l(!) &cEnder chests are &ndisabled&c on this server."));
        }
    }

    @EventHandler
    public void onStrength(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getItem().getDurability() == ((short)8201) || e.getItem().getDurability() == ((short)8233) || e.getItem().getDurability() == ((short)8265) || e.getItem().getDurability() == ((short)16393) || e.getItem().getDurability() == ((short)16425) || e.getItem().getDurability() == ((short)16457) || e.getItem().getDurability() == ((short)8196) || e.getItem().getDurability() == ((short)8228) || e.getItem().getDurability() == ((short)8260) || e.getItem().getDurability() == ((short)16388) || e.getItem().getDurability() == ((short)16420) || e.getItem().getDurability() == ((short)16452) || e.getItem().getDurability() == ((short)16457) || e.getItem().getDurability() == ((short)8232) || e.getItem().getDurability() == ((short)8264) || e.getItem().getDurability() == ((short)16424) || e.getItem().getDurability() == ((short)16456)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(c.c("&c&l(!) &cStrength/weakness/poison potions are &ndisabled&c on this server!"));
            }
        }
    }
}
