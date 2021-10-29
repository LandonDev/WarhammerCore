/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.listeners.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener
        implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals("Permissions") || event.getInventory().getName().equals("/f access: Select Type") || event.getInventory().getName().startsWith("/f access at ") || event.getInventory().getName().startsWith("/f access: ") || event.getInventory().getName().equals("Faction Logs")) {
            event.setCancelled(true);
        }
        Player player = (Player) event.getWhoClicked();
        GUIMenu menu = GUIMenu.getMenus().get(player.getUniqueId());
        if (menu != null) {
            event.setCancelled(true);
            if (!menu.getName().equals(event.getInventory().getName())) {
                player.closeInventory();
                return;
            }
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            if (event.getRawSlot() >= event.getInventory().getSize()) {
                return;
            }
            ClickableItemStack found = menu.getMenuItems().get(event.getRawSlot());
            if (found != null && found.getType() == item.getType() && found.getDurability() == item.getDurability()) {
                if (found.getItemCallback() == null) {
                    return;
                }
                found.getItemCallback().accept(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GUIMenu menu = GUIMenu.getMenus().remove(event.getPlayer().getUniqueId());
        if (menu != null && menu.getCloseCallback() != null) {
            menu.getCloseCallback().accept(event);
        }
    }

    @EventHandler
    public void onPLayerLeave(PlayerQuitEvent event) {
        if(event.getPlayer() == null) return;

        GUIMenu menu = GUIMenu.getMenus().remove(event.getPlayer().getUniqueId());
        if (menu != null && menu.getCloseCallback() != null) {
            menu.getCloseCallback().accept(new InventoryCloseEvent(event.getPlayer().getOpenInventory()));
        }
    }
}

