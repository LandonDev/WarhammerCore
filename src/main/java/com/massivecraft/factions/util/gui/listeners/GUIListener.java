package com.massivecraft.factions.util.gui.listeners;

import com.massivecraft.factions.util.gui.CustomGUI;
import com.massivecraft.factions.util.gui.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class GUIListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        CustomGUI active = CustomGUI.getActiveGUI(event.getWhoClicked().getUniqueId());
        if (active != null) {
            event.setCancelled(true);
            if (event.getRawSlot() < event.getInventory().getSize()) {
                int slot = event.getSlot();
                InventoryItem item = (InventoryItem)active.getInventoryItems().get(Integer.valueOf(slot));
                if (item != null)
                    item.handleClick(event);
                return;
            }
            active.onUnknownItemClick(event);
        } else if (CustomGUI.allGUINames.contains(event.getInventory().getName())) {
            event.setCancelled(true);
            Bukkit.getLogger().info("Cancelling Inventory CLICKED: " + event.getInventory().getName() + " DUE TO IT NOT BEING TRACKED FOR " + event.getWhoClicked().getName() + ", MASSIVE LAG??");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CustomGUI active = CustomGUI.getActiveGUI(event.getPlayer().getUniqueId());
        if (active != null) {
            active.onInventoryClose();
            CustomGUI.removeGUI(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CustomGUI active = CustomGUI.getActiveGUI(event.getPlayer().getUniqueId());
        if (active != null)
            active.close();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (CustomGUI active : CustomGUI.activeGUIs.values()) {
            if (active.getOwningPluginName().equals(event.getPlugin().getName())) {
                Bukkit.getLogger().info("Closing GUI due to " + event.getPlugin().getName() + " disabling!");
                try {
                    active.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

