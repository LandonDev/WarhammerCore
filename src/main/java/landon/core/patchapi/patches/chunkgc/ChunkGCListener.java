package landon.core.patchapi.patches.chunkgc;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.concurrent.CopyOnWriteArrayList;

final class ChunkGCListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk c = e.getChunk();
        CopyOnWriteArrayList<Long> chunks = ChunkGCTask.loaded_chunks.get(e.getWorld().getName());
        Long val = Long.valueOf(LongHash.toLong(c.getX(), c.getZ()));
        if (chunks != null && !chunks.contains(val))
            chunks.add(val);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnloaded(ChunkUnloadEvent e) {
        CopyOnWriteArrayList<Long> chunks = ChunkGCTask.loaded_chunks.get(e.getWorld().getName());
        if (chunks != null)
            chunks.remove(Long.valueOf(LongHash.toLong(e.getChunk().getX(), e.getChunk().getZ())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerMoveTask.async_player_locations.remove(e.getPlayer().getName());
    }
}
