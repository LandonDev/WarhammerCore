package landon.warhammercore.patchapi.patches.ftop.manager;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import landon.warhammercore.patchapi.patches.blockvalues.BlockValue;
import landon.warhammercore.patchapi.patches.ftop.FactionsTop;
import landon.warhammercore.patchapi.patches.ftop.struct.LoadCallback;
import landon.warhammercore.patchapi.patches.ftop.struct.StoredFaction;
import landon.warhammercore.patchapi.patches.ftop.struct.TopFaction;
import landon.warhammercore.patchapi.patches.ftop.utils.BlockValueLocation;
import landon.warhammercore.patchapi.patches.ftop.utils.SpawnerLocation;
import landon.warhammercore.patchapi.patches.spawnerfree.SpawnerFee;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class TopManager {
    private LinkedHashMap<Integer, TopFaction> topFactions = new LinkedHashMap<>();

    public LinkedHashMap<Integer, TopFaction> getTopFactions() {
        return this.topFactions;
    }

    private LinkedHashMap<Integer, TopFaction> wealthTopFactions = new LinkedHashMap<>();

    public LinkedHashMap<Integer, TopFaction> getWealthTopFactions() {
        return this.wealthTopFactions;
    }

    private LinkedHashMap<String, TopFaction> fastFactions = new LinkedHashMap<>();

    public LinkedHashMap<String, TopFaction> getFastFactions() {
        return this.fastFactions;
    }

    private int totalSpawnersFound = 0;

    private int refreshTimer = 5;

    public int getRefreshTimer() {
        return this.refreshTimer;
    }

    boolean loadingData = false;

    public boolean isLoadingData() {
        return this.loadingData;
    }

    private long lastUpdate = -1L;

    private long lastWealthUpdate = -1L;

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public long getLastWealthUpdate() {
        return this.lastWealthUpdate;
    }

    private Map<Integer, String> previousFTops = new HashMap<>();

    private Map<Integer, String> previousWealthFTops = new HashMap<>();

    public TopFaction getTopFaction(String id) {
        return this.fastFactions.get(id);
    }

    private static DecimalFormat format = new DecimalFormat("#,###");

    public void loadTopFactions(LoadCallback callback, int delay) {
        loadTopFactions(callback, delay, false);
    }

    public void loadTopFactions(final LoadCallback callback, int delay, final boolean forceWealth) {
        long last = forceWealth ? this.lastWealthUpdate : this.lastUpdate;
        if (delay != -1 && last != -1L && System.currentTimeMillis() - last < TimeUnit.MINUTES.toMillis(this.refreshTimer))
            return;
        final boolean webLinkEnabled = Bukkit.getPluginManager().isPluginEnabled("CosmicWebLink");
        if (forceWealth) {
            this.lastWealthUpdate = System.currentTimeMillis();
        } else {
            this.lastUpdate = System.currentTimeMillis();
        }
        final HashMap<String, List<SpawnerLocation>> spawners = new HashMap<>();
        final HashMap<String, List<BlockValueLocation>> containers = new HashMap<>();
        final HashMap<String, StoredFaction> storedFactions = new HashMap<>();
        System.out.println("Loading spawners..");
        (new BukkitRunnable() {
            public void run() {
                try {
                    Field mapField = Board.class.getDeclaredField("flocationIds");
                    mapField.setAccessible(true);
                    ConcurrentHashMap<FLocation, String> factionLocations = (ConcurrentHashMap<FLocation, String>)mapField.get(Board.getInstance());
                    TopManager.this.loadingData = true;
                    long start = System.currentTimeMillis();
                    long firstStart = System.currentTimeMillis();
                    int chunksChecked = 0;
                        int spawnersChecked = 0;
                        for (Map.Entry<String, SpawnerLocation> spawner : (Iterable<Map.Entry<String, SpawnerLocation>>) SpawnerFee.playerSpawners.entrySet()) {
                            spawnersChecked++;
                            if (((SpawnerLocation)spawner.getValue()).getSpawnerType() == EntityType.UNKNOWN)
                                continue;
                            String chunkCoords = ((SpawnerLocation)spawner.getValue()).getWorldName() + ":" + ((SpawnerLocation)spawner.getValue()).getChunkX() + "," + ((SpawnerLocation)spawner.getValue()).getChunkZ();
                            List<SpawnerLocation> container = (List<SpawnerLocation>)spawners.get(chunkCoords);
                            if (container == null)
                                container = Lists.newArrayList();
                            container.add(spawner.getValue());
                            TopManager.this.totalSpawnersFound++;
                            spawners.put(chunkCoords, container);
                        }
                        System.out.println("Loaded " + spawnersChecked + " spawners in " + spawners.size() + " chunks.");
                        AtomicInteger index = new AtomicInteger(0);
                        long trackStart = System.currentTimeMillis();
                        if (BlockValue.trackedBlockValues != null)
                            (new HashMap<>(BlockValue.trackedBlockValues)).forEach((coordString, blockValue) -> {
                                if (blockValue.getBlockType() == Material.HOPPER && !FactionsTop.get().isIncludeHoppers())
                                    return;
                                String chunkCoords = blockValue.getWorldName() + ":" + blockValue.getChunkX() + "," + blockValue.getChunkZ();
                                List<BlockValueLocation> currentBlocksInChunk = containers.computeIfAbsent(chunkCoords, e -> Lists.newArrayList());
                                currentBlocksInChunk.add(blockValue);
                                index.addAndGet(1);
                            });
                        System.out.println("Took " + (System.currentTimeMillis() - trackStart) + " ms to load " + index.get() + " trackedBlockValues into chunk map.");
                        System.out.println("Scanning " + factionLocations.size() + " Faction Claims..");
                        for (Map.Entry<FLocation, String> entry : factionLocations.entrySet()) {
                            FLocation chunkLoc = entry.getKey();
                            String facId = entry.getValue();
                            int factionID = Integer.parseInt(entry.getValue());
                            World world = (chunkLoc != null) ? chunkLoc.getWorld() : null;
                            if (factionID <= 0 || world == null)
                                continue;
                            String chunkCoords = world.getName() + ":" + chunkLoc.getX() + "," + chunkLoc.getZ();
                            List<SpawnerLocation> chunkSpawners = (List<SpawnerLocation>)spawners.get(chunkCoords);
                            if (chunkSpawners != null) {
                                StoredFaction storedFaction = storedFactions.computeIfAbsent(facId, fac -> new StoredFaction(facId, Lists.newArrayList(), Lists.newArrayList()));
                                storedFaction.getFactionSpawners().addAll(chunkSpawners);
                            }
                            List<BlockValueLocation> locations = (List<BlockValueLocation>)containers.get(chunkCoords);
                            if (locations != null) {
                                StoredFaction faction = storedFactions.computeIfAbsent(facId, e -> new StoredFaction(facId, Lists.newArrayList(), Lists.newArrayList()));
                                faction.getFactionContainers().addAll(locations);
                            }
                            if (++chunksChecked % 10000 == 0)
                                Bukkit.getLogger().info("Checked " + chunksChecked + " Chunks..");
                        }
                    start = System.currentTimeMillis();
                    Bukkit.getLogger().info("Starting faction check..");
                    for (Faction faction : Factions.i.get()) {
                        try {
                            if (!faction.isNormal())
                                continue;
                            StoredFaction storedFaction = (StoredFaction)storedFactions.get(faction.getId());
                            if (storedFaction == null) {
                                storedFaction = new StoredFaction(faction.getId(), Lists.newArrayList(), Lists.newArrayList());
                                storedFactions.put(faction.getId(), storedFaction);
                            } else {
                                storedFaction.calculateSpawnerWorth();
                            }
                            storedFaction.calculateTotalBalance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Bukkit.getLogger().info("Finished faction check in " + (System.currentTimeMillis() - start) + "ms");
                    System.out.println("Scanned " + chunksChecked + " Chunks in " + (System.currentTimeMillis() - firstStart) + "ms found " + TopManager.this.totalSpawnersFound + " spawners.");
                    LinkedHashMap<String, StoredFaction> topSortedFactions = TopManager.this.sortByComparator(storedFactions, false, (forceWealth && (FactionsTop.get()).factionPointsEnabled));
                    boolean wealth = (forceWealth && (FactionsTop.get()).factionPointsEnabled);
                    if (!wealth)
                        TopManager.this.fastFactions.clear();
                    int place = 1;
                    for (Map.Entry<String, StoredFaction> entry : topSortedFactions.entrySet()) {
                        Faction topFaction = ((StoredFaction)entry.getValue()).getFaction();
                        if (topFaction != null) {
                            StoredFaction storedFaction = entry.getValue();
                            if (place < 50)
                                System.out.println("Top Faction " + topFaction
                                        .getTag() + " is #" + place + ", found " + storedFaction.getMobSpawnerCount() + " Spawners worth " + storedFaction
                                        .getTotalSpawnerWorth() + " with top player balance: " + storedFaction
                                        .getTotalBalance() + " owner: " + storedFaction.getOwner());
                            if (place <= 3) {
                                String previousPlace = forceWealth ? (String)TopManager.this.previousWealthFTops.get(Integer.valueOf(place)) : (String)TopManager.this.previousFTops.get(Integer.valueOf(place));
                                if (forceWealth) {
                                    TopManager.this.previousWealthFTops.put(Integer.valueOf(place), topFaction.getId());
                                } else {
                                    TopManager.this.previousFTops.put(Integer.valueOf(place), topFaction.getId());
                                }
                                if (previousPlace == null || !previousPlace.equals(topFaction.getId())) {
                                    String worthString;
                                    if (!(FactionsTop.get()).factionPointsEnabled || forceWealth) {
                                        worthString = "$" + TopManager.format.format(storedFaction.getTotalWorth());
                                    } else {
                                        worthString = storedFaction.getFactionPoints() + " Faction Points";
                                    }
                                    int finalPlace = place;
                                    Bukkit.getScheduler().runTask(P.p, () -> Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.UNDERLINE.toString() + topFaction.getTag() + ChatColor.GREEN + " has taken " + ChatColor.GREEN + ChatColor.BOLD.toString() + "#" + finalPlace + ChatColor.GREEN + " in /f " + (((FactionsTop.get()).factionPointsEnabled && forceWealth) ? "wealth" : "top") + " with " + worthString));
                                }
                            }
                            TopFaction fac = new TopFaction(storedFaction, place);
                            if (wealth) {
                                TopManager.this.wealthTopFactions.put(Integer.valueOf(place++), fac);
                                continue;
                            }
                            TopManager.this.topFactions.put(Integer.valueOf(place++), fac);
                            TopManager.this.fastFactions.put(storedFaction.getFactionID(), fac);
                        }
                    }
                    topSortedFactions.clear();
                    TopManager.this.loadingData = false;
                    if (callback != null)
                        callback.onLoad();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).runTaskLaterAsynchronously(P.p, (delay == -1) ? 0L : delay);
    }

    public LinkedHashMap<String, StoredFaction> sortByComparator(HashMap<String, StoredFaction> unsortMap, boolean order, boolean wealth) {
        if (unsortMap == null)
            return null;
        List<Map.Entry<String, StoredFaction>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, (o1, o2) -> order ? ((StoredFaction)o1.getValue()).compareTo((StoredFaction)o2.getValue(), wealth) : ((StoredFaction)o2.getValue()).compareTo((StoredFaction)o1.getValue(), wealth));
        LinkedHashMap<String, StoredFaction> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, StoredFaction> entry : list)
            sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }
}
