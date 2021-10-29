package landon.warhammercore.patches.patches.fpoints.struct;

public class RaidLog {
    private String fWhoRaided;

    private String originated;

    private String breachedAt;

    private long raidTime;

    private long pointsLost;

    public RaidLog(String fWhoRaided, String originated, String breachedAt, long raidTime, long pointsLost) {
        this.fWhoRaided = fWhoRaided;
        this.originated = originated;
        this.breachedAt = breachedAt;
        this.raidTime = raidTime;
        this.pointsLost = pointsLost;
    }

    public String getFWhoRaided() {
        return this.fWhoRaided;
    }

    public String getOriginated() {
        return this.originated;
    }

    public String getBreachedAt() {
        return this.breachedAt;
    }

    public long getRaidTime() {
        return this.raidTime;
    }

    public long getPointsLost() {
        return this.pointsLost;
    }
}
