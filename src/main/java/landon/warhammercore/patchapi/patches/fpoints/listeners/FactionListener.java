package landon.warhammercore.patchapi.patches.fpoints.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import landon.warhammercore.patchapi.patches.fpoints.FactionsPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {
    @EventHandler
    public void onFactionCreate(FactionCreateEvent event) {
        FactionsPointsAPI.removePoints(event.getFactionId());
    }

    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        FactionsPointsAPI.removePoints(event.getFaction());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeave(FPlayerLeaveEvent event) {
        if (event.getFaction().getFPlayers().size() <= 1)
            Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> {
                if (event.getFaction().getFPlayers().size() <= 0 && !event.getFaction().attached()) {
                    FactionsPointsAPI.removePoints(event.getFaction());
                }
            }, 1L);
    }

    private Map<Object, Object> expiringMsgs = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build().asMap();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLandClaimMonitor(LandClaimEvent event) {
        if (isOutsideWorldBorder(event.getLocation()) && event.getPlayer() != null && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot claim land outside of the WorldBorder!");
        }
    }

    private boolean isOutsideWorldBorder(FLocation location) {
        WorldBorder border = (WorldBorder)Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (border == null)
            return false;
        BorderData data = border.getWorldBorder(location.getWorldName());
        if (data == null)
            return false;
        return (!data.insideBorder((location.getX() << 4L), (location.getZ() << 4L)) || !data.insideBorder(((location.getX() << 4L) + 15L), ((location.getZ() << 4L) + 15L)));
    }
}

