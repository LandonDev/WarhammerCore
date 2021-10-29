package landon.warhammercore.patches.patches.fpoints.struct;

public class PointChangeLog implements Comparable {
    private int amount;

    private String reason;

    private String pl;

    private long time;

    public PointChangeLog(int amount, String reason, String pl, long time) {
        this.amount = amount;
        this.reason = reason;
        this.pl = pl;
        this.time = time;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getReason() {
        return this.reason;
    }

    public String getPl() {
        return this.pl;
    }

    public long getTime() {
        return this.time;
    }

    public int compareTo(Object o) {
        if (o != null && o instanceof PointChangeLog) {
            PointChangeLog other = (PointChangeLog)o;
            if (other.getTime() > getTime())
                return 1;
            return -1;
        }
        return 0;
    }
}
