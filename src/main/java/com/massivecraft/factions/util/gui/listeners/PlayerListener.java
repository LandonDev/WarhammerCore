package com.massivecraft.factions.util.gui.listeners;

import java.util.UUID;

import com.massivecraft.factions.util.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("protectedItem")) {
            long time = ((MetadataValue)event.getItem().getMetadata("protectedItem").get(0)).asLong();
            if (time == -1L || time > System.currentTimeMillis()) {
                String ownerUUID = ((MetadataValue)event.getItem().getMetadata("protectedOwner").get(0)).asString();
                if (!ownerUUID.equals(event.getPlayer().getUniqueId().toString()))
                    event.setCancelled(true);
            }
        }
    }
}
