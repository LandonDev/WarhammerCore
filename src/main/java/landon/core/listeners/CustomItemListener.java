package landon.core.listeners;

import landon.core.WarhammerCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomItemListener implements Listener {
    @EventHandler
    public void rightClick(PlayerInteractEvent e) {
        if(e.getItem() != null && WarhammerCore.get().getItemManager().isOasisItem(e.getItem())) {
            WarhammerCore.get().getItemManager().getOasisItem(e.getItem()).onClick(e);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getCursor() != null && WarhammerCore.get().getItemManager().isOasisItem(e.getCursor())) {
            WarhammerCore.get().getItemManager().getOasisItem(e.getCursor()).onInventoryClick(e, (Player) e.getWhoClicked());
            e.setCancelled(true);
        }
    }
}
