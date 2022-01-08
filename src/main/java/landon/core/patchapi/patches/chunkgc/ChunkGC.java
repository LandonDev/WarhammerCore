package landon.core.patchapi.patches.chunkgc;

import landon.core.patchapi.UHCFPatch;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.Plugin;

public class ChunkGC extends UHCFPatch {
    public ChunkGC(Plugin p) {
        super(p);
    }

    public void enable() {
        ((CraftServer) Bukkit.getServer()).chunkGCPeriod = 0;
        ((CraftServer)Bukkit.getServer()).chunkGCLoadThresh = 0;
        for (World w : Bukkit.getWorlds())
            (((CraftWorld)w).getHandle()).spigotConfig.clearChunksOnTick = true;
        ChunkSyncUtil.syncChunks();
        registerListener(new ChunkGCListener());
        registerTask((new PlayerMoveTask()).runTaskTimer(getBukkitPlugin(), 20L, 5L), PlayerMoveTask.class);
        registerTask((new ChunkGCTask()).runTaskTimer(getBukkitPlugin(), 300L, 1200L), ChunkGCTask.class);
        registerCommand(new CommandForceChunkGC());
    }

    public void disable() {}

    public static ChunkGCLogger log = new ChunkGCLogger();
}
