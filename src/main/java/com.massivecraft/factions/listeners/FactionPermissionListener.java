/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionPermissionListener
        implements Listener {
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        PermissionManager.get().getPermissionMap().values().forEach(perm -> perm.cleanupPermissions(event.getFaction()));
    }

    @EventHandler
    public void onPlayerUnclaim(LandUnclaimEvent event) {
        FactionPermissions permissions = PermissionManager.get().getPermissions(event.getFaction());
        if (permissions != null) {
            permissions.cleanupPermissions(event.getLocation());
        }
    }

    @EventHandler
    public void onPlayerLeaveFaction(FPlayerLeaveEvent event) {
        PermissionManager.get().getPermissionMap().values().forEach(perm -> perm.cleanupPermissions(event.getFPlayer().getCachedUUID()));
    }
}

