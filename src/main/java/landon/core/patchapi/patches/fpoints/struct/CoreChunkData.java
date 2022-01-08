package landon.core.patchapi.patches.fpoints.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import landon.core.patchapi.patches.fpoints.FactionPoints;
import landon.core.patchapi.patches.fpoints.FactionsPointsAPI;
import landon.core.patchapi.patches.fpoints.utils.LocationUtils;
import landon.core.patchapi.patches.fpoints.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class CoreChunkData {
    public FLocation masterChunk;
    public static long RAID_COOLDOWN = TimeUnit.HOURS.toMillis(8L);
    private Long lastRaided;
    public Long coreChunkClaimTime;
    public Long coreChunkRemovedAt;
    private Double lastPointsLost;
    private String lastFactionWhoRaided;
    private LinkedList<RaidLog> raidLogList;
    private static final long coreChunkTimeNeeded = TimeUnit.HOURS.toMillis(24L);
    private static final long mapStartThreshhold = TimeUnit.HOURS.toMillis(48L);

    public boolean canBeRaided() {
        if (this.lastRaided == null || this.lastRaided == 0L) {
            return true;
        }
        return System.currentTimeMillis() - this.lastRaided >= RAID_COOLDOWN;
    }

    public boolean canEarnFactionPoints() {
        long start = FactionPoints.get().getStartOfMap();
        long timeSinceStart = System.currentTimeMillis() - start;
        if (timeSinceStart > 0L && timeSinceStart < mapStartThreshhold) {
            return true;
        }
        return this.masterChunk != null && this.coreChunkClaimTime != null && System.currentTimeMillis() - this.coreChunkClaimTime >= coreChunkTimeNeeded;
    }

    public List<RaidLog> getRaidLogList(boolean checkNull) {
        if (!checkNull) {
            return this.raidLogList;
        }
        if (this.raidLogList == null) {
            this.raidLogList = Lists.newLinkedList();
        }
        return this.raidLogList;
    }

    public void onRaidedByFaction(Faction myFaction, Faction faction, Location entityLocation, Block cameFrom) {
        Long factionPoints;
        int y = entityLocation.getBlockY();
        if (y < 20) {
            FactionPoints.debug("Ignoring faction points earning for " + faction.getTag() + " raiding " + myFaction.getTag() + " at " + (Object)entityLocation + " came from: " + (Object)cameFrom);
            return;
        }
        double maxPercentagePoints = 25.0;
        Long points = FactionsPointsAPI.getPoints(myFaction);
        if (points == null || points <= 0L) {
            FactionPoints.debug("No faction points found for " + myFaction.getTag() + ": " + points + ", returning...");
            return;
        }
        double percentToRemove = Math.min(y >= 240 ? maxPercentagePoints : 0.1136 * (double)y, maxPercentagePoints);
        int pointsToTake = (int)Math.floor(Math.min((double)points.longValue(), 0.01 * percentToRemove * (double)points.longValue()));
        if (pointsToTake <= 0) {
            FactionPoints.debug("Tried to take <=0 pointsTotake: " + pointsToTake);
            return;
        }
        if ((long)pointsToTake > points) {
            pointsToTake = (int)Math.floor(points.longValue());
        }
        if ((factionPoints = FactionsPointsAPI.modifyPoints(faction, (double)pointsToTake)) == null) {
            P.p.getLogger().info("[CosmicFactionPoints] Unable to modifyPoints for " + faction.getTag() + " when raiding " + myFaction.getTag());
            return;
        }
        this.lastFactionWhoRaided = faction.getTag();
        this.lastPointsLost = (double)pointsToTake;
        this.setLastRaided(System.currentTimeMillis());
        Long raidedNewPoints = FactionsPointsAPI.modifyPoints(myFaction, (double)(-pointsToTake));
        Bukkit.getLogger().info("[CosmicFactionPoints] " + myFaction.getTag() + " being raided by " + faction.getTag() + ", taking=" + pointsToTake);
        if (this.getRaidLogList(true).size() > 100) {
            this.getRaidLogList().remove(this.getRaidLogList().size() - 1);
        }
        this.getRaidLogList(true).add(new RaidLog(faction.getTag(), ChatColor.stripColor((String) LocationUtils.printPretty((Location)cameFrom.getLocation(), (ChatColor)ChatColor.WHITE, (boolean)false)), ChatColor.stripColor((String)LocationUtils.printPretty((Location)entityLocation, (ChatColor)ChatColor.WHITE, (boolean)false)), System.currentTimeMillis(), pointsToTake));
        faction.sendMessage("");
        faction.sendMessage(CC.RedB + "(!) " + CC.Red + "You have breached " + myFaction.getTag() + "'s Core Chunk, taking " + CC.RedB + NumberUtils.formatSeconds((double)pointsToTake) + CC.Red + " Faction Points!");
        faction.sendMessage("");
        myFaction.sendMessage("");
        myFaction.sendMessage(CC.RedB + "(!) " + CC.Red + "Your Core Chunk has been breached by " + CC.RedU + faction.getTag() + CC.Red + ", they took " + CC.RedB + NumberUtils.formatSeconds((double)pointsToTake) + CC.Red + " Faction Points!");
        myFaction.sendMessage(CC.Gray + "Your faction is now on an " + CC.GrayU + "8 hour" + CC.Gray + " raid cooldown.");
        myFaction.sendMessage("");
        String locationString = NumberUtils.formatMoney((double)entityLocation.getX()) + "x " + NumberUtils.formatMoney((double)entityLocation.getY()) + "y " + NumberUtils.formatMoney((double)entityLocation.getZ()) + "z";
        FactionPoints.get().getPointManager().logPointChange(myFaction, null, "raided by " + faction.getTag() + " at " + locationString, -pointsToTake);
        FactionPoints.get().getPointManager().logPointChange(faction, null, "raided " + myFaction.getTag() + " at " + locationString, pointsToTake);
        try {
            P.p.getFlogManager().log(myFaction, FLogType.F_POINTS, new String[]{CC.RedB + "- " + CC.Red + pointsToTake + " Faction Points (" + (raidedNewPoints == null ? 0L : raidedNewPoints) + ")", CC.Red + faction.getTag(), "- " + CC.RedB + "RAIDED"});
            P.p.getFlogManager().log(faction, FLogType.F_POINTS, new String[]{CC.GreenB + "+ " + CC.Green + pointsToTake + " Faction Points (" + factionPoints + ")", CC.Red + myFaction.getTag(), "- " + CC.GreenB + "RAIDED"});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FLocation getMasterChunk() {
        return this.masterChunk;
    }

    public Long getLastRaided() {
        return this.lastRaided;
    }

    public Long getCoreChunkClaimTime() {
        return this.coreChunkClaimTime;
    }

    public Long getCoreChunkRemovedAt() {
        return this.coreChunkRemovedAt;
    }

    public Double getLastPointsLost() {
        return this.lastPointsLost;
    }

    public String getLastFactionWhoRaided() {
        return this.lastFactionWhoRaided;
    }

    public LinkedList<RaidLog> getRaidLogList() {
        return this.raidLogList;
    }

    public void setMasterChunk(FLocation masterChunk) {
        this.masterChunk = masterChunk;
    }

    public void setLastRaided(Long lastRaided) {
        this.lastRaided = lastRaided;
    }

    public void setCoreChunkClaimTime(Long coreChunkClaimTime) {
        this.coreChunkClaimTime = coreChunkClaimTime;
    }

    public void setCoreChunkRemovedAt(Long coreChunkRemovedAt) {
        this.coreChunkRemovedAt = coreChunkRemovedAt;
    }

    public void setLastPointsLost(Double lastPointsLost) {
        this.lastPointsLost = lastPointsLost;
    }

    public void setLastFactionWhoRaided(String lastFactionWhoRaided) {
        this.lastFactionWhoRaided = lastFactionWhoRaided;
    }

    public void setRaidLogList(LinkedList<RaidLog> raidLogList) {
        this.raidLogList = raidLogList;
    }
}
