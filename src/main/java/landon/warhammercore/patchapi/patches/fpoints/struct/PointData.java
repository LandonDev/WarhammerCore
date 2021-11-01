package landon.warhammercore.patchapi.patches.fpoints.struct;

import com.massivecraft.factions.Faction;
import landon.warhammercore.patchapi.patches.fpoints.FactionPoints;

import java.util.Calendar;

public class PointData {
    public long points;

    private long lastPointPurchased;

    private int pointsPurchasedToday;

    public void setPoints(long points) {
        this.points = points;
    }

    public void setLastPointPurchased(long lastPointPurchased) {
        this.lastPointPurchased = lastPointPurchased;
    }

    public void setPointsPurchasedToday(int pointsPurchasedToday) {
        this.pointsPurchasedToday = pointsPurchasedToday;
    }

    public long getPoints() {
        return this.points;
    }

    public PointData(long points) {
        this.points = points;
    }

    public long getLastPointPurchased() {
        return this.lastPointPurchased;
    }

    public int getPointsPurchasedToday() {
        if (this.pointsPurchasedToday != 0 && this.lastPointPurchased != 0L &&
                isDifferentDay(this.lastPointPurchased)) {
            FactionPoints.debug("Different day detected, resetting points purchased today: " + this.pointsPurchasedToday + ", last=" + this.lastPointPurchased);
            this.pointsPurchasedToday = 0;
        }
        return this.pointsPurchasedToday;
    }

    private boolean isDifferentDay(long pastTime) {
        Calendar today = Calendar.getInstance();
        int dayToday = today.get(5);
        today.setTimeInMillis(pastTime);
        int lastDayWePurchasedSome = today.get(5);
        return (dayToday != lastDayWePurchasedSome);
    }

    public long addPointsPurchased(double pointsAdded, Faction faction) {
        int currentPoints = getPointsPurchasedToday();
        this.lastPointPurchased = System.currentTimeMillis();
        this.pointsPurchasedToday = (int)(currentPoints + pointsAdded);
        this.points = (long)(this.points + pointsAdded);
        FactionPoints.debug("Giving " + pointsAdded + " points to " + faction.getTag() + " id=" + faction.getId());
        return this.points;
    }
}
