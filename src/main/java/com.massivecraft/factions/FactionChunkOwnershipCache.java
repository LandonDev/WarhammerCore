/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.event.world.ChunkUnloadEvent
 */
package com.massivecraft.factions;

import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashMap;

public class FactionChunkOwnershipCache
        implements Listener {
    private static HashMap<Chunk, Faction> cachedChunkOwnership = new HashMap<>();

    public static Faction getCachedFactionChunkOwnership(Location l) {
        return cachedChunkOwnership.get(l.getChunk());
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        Chunk c;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && !cachedChunkOwnership.containsKey((c = e.getLocation().getChunk()))) {
            cachedChunkOwnership.put(c, Board.getFactionAt(new FLocation(e.getLocation())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e) {
        cachedChunkOwnership.remove(e.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLandClaim(LandClaimEvent e) {
        cachedChunkOwnership.remove(e.getLocation().getWorld().getChunkAt((int) e.getLocation().getX(), (int) e.getLocation().getZ()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLandUnclaim(LandUnclaimEvent e) {
        cachedChunkOwnership.remove(e.getLocation().getWorld().getChunkAt((int) e.getLocation().getX(), (int) e.getLocation().getZ()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLandUnclaimAll(LandUnclaimAllEvent e) {
        for (FLocation fl : e.getFaction().getClaimOwnership().keySet()) {
            cachedChunkOwnership.remove(fl.getWorld().getChunkAt((int) fl.getX(), (int) fl.getZ()));
        }
    }
}

