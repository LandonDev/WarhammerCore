package landon.warhammercore.patchapi.patches.fpoints.utils;

import com.massivecraft.factions.FLocation;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

