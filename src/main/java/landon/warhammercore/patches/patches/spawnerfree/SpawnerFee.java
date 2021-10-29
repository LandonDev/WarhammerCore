package landon.warhammercore.patches.patches.spawnerfree;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import landon.warhammercore.patches.UHCFPatch;
import landon.warhammercore.patches.patches.ftop.utils.SpawnerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class SpawnerFee extends UHCFPatch {
    public static HashMap<String, SpawnerLocation> playerSpawners;

    public static boolean spawnerFee = true;

    private static Thread spawnerFeeSaveThread;

    private static boolean spawnersSaved;

    private static boolean cleanSpawners;

    public SpawnerFee(Plugin p) {
        super(p);
        spawnersSaved = false;
        cleanSpawners = !(new File("spawner_fee_clean")).exists();
        if (cleanSpawners)
            try {
                (new File("spawner_fee_clean")).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        spawnerFeeSaveThread = new Thread() {
            public void run() {
                SpawnerFee.saveSpawners();
                SpawnerFee.spawnersSaved = true;
            }
        };
        spawnerFeeSaveThread.setName("SpawnerFeeAsyncSaveThread");
    }

    public void enable() {
        spawnerFee = Arkkit.get().getConfig().getBoolean("settings.spawner_fee.spawnerFee");
        loadSpawners();
        registerListener(new SpawnerFeeListener());
        registerCommand(new CommandSpawnerFee("spawnerfee", "/<command>", "Define spawner fees."));
    }

    public synchronized void disable() {
        spawnerFeeSaveThread.start();
        while (!spawnersSaved) {
            try {
                Bukkit.getLogger().info("Waiting for spawnerFeeSaveThread to complete saving " + playerSpawners.size() + " spawners to proceed...");
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSpawners() {
        playerSpawners = new HashMap<>();
        int count = 0;
        boolean conversion = false;
        boolean hasWhitelist = Bukkit.hasWhitelist();
        if (!(new File("playerSpawners.dat")).exists())
            try {
                (new File("playerSpawners.dat")).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("playerSpawners.dat")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0)
                    try {
                        String[] data = line.split(",");
                        EntityType spawnerType = EntityType.UNKNOWN;
                        long placeTime = 0L;
                        int spawnerCount = 1;
                        if (data.length == 4) {
                            if (!conversion) {
                                conversion = true;
                                if (!hasWhitelist)
                                    Bukkit.setWhitelist(true);
                            }
                            Block b = Bukkit.getWorld(data[0]).getBlockAt(Integer.valueOf(data[1]).intValue(), Integer.valueOf(data[2]).intValue(), Integer.valueOf(data[3]).intValue());
                            if (b.getType() == Material.MOB_SPAWNER) {
                                spawnerType = ((CreatureSpawner)b.getState()).getSpawnedType();
                                Bukkit.getLogger().info("[Arkkit (SpawnerFee)] Stored mob spawner at " + b.getLocation() + " as spawnerType " + spawnerType + "!");
                            }
                        } else if (data.length == 5) {
                            spawnerType = EntityType.valueOf(data[4]);
                        } else if (data.length == 6) {
                            String entityNameAndCount = data[4];
                            if (entityNameAndCount.contains(":")) {
                                spawnerType = EntityType.valueOf(entityNameAndCount.split(":")[0]);
                                spawnerCount = Integer.parseInt(entityNameAndCount.split(":")[1]);
                            } else {
                                spawnerType = EntityType.valueOf(data[4]);
                            }
                            placeTime = Long.parseLong(data[5]);
                        }
                        if (spawnerType != EntityType.UNKNOWN) {
                            if (cleanSpawners) {
                                Block b = Bukkit.getWorld(data[0]).getBlockAt(Integer.valueOf(data[1]).intValue(), Integer.valueOf(data[2]).intValue(), Integer.valueOf(data[3]).intValue());
                                if (b.getType() != Material.MOB_SPAWNER) {
                                    Bukkit.getLogger().info("[Arkkit (SpawnerFee)] Cleaned non-existing mob spawner at " + b.getLocation() + " as spawnerType " + spawnerType + "!");
                                    continue;
                                }
                                CreatureSpawner cs = (CreatureSpawner)b.getState();
                                if (cs.getSpawnerCount() != spawnerCount) {
                                    Bukkit.getLogger().info("[Arkkit (SpawnerFee) Assigned spawnerCount value:1(" + cs.getSpawnerCount() + ") to spawner at: " + b.getLocation());
                                    cs.setSpawnerCount((short)1);
                                }
                            }
                            playerSpawners.put(data[0] + "," + data[1] + "," + data[2] + "," + data[3], new SpawnerLocation(
                                    Integer.valueOf(data[1]).intValue(), Integer.valueOf(data[2]).intValue(), Integer.valueOf(data[3]).intValue(), data[0], spawnerType, placeTime, spawnerCount));
                            count++;
                        }
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
            }
            reader.close();
            if (conversion) {
                if (!hasWhitelist)
                    Bukkit.setWhitelist(false);
                Bukkit.shutdown();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Bukkit.getLogger().info("Loaded " + count + " playerSpawner locations from disk.");
    }

    protected static void saveSpawners() {
        try {
            (new File("playerSpawners.dat")).delete();
            BufferedWriter out = new BufferedWriter(new FileWriter("playerSpawners.dat"));
            Iterator<SpawnerLocation> it = playerSpawners.values().iterator();
            while (it.hasNext()) {
                SpawnerLocation sl = it.next();
                if (sl.getSpawnerType() != EntityType.UNKNOWN)
                    try {
                        out.write(sl.string() + "\n");
                    } catch (Exception err) {
                        if (sl != null)
                            Bukkit.getLogger().info("[SpawnerFee] Failed to parse SpawnerLocation at " + sl.getX() + "," + sl.getY() + "," + sl.getZ());
                        err.printStackTrace();
                    }
            }
            out.close();
            Bukkit.getLogger().info("Saved " + playerSpawners.size() + " playerSpawner locations to disk.");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static boolean isPlayerSpawner(Block b) {
        try {
            return playerSpawners.containsKey(b.getWorld().getName() + "," + b.getX() + "," + b.getY() + "," + b.getZ());
        } catch (Exception err) {
            return false;
        }
    }

    public static void updateMobSpawnerData(Block b) {
        playerSpawners.put(SpawnerFeeListener.convertBlockToString(b), SpawnerFeeListener.convertBlockToSpawnerLocation(b));
    }
}
