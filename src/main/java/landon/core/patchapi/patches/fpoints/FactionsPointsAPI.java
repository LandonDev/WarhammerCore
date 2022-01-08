package landon.core.patchapi.patches.fpoints;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.massivecraft.factions.util.CCItemBuilder;
import com.massivecraft.factions.util.TimeUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.core.patchapi.patches.fpoints.struct.PointData;
import landon.core.patchapi.patches.fpoints.utils.DecimalFormatType;
import landon.core.patchapi.patches.fpoints.utils.FactionUtils;
import landon.core.patchapi.patches.fpoints.utils.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FactionsPointsAPI {
    private static SimpleDateFormat format = new SimpleDateFormat("MMM dd hh:mmaa");

    public static ItemStack createPointItem(double amount) {
        return createPointItem(amount, null);
    }

    public static ItemStack createPointItem(double amount, Long manualExpiration) {
        long currentTime = System.currentTimeMillis();
        NBTItem wrapper = (new NBTItem((new CCItemBuilder(Material.PAPER, CC.GoldB + "Faction Points Note" + CC.Gray + " (Right Click)", new String[] { CC.Gold + "Value " + CC.White + NumberUtils.format(amount, DecimalFormatType.MONEY).replace(".00", ""), "", CC.Gray + "Click to apply faction points", CC.Gray + "to your factions /f top value!", "", CC.RedB + "Valid Until " + CC.RedU + TimeUtils.formatTimeToDate((manualExpiration != null) ? manualExpiration.longValue() : (currentTime + TimeUnit.HOURS.toMillis(24L)), "MMM d'%s' hh:mmaa", "CST").replace(" 0", " ") + " CST" })).build()));
        wrapper.setString("cosmicType", "fPointNote");
        wrapper.addCompound("cosmicData").setDouble("points", amount);
        wrapper.getCompound("cosmicData").setLong("created", System.currentTimeMillis());
        if (manualExpiration != null && manualExpiration.longValue() > 0L)
            wrapper.getCompound("cosmicData").setLong("expiration", manualExpiration);
        return wrapper.getItem();
    }

    public static Long modifyPoints(Player player, double modification) {
        FPlayer fplayer = FactionUtils.getFPlayer(player);
        if (fplayer == null)
            return null;
        return modifyPoints(fplayer.getFaction(), modification);
    }

    public static Long modifyPoints(Faction faction, double modification, boolean forceSendMessage) {
        if (forceSendMessage && faction != null)
            FactionPoints.get().getPointManager().getFactionNotifiedMap().remove(faction.getId());
        return modifyPoints(faction, modification);
    }

    public static Long modifyPoints(Faction faction, double modification) {
        if (!FactionUtils.isNormal(faction))
            return null;
        return FactionPoints.get().getPointManager().modifyFactionPoints(faction, modification);
    }

    public static Long setPoints(Faction faction, double newPoints) {
        if (!FactionUtils.isNormal(faction))
            return null;
        FactionPoints.get().getPointManager().setFactionPoints(faction, newPoints);
        return Long.valueOf((long)newPoints);
    }

    public static boolean removePoints(Faction faction) {
        return (FactionPoints.get().getPointManager().removeFactionPoints(faction.getId()) != null);
    }

    public static boolean removePoints(String id) {
        return (FactionPoints.get().getPointManager().removeFactionPoints(id) != null);
    }

    public static Long getPoints(Faction faction) {
        if (faction == null || !FactionUtils.isNormal(faction))
            return null;
        return FactionPoints.get().getPointManager().getFactionPoints(faction);
    }

    public static PointData getPointData(Faction faction, boolean create) {
        if (!FactionUtils.isNormal(faction))
            return null;
        return FactionPoints.get().getPointManager().getFactionPointData(faction, create);
    }

    public static boolean isPointItem(ItemStack item) {
        return isPointItem(new NBTItem(item));
    }

    public static boolean isPointItem(NBTItem wrapper) {
        return wrapper.getString("cosmicType").equals("fPointNote");
    }

    public static Double getPointValue(NBTItem wrapper) {
        return wrapper.getCompound("cosmicData").getDouble("points");
    }

    public static Long giveFactionPoints(Faction faction, Player player, String eventName, Object data) {
        int pointsToGive = getPointsToGiveScaled(faction, player, eventName, data);
        if (pointsToGive <= 0)
            return Long.valueOf(0L);
        return giveFactionPoints(faction, player, eventName, pointsToGive, data);
    }

    public static Long giveFactionPoints(Faction faction, Player player, String eventName, int points, Object data) {
        if (faction == null || !faction.isNormal())
            return Long.valueOf(0L);
        boolean override = (eventName != null && (eventName.equals("f_upgrade") || eventName.equals("op_command")));
        boolean isSpammable = (eventName != null && (eventName.equals("outpost_vanilla") || eventName.equals("end_custom")));
        Long retr = override ? FactionPoints.get().getPointManager().modifyFactionPoints(faction, points, true) : modifyPoints(faction, points, !isSpammable);
        if (retr != null) {
            FactionPoints.get().getPointManager().logPointChange(faction, player, eventName, points);
            if (eventName != null)
                try {
                    if (player != null) {
                        P.p.getFlogManager().log(faction, FLogType.F_POINTS, new String[] { CC.GreenB + "+ " + CC.Green + points + " Faction Points (" + retr + ")", player.getName(), "- " + pretifyEventName(eventName) });
                    } else {
                        P.p.getFlogManager().log(faction, FLogType.F_POINTS, new String[] { CC.GreenB + "+ " + CC.Green + points + " Faction Points (" + retr + ")", "your faction", "- " + pretifyEventName(eventName) });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return retr;
    }

    private static String pretifyEventName(String name) {
        if (name.equals("boss_death"))
            return "Boss Defeated";
        if (name.equals("koth"))
            return "KOTH";
        if (name.equals("f_quest"))
            return "/f quest";
        if (name.equals("f_upgrade"))
            return "/f Upgrade";
        if (name.equals("vanilla_outpost") || name.equals("outpost_vanilla"))
            return "Outpost";
        return name;
    }

    public static ItemStack getFactionPointItem(Faction faction, Player player, String eventName, Object data) {
        int pointsToGive = getPointsToGiveScaled(faction, player, eventName, data);
        if (pointsToGive <= 0)
            return null;
        return createPointItem(pointsToGive);
    }

    public static ItemStack getFactionPointItem(Faction faction, Player player, String eventName, int points, Object data) {
        return createPointItem(points);
    }

    private static int getPointsToGiveScaled(Faction faction, Player player, String eventName, Object data) {
        int retr = getPointsToGive(faction, player, eventName, data);
        if (eventName != null && eventName.equals("boss_death") && data instanceof Entity && (
                (Entity)data).hasMetadata("playerSpawnedBoss"))
            return retr;
        long timeSinceStart = System.currentTimeMillis() - FactionPoints.get().getStartOfMap();
        long seconds = timeSinceStart / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        int adjustedRetr = retr;
        double multiplier = Math.max(1.0D, FactionPoints.get().getPointManager().getCurrentWeeklyPercent() * 0.01D);
        FactionPoints.debug("Getting multiplier: " + multiplier + " weeks: " + weeks + ", retr: " + retr + ", adjusted: " + adjustedRetr);
        return (int)(adjustedRetr * multiplier);
    }

    private static int getBossPoints(String bossName, Entity ent) {
        boolean playerSpawned = false;
        if (bossName.equalsIgnoreCase("Brood Mother"))
            return playerSpawned ? 1 : 2;
        if (bossName.equalsIgnoreCase("King Slime"))
            return playerSpawned ? 2 : 5;
        if (bossName.equalsIgnoreCase("Undead Assassin"))
            return playerSpawned ? 3 : 8;
        if (ent != null && ent.hasMetadata("heroicEnvoyBoss"))
            return 50;
        if (bossName.equalsIgnoreCase("Plague Bloater"))
            return playerSpawned ? 4 : 10;
        if (bossName.equalsIgnoreCase("Yijki, World Destroyer"))
            return playerSpawned ? 5 : 15;
        return 0;
    }

    private static int getPointsToGive(Faction faction, Player player, String eventName, Object data) {
        if (eventName == null)
            return 0;
        if (eventName.equalsIgnoreCase("treasure_drop"))
            return 10;
        if (eventName.equalsIgnoreCase("koth"))
            return FactionPoints.get().isVanillaPlanet() ? 100 : 250;
        if (eventName.equalsIgnoreCase("stronghold") || eventName.equalsIgnoreCase("hcf_outposts"))
            return FactionPoints.get().isVanillaPlanet() ? 10 : 20;
        if (eventName.equalsIgnoreCase("vanilla_envoy")) {
            if (data != null) {
                String rarity = (String)data;
                if (rarity.equals("LEGENDARY"))
                    return ThreadLocalRandom.current().nextInt(3) + 2;
            }
            return 0;
        }
        if (eventName.equalsIgnoreCase("conquest"))
            return 50;
        if (eventName.equalsIgnoreCase("outpost_vanilla"))
            return 1;
        if (eventName.equalsIgnoreCase("end_custom"))
            return 1 + ThreadLocalRandom.current().nextInt(2);
        if (eventName.equalsIgnoreCase("boss_death") && data != null) {
            Object[] entityInfo = (Object[])data;
            String givenName = (String)entityInfo[0];
            String bossName = ChatColor.stripColor(givenName);
            return getBossPoints(bossName, (Entity)entityInfo[1]);
        }
        if (eventName.equalsIgnoreCase("contest_prize")) {
            int ranking = ((Integer)data).intValue();
            return (ranking == 1) ? 10 : ((ranking == 2) ? 5 : ((ranking == 3) ? 2 : 0));
        }
        if (eventName.equalsIgnoreCase("space_chest"))
            return 1 + ThreadLocalRandom.current().nextInt(25);
        if (eventName.equalsIgnoreCase("lootbag"))
            return 5 + ThreadLocalRandom.current().nextInt(6);
        if (eventName.equalsIgnoreCase("f_quest"))
            return 50;
        if (eventName.equalsIgnoreCase("contests")) {
            int ranking = ((Integer)data).intValue();
            if (ranking == 1)
                return 10;
            if (ranking == 2)
                return 5;
            if (ranking == 3)
                return 2;
        }
        return 0;
    }
}
