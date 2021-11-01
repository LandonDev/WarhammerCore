package landon.warhammercore.patchapi.patches.fpoints.struct;

import com.massivecraft.factions.Faction;
import com.google.gson.annotations.SerializedName;

public class ExpiringChunkData {
    public long timeClaimed;

    public long expirationTime;

    public String fId;

    @SerializedName("cToWar")
    public boolean connectedToWarzone;

    public long getTimeClaimed() {
        return this.timeClaimed;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public String getFId() {
        return this.fId;
    }

    public void setConnectedToWarzone(boolean connectedToWarzone) {
        this.connectedToWarzone = connectedToWarzone;
    }

    public boolean isConnectedToWarzone() {
        return this.connectedToWarzone;
    }

    public ExpiringChunkData(long timeClaimed, long expiration, String fId) {
        this.timeClaimed = timeClaimed;
        this.expirationTime = expiration;
        this.fId = fId;
    }

    public void onClaimed(Faction whoClaimed) {
        this.timeClaimed = System.currentTimeMillis();
        this.expirationTime = 0L;
        this.fId = whoClaimed.getId();
    }
}
