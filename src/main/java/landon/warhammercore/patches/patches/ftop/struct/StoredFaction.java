package landon.warhammercore.patches.patches.ftop.struct;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import landon.warhammercore.patches.patches.fpoints.FactionsPointsAPI;
import landon.warhammercore.patches.patches.ftop.FactionsTop;
import landon.warhammercore.patches.patches.ftop.utils.BlockValueLocation;
import landon.warhammercore.patches.patches.ftop.utils.SpawnerLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class StoredFaction implements Comparable<StoredFaction> {
    String factionID;

    List<SpawnerLocation> factionSpawners;

    private List<BlockValueLocation> factionContainers;

    public String getFactionID() {
        return this.factionID;
    }

    public List<SpawnerLocation> getFactionSpawners() {
        return this.factionSpawners;
    }

    public List<BlockValueLocation> getFactionContainers() {
        return this.factionContainers;
    }

    double totalSpawnerWorth = 0.0D;

    public double getTotalSpawnerWorth() {
        return this.totalSpawnerWorth;
    }

    double totalBalance = 0.0D;

    double containerWorth;

    double bankBalance;

    double factionPoints;

    public double getTotalBalance() {
        return this.totalBalance;
    }

    public double getContainerWorth() {
        return this.containerWorth;
    }

    public double getBankBalance() {
        return this.bankBalance;
    }

    public double getFactionPoints() {
        return this.factionPoints;
    }

    double highestPlayerBalance = 0.0D;

    public double getHighestPlayerBalance() {
        return this.highestPlayerBalance;
    }

    String highestPlayer = "None";

    String owner;

    public String getHighestPlayer() {
        return this.highestPlayer;
    }

    public String getOwner() {
        return this.owner;
    }

    int kothWins = 0;

    public int getKothWins() {
        return this.kothWins;
    }

    public void setKothWins(int kothWins) {
        this.kothWins = kothWins;
    }

    private DecimalFormat format = new DecimalFormat("#,##0");

    private boolean useSpawnerFee = true;

    private double upgradeTotal;

    public void calculateSpawnerWorth() {
        if (!this.useSpawnerFee) {
            System.out.println("Not loading worth.");
            return;
        }
        this.totalSpawnerWorth = 0.0D;
        long day = TimeUnit.DAYS.toMillis(1L);
        for (SpawnerLocation spawner : this.factionSpawners) {
            double spawnerCost = getSpawnerFeeBase(spawner.getSpawnerType());
            if (FactionsTop.get().isGradualSpawnerValues()) {
                long aliveTime = spawner.getMillisecondsSincePlace();
                long days = aliveTime / day;
                double percent = (20L + days * 20L);
                if (percent > 100.0D)
                    percent = 100.0D;
                if (percent < 20.0D)
                    percent = 20.0D;
                spawnerCost *= 0.01D * percent;
            }
            this.totalSpawnerWorth += spawnerCost * spawner.getSpawnerCount();
        }
    }

    public void calculateContainerWorth() {
        this.containerWorth = 0.0D;
        for (BlockValueLocation loc : this.factionContainers)
            this.containerWorth += loc.getBlockValue();
    }

    private static HashMap<EntityType, Integer> cachedPrices = new HashMap<>();

    long lastBalanceUpdate;

    private int getSpawnerFeeBase(EntityType type) {
        Integer cachedPrice = cachedPrices.get(type);
        int price = 0;
        if (cachedPrice != null)
            return cachedPrice.intValue();
        switch (type) {
            case GHAST:
                price = 5000000;
                cachedPrices.put(type, Integer.valueOf(price));
                return price;
            case MAGMA_CUBE:
            case MUSHROOM_COW:
                price = 4000000;
                cachedPrices.put(type, Integer.valueOf(price));
                return price;
        }
        price = P.p.getConfig().getInt("patches.spawner_fee." + type.toString().toLowerCase()) * 10;
        cachedPrices.put(type, Integer.valueOf(price));
        return price;
    }

    public StoredFaction(String factionId, List<SpawnerLocation> spawners, List<BlockValueLocation> factionContainers) {
        this.lastBalanceUpdate = -1L;
        this.factionID = factionId;
        this.factionSpawners = spawners;
        this.factionContainers = factionContainers;
        this.useSpawnerFee = false;
    }

    public void calculateTotalBalance() {
        calculateContainerWorth();
        this.totalBalance = 0.0D;
        double currentHighest = 0.0D;
        Faction faction = getFaction();
        if (faction == null)
            return;
        FPlayer highest = null;
        this.lastBalanceUpdate = System.currentTimeMillis();
        for (FPlayer player : getFaction().getFPlayers()) {
            if (player == null)
                continue;
            if (player.getRole() == Role.ADMIN)
                this.owner = player.getNameAsync();
            try {
                double bal = FactionsTop.getEconomy().getBalance(player.getNameAsync());
                this.totalBalance += bal;
                if (bal > currentHighest) {
                    currentHighest = bal;
                    highest = player;
                }
            } catch (Exception e) {
                System.out.println("Null offline player for " + player.getNameAsync() + " - " + player.getId());
            }
        }
        if (highest != null) {
            this.highestPlayer = highest.getNameAsync();
            this.highestPlayerBalance = currentHighest;
        }
        if (FactionsTop.get().isIncludeBank())
            this.bankBalance = Econ.getFactionBalance(faction);
        if ((FactionsTop.get()).factionPointsEnabled) {
            Long points = FactionsPointsAPI.getPoints(faction);
            this.factionPoints = (points == null) ? 0.0D : points.longValue();
        }
    }

    public double getTotalWorth() {
        return this.totalSpawnerWorth + this.upgradeTotal + this.bankBalance + this.containerWorth;
    }

    public Faction getFaction() {
        return Factions.i.get(this.factionID);
    }

    public String getHoverData(Faction otherFaction) {
        Faction faction = getFaction();
        List<String> retr = Lists.newArrayList();
        ChatColor key = ChatColor.AQUA;
        ChatColor value = ChatColor.LIGHT_PURPLE;
        retr.add(key + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + ((otherFaction != null && otherFaction.isNormal()) ? faction
                .getTag(otherFaction) : faction
                .getTag()));
        retr.add(value + "Leader: " + key + this.owner);
        retr.add("");
        if ((FactionsTop.get()).factionPointsEnabled)
            retr.add(value + "Faction Points: " + key + this.format.format(this.factionPoints));
        retr.add(value + "Faction Wealth: " + key + ChatColor.BOLD.toString() + "$" + key + this.format.format(getTotalWorth()));
        retr.add(value + "Member Wealth: " + key + ChatColor.BOLD.toString() + "$" + key + this.format.format(this.totalBalance));
        if (this.useSpawnerFee) {
            retr.add(value + "Spawner Value: " + key + ChatColor.BOLD.toString() + "$" + key + this.format.format(this.totalSpawnerWorth));
            retr.add("" + value + "Mob Spawners: " + key + getMobSpawnerCount());
        }
        retr.add(value + "Block Value: " + key + ChatColor.BOLD.toString() + "$" + key + this.format.format(this.containerWorth));
        if (FactionsTop.get().isIncludeBank())
            retr.add(value + "Faction Bank: " + key + ChatColor.BOLD + "$" + key + this.format.format(this.bankBalance));
        retr.add(value + "Faction Upgrades: " + key + ChatColor.BOLD + "$" + key + this.format.format(this.upgradeTotal));
        retr.add(value + "KOTH Wins: " + key + this.kothWins);
        retr.add(value + "Richest Member:");
        retr.add("  " + key + this.highestPlayer + ":  " + key + ChatColor.BOLD.toString() + "$" + key + this.format.format((long)this.highestPlayerBalance));
        StringBuilder builder = new StringBuilder();
        for (String val : retr) {
            builder.append(val);
            builder.append("\n");
        }
        return builder.toString();
    }

    public int compareTo(StoredFaction other) {
        return compareTo(other, false);
    }

    public int compareTo(StoredFaction other, boolean forceWealth) {
        boolean pointsEnabled = ((FactionsTop.get()).factionPointsEnabled && !forceWealth);
        double totalWorth = pointsEnabled ? this.factionPoints : getTotalWorth();
        double otherWorth = pointsEnabled ? other.factionPoints : other.getTotalWorth();
        if (otherWorth == totalWorth)
            return 0;
        if (otherWorth < totalWorth)
            return 1;
        return -1;
    }

    public int getMobSpawnerCount() {
        int total = 0;
        for (SpawnerLocation spawner : this.factionSpawners)
            total += spawner.getSpawnerCount();
        return total;
    }
}
