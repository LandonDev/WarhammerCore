package landon.core.patchapi.patches.chunkgc;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.massivecraft.factions.P;
import landon.core.patchapi.patches.anticrash.AntiCrash;
import net.minecraft.server.v1_8_R3.Chunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;

public class ChunkSyncUtil {
    P plugin;

    public ChunkSyncUtil() {
        this.plugin = null;
        this.plugin = P.p;
    }

    public static void syncChunks() {
        int tracked_loaded_chunks = getTrackedLoadedChunks();
        int loaded_chunks = getLoadedChunkCount();
        int chunk_offset = Math.abs(tracked_loaded_chunks - loaded_chunks);
        if (chunk_offset < 10 && loaded_chunks > 10) {
            if (chunk_offset > 0)
                ChunkGC.log.debug("Chunk offset = " + chunk_offset + "<10, no resync.", ChunkSyncUtil.class);
            return;
        }
        ChunkGCTask.loaded_chunks.clear();
        populateLoadedChunkList();
        int dif = loaded_chunks - tracked_loaded_chunks;
        if (Math.abs(dif) > 0)
            ChunkGC.log.debug("Chunk offset (" + ((dif > 0) ? "+" : "") + dif + ")", ChunkSyncUtil.class);
    }

    private static void populateLoadedChunkList() {
        for (World w : P.p.getServer().getWorlds()) {
            if (w.getName().startsWith("dungeon_"))
                continue;
            Iterator<Chunk> it = (((CraftWorld)w).getHandle()).chunkProviderServer.a().iterator();
            CopyOnWriteArrayList<Long> cslist = new CopyOnWriteArrayList<>();
            while (it.hasNext()) {
                Chunk c = it.next();
                cslist.add(Long.valueOf(LongHash.toLong(c.locX, c.locZ)));
            }
            ChunkGCTask.loaded_chunks.put(w.getName(), cslist);
        }
    }

    private static int getLoadedChunkCount() {
        int i = 0;
        try {
            for (World w : P.p.getServer().getWorlds())
                i += (w.getLoadedChunks()).length;
        } catch (Exception err) {
            err.printStackTrace();
            if (err instanceof java.util.NoSuchElementException || err instanceof NullPointerException) {
                Bukkit.getLogger().info("[Arkkit] Forcing Arkreboot due to null pointer in chunk data!");
                AntiCrash.restartServer(60);
            }
        }
        return i;
    }

    private static int getTrackedLoadedChunks() {
        int tracked_chunks = 0;
        for (CopyOnWriteArrayList<Long> l : ChunkGCTask.loaded_chunks.values())
            tracked_chunks += l.size();
        return tracked_chunks;
    }
}
