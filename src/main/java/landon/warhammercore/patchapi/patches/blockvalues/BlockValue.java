package landon.warhammercore.patchapi.patches.blockvalues;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.UHCFPatch;
import landon.warhammercore.patchapi.patches.ftop.utils.BlockValueLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

public class BlockValue extends UHCFPatch {
    public static HashMap<String, BlockValueLocation> trackedBlockValues;

    protected static HashSet<Material> blocksWithValue = new HashSet<>(Arrays.asList(new Material[] { Material.HOPPER, Material.BEACON }));

    private static Thread blockValueSaveThread;

    private static boolean blocksSaved;

    public BlockValue(Plugin p) {
        super(p);
        blocksSaved = false;
    }

    public void enable() {
        blockValueSaveThread = new Thread() {
            public void run() {
                BlockValue.saveBlockValues();
                BlockValue.blocksSaved = true;
            }
        };
        blockValueSaveThread.setName("BlockValueAsyncSaveThread");
        loadBlockValues();
        registerListener(new BlockValueListener());
        P.p.getCommand("blockvalue").setExecutor(new CommandBlockValue());
        scanLoadedChunks();
    }

    public synchronized void disable() {
        blockValueSaveThread.start();
        while (!blocksSaved) {
            try {
                Bukkit.getLogger().info("Waiting for BlockValueSaveThread to complete saving " + trackedBlockValues.size() + " spawners to proceed...");
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        blockValueSaveThread.interrupt();
    }

    private void loadBlockValues() {
        trackedBlockValues = new HashMap<>();
        int count = 0;
        if (!(new File("blockValues.dat")).exists())
            try {
                (new File("blockValues.dat")).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("blockValues.dat")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0)
                    try {
                        String[] data = line.split(",");
                        Material blockType = Material.AIR;
                        long placeTime = 0L;
                        if (data.length != 6) {
                            Bukkit.getLogger().info("[Auqkwa (BlockValue)] Failed to parse blockValue: " + line);
                            continue;
                        }
                        blockType = Material.valueOf(data[4]);
                        placeTime = Long.parseLong(data[5]);
                        if (blockType != Material.AIR) {
                            trackedBlockValues.put(data[0] + "," + data[1] + "," + data[2] + "," + data[3], new BlockValueLocation(
                                    Integer.valueOf(data[1]).intValue(), Integer.valueOf(data[2]).intValue(), Integer.valueOf(data[3]).intValue(), data[0], blockType, placeTime));
                            count++;
                        }
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Bukkit.getLogger().info("[Auqkwa (BlockValue)] Loaded " + count + " blockValue locations from disk.");
    }

    protected static void saveBlockValues() {
        try {
            (new File("blockValues.dat")).delete();
            BufferedWriter out = new BufferedWriter(new FileWriter("blockValues.dat"));
            Iterator<BlockValueLocation> it = trackedBlockValues.values().iterator();
            while (it.hasNext()) {
                BlockValueLocation sl = it.next();
                if (sl.getBlockType() != Material.AIR)
                    try {
                        out.write(sl.string() + "\n");
                    } catch (Exception err) {
                        if (sl != null)
                            Bukkit.getLogger().info("[BlockValue] Failed to parse BlockValueLocation at " + sl.getX() + "," + sl.getY() + "," + sl.getZ());
                        err.printStackTrace();
                    }
            }
            out.close();
            Bukkit.getLogger().info("[Auqkwa (BlockValue)] Saved " + trackedBlockValues.size() + " blockValue locations to disk.");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static boolean isValueBlock(Block b) {
        try {
            return trackedBlockValues.containsKey(b.getWorld().getName() + "," + b.getX() + "," + b.getY() + "," + b.getZ());
        } catch (Exception err) {
            return false;
        }
    }

    public static int getBlockValueBase(Material m) {
        return P.p.getConfig().getInt("patches.blockValue." + m.name());
    }

    private void scanLoadedChunks() {
        for (World w : Bukkit.getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                String cs = c.getX() + "," + c.getZ();
                FLocation fl = new FLocation(c.getBlock(0, 0, 0));
                if (Board.getFactionAt(fl).isNormal()) {
                    int blocksFound = 0;
                    for (BlockState blockState : c.getTileEntities()) {
                        if (blocksWithValue.contains(blockState.getBlock().getType())) {
                            String blockString = BlockValueListener.convertBlockToString(blockState.getBlock());
                            if (!trackedBlockValues.containsKey(blockString)) {
                                trackedBlockValues.put(blockString, BlockValueListener.convertBlockToBlockValueLocation(blockState.getBlock()));
                                blocksFound++;
                            }
                        }
                    }
                    if (blocksFound > 0)
                        Bukkit.getLogger().info("[Auqkwa (BlockValue)] Tracked " + blocksFound + " new valueBlocks in chunk: " + cs);
                }
            }
        }
    }
}
