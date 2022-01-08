package landon.core.patchapi.patches.chunkgc;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.massivecraft.factions.P;
import me.ifamasssxd.cosmicbosses.struct.Boss;
import me.ifamasssxd.cosmicbosses.utils.BossUtils;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkGCTask extends BukkitRunnable {
    public static boolean isEndDragonSpawned() {
        if (endDragonClass == null && bossesEnabled)
            try {
                endDragonClass = (Class)Class.forName("me.ifamasssxd.cosmicbosses.bosses.dragon.EndDragon");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        return (bossesEnabled && !BossUtils.getAllBossesFromType(endDragonClass).isEmpty());
    }

    public ChunkGCTask() {
        this.plugin = P.p;
        for (World w : Bukkit.getWorlds()) {
            try {
                (((CraftWorld)w).getHandle()).keepSpawnInMemory = false;
                int w_view_distance = ((((CraftWorld)w).getHandle()).spigotConfig != null) ? (((CraftWorld)w).getHandle()).spigotConfig.viewDistance : block_viewdistance_raw;
                int adjusted_w_view_distance = w_view_distance * 16;
                world_viewdistances.put(w.getName(), Integer.valueOf(adjusted_w_view_distance));
                ChunkGC.log.debug("Set " + w.getName() + " view_distance to " + w_view_distance + " (b" + adjusted_w_view_distance + ")!",
                        getClass());
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
        bossesEnabled = (new File("plugins/JurassicDungeons.jar")).exists();
    }

    private static boolean canUnloadChunk(org.bukkit.Chunk chunk) {
        if (chunk != null)
            for (Entity ent : chunk.getEntities()) {
                if (ent instanceof org.bukkit.entity.EnderPearl) {
                    if (ent.getTicksLived() < 600)
                        return false;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, ent::remove);
                    Bukkit.getLogger()
                            .info("[ChunkGCTask] Removing old enderpearl and unloading chunk due to living for " + ent
                                    .getTicksLived() + " at " + ent.getLocation().toString());
                } else if (bossesEnabled && ent instanceof org.bukkit.entity.LivingEntity && ((ent
                        .hasMetadata("boss") && Boss.bosses.get(ent.getUniqueId()) != null) || ent
                        .hasMetadata("cbe_boss"))) {
                    Bukkit.getLogger().info("[ChunkGCTask] Cancelling unload of chunk due to boss being inside at x" + chunk
                            .getX() + ", z" + chunk.getZ());
                    return false;
                }
            }
        return true;
    }

    public synchronized void run() {
        if (active_workers.size() > 0) {
            ChunkGC.log.notice("Skipping ChunkGCTask; " + active_workers.size() + " active_workers!",
                    getClass());
            return;
        }
        ChunkSyncUtil.syncChunks();
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, new Runnable() {
            public void run() {
                ChunkGCTask.doChunkGC();
            }
        });
    }

    public static void doChunkGC() {
        chunks_to_unload.clear();
        final AtomicInteger skipped = new AtomicInteger(0);
        for (Map.Entry<String, CopyOnWriteArrayList<Long>> data : loaded_chunks.entrySet()) {
            final String w_name = data.getKey();
            if (w_name.equals("dungeon_yijki_a") || w_name.equals("dungeon_destroyed_outpost") || w_name.equals("dungeon_planet_nul")) {
                loaded_chunks.remove(w_name);
                continue;
            }
            CopyOnWriteArrayList<Long> chunkList = data.getValue();
            World world = Bukkit.getWorld(w_name);
            if (world == null) {
                loaded_chunks.remove(w_name);
                continue;
            }
            WorldServer nmsWorld = ((CraftWorld)world).getHandle();
            for (Long cs : chunkList) {
                int cx = LongHash.msw(cs.longValue());
                int cz = LongHash.lsw(cs.longValue());
                org.bukkit.Chunk c = getChunkFromLong(cs.longValue(), nmsWorld);
                if (c == null)
                    continue;
                if (!canUnloadChunk(c)) {
                    skipped.incrementAndGet();
                    continue;
                }
                int x = cx * 16;
                int z = cz * 16;
                Location c_loc = new Location((world == null) ? (world = Bukkit.getWorld(w_name)) : world, x, 0.0D, z);
                boolean nearby = ChunkAPI.isPlayerNearby(c_loc, 0);
                if (nearby)
                    continue;
                CopyOnWriteArrayList<Long> clist = chunks_to_unload.computeIfAbsent(w_name, e -> new CopyOnWriteArrayList());
                if (clist.contains(cs))
                    continue;
                clist.add(cs);
            }
        }
        for (Map.Entry<String, CopyOnWriteArrayList<Long>> data : chunks_to_unload.entrySet()) {
            final String w_name = data.getKey();
            if (w_name.equals("dungeon_yijki_a") || w_name.equals("dungeon_destroyed_outpost") || w_name.equals("dungeon_planet_nul")) {
                chunks_to_unload.remove(w_name);
                continue;
            }
            final World w = P.p.getServer().getWorld(w_name);
            CopyOnWriteArrayList<Long> slist = data.getValue();
            int exec_count = 0;
            int chunkInterval = Math.min(Math.max(100, slist.size() / 100), 500);
            Bukkit.getLogger().info("[ChunkGC (scanning)] Marked " + slist.size() + " chunks for unload in batches of " + chunkInterval);
            int x2;
            for (x2 = 0; x2 <= slist.size(); x2 += chunkInterval) {
                int x_end = Math.min(x2 + chunkInterval, slist.size());
                final CopyOnWriteArrayList<Long> chunk_batch = new CopyOnWriteArrayList<>(slist.subList(x2, x_end));
                final int current_exec = ++exec_count;
                active_workers.add(Integer.valueOf(current_exec));
                P.p.getServer().getScheduler().runTaskLater((Plugin)P.p, new Runnable() {
                    public void run() {
                        World world = P.p.getServer().getWorld(w_name);
                        for (Long s : chunk_batch) {
                            int cx = LongHash.msw(s.longValue());
                            int cz = LongHash.lsw(s.longValue());
                            world.unloadChunk(cx, cz, true, true);
                        }
                        ChunkGC.log.debug(w_name + " worker_" + current_exec + ": Unloading " + chunk_batch.size() + " chunks...",
                                getClass());
                        ChunkGCTask.active_workers.remove(Integer.valueOf(current_exec));
                    }
                }, (exec_count * 6));
            }
            exec_count++;
            P.p.getServer().getScheduler().runTaskLater((Plugin)P.p, new Runnable() {
                public void run() {
                    ChunkGC.log.debug("(" + w_name + ") Cleared unloadQueue chunks, skipped " + skipped
                            .get() + " chunks from unloading.", getClass());
                }
            }, (exec_count * 6));
        }
    }

    private Long getLongHash(int x, int z) {
        return Long.valueOf(LongHash.toLong(x, z));
    }

    private org.bukkit.Chunk getChunkFromLong(int x, int z, World world) {
        return world.getChunkAt(x, z);
    }

    private static org.bukkit.Chunk getChunkFromLong(long val, WorldServer world) {
        int x = LongHash.msw(val);
        int z = LongHash.lsw(val);
        Chunk chunk = world.getChunkIfLoaded(x, z);
        return (chunk == null) ? null : chunk.bukkitChunk;
    }

    public static volatile ConcurrentHashMap<String, CopyOnWriteArrayList<Long>> loaded_chunks = new ConcurrentHashMap<>();

    private static volatile ConcurrentHashMap<String, CopyOnWriteArrayList<Long>> chunks_to_unload = new ConcurrentHashMap<>();

    public static volatile HashMap<String, Integer> world_viewdistances = new HashMap<>();

    public static boolean bossesEnabled = false;

    private final P plugin;

    public static final int block_viewdistance_raw = P.p.getServer().getViewDistance();

    public static final int block_viewdistance = block_viewdistance_raw * 16;

    public static HashSet<Integer> active_workers = new HashSet<>();

    private static Class<? extends Boss> endDragonClass;
}
