package landon.core.patchapi.patches.chunkgc;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.EntityType;

import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkAPI {
    public static boolean isAsyncChunkLoaded(Chunk c) {
        CopyOnWriteArrayList<Long> lists = ChunkGCTask.loaded_chunks.get(c.getWorld().getName());
        return (lists != null && lists.contains(Long.valueOf(LongHash.toLong((((CraftChunk)c).getHandle()).locX, (((CraftChunk)c).getHandle()).locZ))));
    }

    public static boolean isAsyncChunkLoaded(World w, int cx, int cz) {
        return isAsyncChunkLoaded(w.getChunkAt(cx, cz));
    }

    public static boolean isPlayerNearby(Location loc, int view_distance_offset) {
        boolean nearby = false;
        Integer found = ChunkGCTask.world_viewdistances.get(loc.getWorld().getName());
        int lblock_viewdistance = ((found != null) ? found.intValue() : ChunkGCTask.block_viewdistance) + view_distance_offset;
        if (lblock_viewdistance < 1)
            lblock_viewdistance = 1;
        if (loc.getWorld().getName().contains("dungeon_yijki"))
            lblock_viewdistance *= 3;
        for (Location ploc : PlayerMoveTask.async_player_locations.values()) {
            if (!ploc.getWorld().equals(loc.getWorld()))
                continue;
            if (Math.abs(ploc.getX() - loc.getX()) + Math.abs(ploc.getZ() - loc.getZ()) <= lblock_viewdistance) {
                nearby = true;
                break;
            }
        }
        return nearby;
    }

    public static boolean isIntensive(EntityType et) {
        return (et != EntityType.HORSE && et != EntityType.WOLF && (et == EntityType.BLAZE || et == EntityType.CAVE_SPIDER || et == EntityType.CREEPER || et == EntityType.GIANT || et == EntityType.ENDER_DRAGON || et == EntityType.ENDERMAN || et == EntityType.GHAST || et == EntityType.GIANT || et == EntityType.MAGMA_CUBE || et == EntityType.PIG_ZOMBIE || et == EntityType.SILVERFISH || et == EntityType.SKELETON || et == EntityType.SLIME || et == EntityType.SPIDER || et == EntityType.WITCH || et == EntityType.WITHER || et == EntityType.ZOMBIE || et == EntityType.WITHER_SKULL || et == EntityType.FIREBALL || et == EntityType.SMALL_FIREBALL || et == EntityType.EXPERIENCE_ORB || et == EntityType.ARROW || et == EntityType.IRON_GOLEM || et == EntityType.SNOWMAN || et == EntityType.SQUID || et == EntityType.WITHER));
    }

    @Deprecated
    public static boolean isNonEntity(EntityType et) {
        return (et == EntityType.ARROW || et == EntityType.BOAT || et == EntityType.COMPLEX_PART || et == EntityType.DROPPED_ITEM || et == EntityType.EGG || et == EntityType.ENDER_CRYSTAL || et == EntityType.EXPERIENCE_ORB || et == EntityType.FIREWORK || et == EntityType.FISHING_HOOK || et == EntityType.ITEM_FRAME || et == EntityType.LEASH_HITCH || et.name().toLowerCase().contains("minecart") || et == EntityType.PAINTING || et == EntityType.PLAYER || et == EntityType.PRIMED_TNT || et == EntityType.SPLASH_POTION || et == EntityType.WEATHER || et == EntityType.UNKNOWN);
    }
}
