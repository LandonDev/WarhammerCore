package landon.warhammercore.patches.patches.blockvalues;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.massivecraft.factions.P;
import landon.warhammercore.patches.patches.ftop.utils.BlockValueLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

final class BlockValueListener implements Listener {
    HashMap<Block, Long> blockPlaceEvent = new HashMap<>();

    HashSet<String> scannedChunks = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (BlockValue.blocksWithValue.contains(e.getBlock().getType())) {
            BlockValue.trackedBlockValues.put(convertBlockToString(e.getBlock()), convertBlockToBlockValueLocation(e.getBlock()));
            this.blockPlaceEvent.put(e.getBlock(), Long.valueOf(System.currentTimeMillis()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (BlockValue.blocksWithValue.contains(e.getBlock().getType()) &&
                BlockValue.trackedBlockValues.containsKey(convertBlockToString(e.getBlock()))) {
            BlockValue.trackedBlockValues.remove(convertBlockToString(e.getBlock()));
            this.blockPlaceEvent.remove(e.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        final List<Location> blocksToCheck = new ArrayList<>();
        for (Block b : e.blockList()) {
            if (BlockValue.blocksWithValue.contains(b.getType()))
                blocksToCheck.add(b.getLocation());
        }
        Bukkit.getScheduler().runTaskLater(P.p, new Runnable() {
            public void run() {
                for (Location l : blocksToCheck) {
                    if (!BlockValue.blocksWithValue.contains(l.getBlock().getType())) {
                        String s = BlockValueListener.convertBlockToString(l.getBlock());
                        Bukkit.getLogger().info("[Arkkit (BlockValue)] Removing " + s + " from blockValues list due to explosion.");
                        BlockValue.trackedBlockValues.remove(s);
                    }
                }
            }
        },  5L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!P.p.getConfig().getBoolean("settings.blockValue.chunkLoadScan"))
            return;
        String cs = e.getChunk().getX() + "," + e.getChunk().getZ();
        if (this.scannedChunks.contains(cs))
            return;
        FLocation fl = new FLocation(e.getChunk().getBlock(0, 0, 0));
        if (Board.getFactionAt(fl).isNormal()) {
            int blocksFound = 0;
            for (BlockState blockState : e.getChunk().getTileEntities()) {
                if (BlockValue.blocksWithValue.contains(blockState.getBlock().getType())) {
                    String blockString = convertBlockToString(blockState.getBlock());
                    if (!BlockValue.trackedBlockValues.containsKey(blockString)) {
                        BlockValue.trackedBlockValues.put(blockString, convertBlockToBlockValueLocation(blockState.getBlock()));
                        blocksFound++;
                    }
                }
            }
            if (blocksFound > 0)
                Bukkit.getLogger().info("[Arkkit (BlockValue)] Tracked " + blocksFound + " new valueBlocks in chunk: " + cs);
        }
        this.scannedChunks.add(e.getChunk().getX() + "," + e.getChunk().getZ());
    }

    public static String convertBlockToString(Block b) {
        return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + "," + b.getZ();
    }

    public static BlockValueLocation convertBlockToBlockValueLocation(Block b) {
        return new BlockValueLocation(b.getX(), b.getY(), b.getZ(), b.getWorld().getName(), b.getType(), System.currentTimeMillis());
    }
}

