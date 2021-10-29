package landon.warhammercore.patches.patches.fpoints.utils;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import landon.warhammercore.patches.patches.fpoints.FactionPoints;
import landon.warhammercore.patches.patches.fpoints.struct.ExpiringChunkData;
import org.bukkit.Bukkit;

public class FactionHelper {
    private static long time = TimeUnit.HOURS.toMillis(24L);

    private static void checkAttachedNeighbors(List<FLocation> attachedList, List<FLocation> claimList, FLocation fLocation) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (z != 0 || x != 0) {
                    FLocation neighbor = fLocation.getRelative(x, z);
                    boolean isAlreadyMarked = attachedList.contains(neighbor);
                    if (!isAlreadyMarked)
                        if (claimList.contains(neighbor)) {
                            attachedList.add(neighbor);
                            checkAttachedNeighbors(attachedList, claimList, neighbor);
                        }
                }
            }
        }
    }
}

