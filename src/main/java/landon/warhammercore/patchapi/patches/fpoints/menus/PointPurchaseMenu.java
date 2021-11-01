package landon.warhammercore.patchapi.patches.fpoints.menus;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import java.util.Calendar;

import com.massivecraft.factions.util.CCItemBuilder;
import com.massivecraft.factions.util.TimeUtils;
import com.massivecraft.factions.util.gui.CustomGUI;
import com.massivecraft.factions.util.gui.InventoryItem;
import landon.warhammercore.patchapi.patches.fpoints.FactionPoints;
import landon.warhammercore.patchapi.patches.fpoints.FactionsPointsAPI;
import landon.warhammercore.patchapi.patches.fpoints.struct.PointData;
import landon.warhammercore.patchapi.patches.fpoints.utils.EconomyUtils;
import landon.warhammercore.patchapi.patches.fpoints.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PointPurchaseMenu extends CustomGUI {
    public static int COST_PER_POINT = 1000000;

    public static int POINTS_PER_DAY = 400;

    private Faction faction;

    private static long nextResetTime;

    private static Calendar calendar = Calendar.getInstance();

    static {
        calculateResetTime();
    }

    public PointPurchaseMenu(Player player, Faction faction) {
        super(player, "Purchase Faction Points", 9);
        this.faction = faction;
    }

    public static void calculateResetTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(6, 1);
        nextResetTime = calendar.getTimeInMillis();
    }

    public void redraw() {
        if (this.faction == null || !this.faction.attached()) {
            this.player.closeInventory();
            return;
        }
        long points = 0L, pointsUnlockedToday = 0L;
        PointData data = FactionsPointsAPI.getPointData(this.faction, false);
        if (data != null) {
            points = data.getPoints();
            pointsUnlockedToday = data.getPointsPurchasedToday();
        }
        setItem(3, createPurchaseItem(1, points, pointsUnlockedToday));
        setItem(4, createPurchaseItem(10, points, pointsUnlockedToday));
        setItem(5, createPurchaseItem(100, points, pointsUnlockedToday));
    }

    public static long getNextResetTime() {
        if (nextResetTime < System.currentTimeMillis())
            calculateResetTime();
        return nextResetTime;
    }

    public InventoryItem createPurchaseItem(int amount, long currentPoints, long pointsPurchasedToday) {
        String number = NumberUtils.formatSeconds(amount).intern();
        double cost = (amount * COST_PER_POINT);
        return (new InventoryItem((new CCItemBuilder(Material.PAPER, CC.GreenB + number + "x Faction Points", new String[] {
                CC.Gray + "Click to purchase " + CC.Green + number + CC.Gray + "x Faction Points", CC.Gray + "for " + CC.GreenB + "$" + CC.Green +

                NumberUtils.formatMoney(cost) + CC.Gray + "!", "", CC.GreenB + "Current Faction Points", CC.White + " " +

                NumberUtils.formatSeconds(currentPoints), "", CC.RedB + "Daily Purchases", CC.White + " " +

                NumberUtils.formatSeconds(pointsPurchasedToday) + " / " + NumberUtils.formatSeconds(POINTS_PER_DAY), "", CC.Red + "Daily limit resets in " +

                TimeUtils.formatFutureTime(getNextResetTime()),
                "", CC.GreenB + "What are Faction Points?", CC.Gray + "Faction points are used in /f top", CC.Gray + "to determine the #1 Faction!" })).build())).click(() -> {
            if (!EconomyUtils.hasBalance((OfflinePlayer)this.player, cost)) {
                this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot afford " + amount + "x Faction Points for " + CC.RedB + "$" + CC.Red + NumberUtils.formatMoney(cost) + "!");
                return;
            }
            PointData data = FactionsPointsAPI.getPointData(this.faction, true);
            if (data == null) {
                closeWithDelay();
                this.player.sendMessage(CC.Red + "Unable to find proper point data!");
                FactionPoints.log("Unable to create or find point data for " + this.faction.getTag() + "!");
                return;
            }
            if (data.getPointsPurchasedToday() >= POINTS_PER_DAY) {
                this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You have reached your daily limit of purchasable Faction Points!");
                this.player.sendMessage(CC.Gray + "Daily Limit Resets in " + CC.GrayU + TimeUtils.formatFutureTime(getNextResetTime()) + CC.Gray + "!");
                closeWithDelay();
                return;
            }
            if (data.getPointsPurchasedToday() + amount > POINTS_PER_DAY) {
                this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You have almost reached your daily limit of purchasable Faction Points and cannot purchase this amount!");
                this.player.sendMessage(CC.Gray + "Daily Limit Resets in " + CC.GrayU + TimeUtils.formatFutureTime(getNextResetTime()) + CC.Gray + "!");
                closeWithDelay();
                return;
            }
            EconomyUtils.withdrawBalance((OfflinePlayer)this.player, cost);
            Long newPoints = Long.valueOf(data.addPointsPurchased(amount, this.faction));
            this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.4F);
            this.player.sendMessage(CC.RedB + "- $" + CC.Red + NumberUtils.formatMoney(cost));
            try {
                P.p.getFlogManager().log(this.faction, FLogType.F_POINTS, new String[] { this.player.getName(), CC.GreenB + "PURCHASED", String.valueOf(amount) + " Faction Points " + CC.Gray + "for " + CC.GreenB + "$" + CC.Green + NumberUtils.formatMoney(cost) });
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.faction.sendMessage(CC.GreenB + "(!) " + CC.Green + this.player.getName() + " has purchased " + NumberUtils.formatSeconds(amount) + CC.Green + " Faction Point(s)! (" + NumberUtils.formatSeconds(Double.valueOf(newPoints.longValue()).doubleValue()) + ")");
            redraw();
        });
    }
}

