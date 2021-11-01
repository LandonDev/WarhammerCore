package landon.warhammercore.patchapi.patches.fpoints.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.util.TimeUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ChunkNotification implements Comparable {
    private String chunkLocation;

    private int expirationTimeInSeconds;

    public String getChunkLocation() {
        return this.chunkLocation;
    }

    public int getExpirationTimeInSeconds() {
        return this.expirationTimeInSeconds;
    }

    public ChunkNotification(FLocation chunkLocation, int expires) {
        this.chunkLocation = FLocation.chunkToBlock((int)chunkLocation.getX()) + "x " + FLocation.chunkToBlock((int)chunkLocation.getZ()) + "z";
        this.expirationTimeInSeconds = expires;
    }

    public int compareTo(Object o) {
        if (o != null && o instanceof ChunkNotification) {
            ChunkNotification other = (ChunkNotification)o;
            if (other.getExpirationTimeInSeconds() > getExpirationTimeInSeconds())
                return 1;
            if (other.getExpirationTimeInSeconds() == this.expirationTimeInSeconds)
                return 0;
            return -1;
        }
        return 0;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean longMsg) {
        return CC.RedB + "(!) " + CC.Red + (longMsg ? "Your faction's claim" : "Claim") + " at " + CC.RedB + this.chunkLocation + CC.Red + " will expire in " + CC.RedB +
                TimeUtils.formatSeconds(this.expirationTimeInSeconds) + CC.Red + "!";
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return (new HashCodeBuilder(17, 37))
                .append(this.chunkLocation).append(this.expirationTimeInSeconds).toHashCode();
    }
}
