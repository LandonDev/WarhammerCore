package landon.warhammercore.patchapi.patches.fpoints.managers;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import com.massivecraft.factions.Faction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

import com.massivecraft.factions.P;
import com.massivecraft.factions.util.JSONUtils;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import com.google.gson.reflect.TypeToken;
import landon.warhammercore.patchapi.patches.fpoints.FactionPoints;
import landon.warhammercore.patchapi.patches.fpoints.menus.PointPurchaseMenu;
import landon.warhammercore.patchapi.patches.fpoints.struct.PointChangeLogs;
import landon.warhammercore.patchapi.patches.fpoints.struct.PointData;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionManager;
import landon.warhammercore.patchapi.patches.fpoints.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"\000n\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\006\n\002\b\005\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\020%\n\002\020\016\n\002\020\t\n\002\b\005\n\002\030\002\n\002\b\003\n\002\030\002\n\002\b\007\n\002\020\013\n\002\b\n\n\002\020\002\n\002\b\002\n\002\030\002\n\002\b\005\n\002\030\002\n\002\b\002\n\002\020\b\n\002\b\020\030\0002\b\022\004\022\0020\0020\001B\005\006\002\020\003J\006\020.\032\0020/J\032\0200\032\004\030\0010\0342\006\0201\032\002022\b\b\002\0203\032\0020$J\027\0204\032\004\030\0010\0222\b\0201\032\004\030\00102\006\002\0205J(\0206\032\0020/2\006\0201\032\002022\b\0207\032\004\030\001082\006\0209\032\0020\0212\006\020:\032\0020;J\037\020<\032\004\030\0010\0222\b\0201\032\004\030\001022\006\020=\032\0020\005\006\002\020>J\037\020<\032\004\030\0010\0222\b\0201\032\004\030\001022\006\020=\032\0020\022\006\002\020?J)\020<\032\004\030\0010\0222\b\0201\032\004\030\001022\006\020=\032\0020\0222\b\b\002\020@\032\0020$\006\002\020AJ\020\020B\032\0020/2\006\020C\032\0020\002H\026J\020\020D\032\0020/2\006\020C\032\0020\002H\026J\020\020E\032\004\030\0010\0342\006\0201\032\0020\021J\016\020F\032\0020/2\006\020G\032\0020$J\026\020H\032\0020/2\006\0201\032\002022\006\020I\032\0020\005J\026\020H\032\0020/2\006\0201\032\002022\006\020I\032\0020\022J\006\020J\032\0020$R\032\020\004\032\0020\005X\016\006\016\n\000\032\004\b\006\020\007\"\004\b\b\020\tR\020\020\n\032\004\030\0010\013X\016\006\002\n\000R\026\020\f\032\n \016*\004\030\0010\r0\rX\004\006\002\n\000R&\020\017\032\016\022\004\022\0020\021\022\004\022\0020\0220\020X\016\006\016\n\000\032\004\b\023\020\024\"\004\b\025\020\026R&\020\027\032\016\022\004\022\0020\021\022\004\022\0020\0300\020X\016\006\016\n\000\032\004\b\031\020\024\"\004\b\032\020\026R\036\020\033\032\016\022\004\022\0020\021\022\004\022\0020\0340\0208\002@\002X\016\006\002\n\000R\020\020\035\032\004\030\0010\013X\016\006\002\n\000R\032\020\036\032\0020\022X\016\006\016\n\000\032\004\b\037\020 \"\004\b!\020\"R\016\020#\032\0020$X\016\006\002\n\000R\032\020%\032\0020$X\016\006\016\n\000\032\004\b&\020'\"\004\b(\020)R\026\020*\032\n \016*\004\030\0010\r0\rX\004\006\002\n\000R\032\020+\032\0020\005X\016\006\016\n\000\032\004\b,\020\007\"\004\b-\020\t\006K"}, d2 = {"Lcom/cosmicpvp/factionpoints/managers/PointManager;", "Lcom/cosmicpvp/cosmicutils/struct/managers/CosmicManager;", "Lcom/cosmicpvp/factionpoints/FactionPoints;", "()V", "currentWeeklyPercent", "", "getCurrentWeeklyPercent", "()D", "setCurrentWeeklyPercent", "(D)V", "factionLogFile", "Ljava/io/File;", "factionLogToken", "Ljava/lang/reflect/Type;", "kotlin.jvm.PlatformType", "factionNotifiedMap", "", "", "", "getFactionNotifiedMap", "()Ljava/util/Map;", "setFactionNotifiedMap", "(Ljava/util/Map;)V", "factionPointLogs", "Lcom/cosmicpvp/factionpoints/struct/PointChangeLogs;", "getFactionPointLogs", "setFactionPointLogs", "factionPoints", "Lcom/cosmicpvp/factionpoints/struct/PointData;", "factionPointsFile", "lastSetExpiration", "getLastSetExpiration", "()J", "setLastSetExpiration", "(J)V", "loadedData", "", "pointsBuyable", "getPointsBuyable", "()Z", "setPointsBuyable", "(Z)V", "token", "weeklyIncreasePercent", "getWeeklyIncreasePercent", "setWeeklyIncreasePercent", "doWeeklyReset", "", "getFactionPointData", "faction", "Lcom/massivecraft/factions/Faction;", "createIfNull", "getFactionPoints", "(Lcom/massivecraft/factions/Faction;)Ljava/lang/Long;", "logPointChange", "involved", "Lorg/bukkit/entity/Player;", "reason", "points", "", "modifyFactionPoints", "modification", "(Lcom/massivecraft/factions/Faction;D)Ljava/lang/Long;", "(Lcom/massivecraft/factions/Faction;J)Ljava/lang/Long;", "overrideAddition", "(Lcom/massivecraft/factions/Faction;JZ)Ljava/lang/Long;", "onDisable", "FactionPoints", "onEnable", "removeFactionPoints", "saveExpirationData", "changeIncrease", "setFactionPoints", "newPoints", "togglePointsBuyable", "FactionPoints"})
public final class PointManager extends FactionManager<FactionPoints> {
    private Map<String, PointData> factionPoints;

    @NotNull
    private Map<String, PointChangeLogs> factionPointLogs;

    @NotNull
    private Map<Object, Object> factionNotifiedMap;

    private boolean loadedData;

    private File factionPointsFile;

    private File factionLogFile;

    private final Type token;

    private final Type factionLogToken;

    private boolean pointsBuyable;

    private double currentWeeklyPercent;

    private double weeklyIncreasePercent;

    private long lastSetExpiration;

    public PointManager() {
        Intrinsics.checkExpressionValueIsNotNull((new MapMaker()).concurrencyLevel(16).makeMap(), "MapMaker().concurrencyLevel(16).makeMap()");
        this.factionPoints = (new MapMaker()).concurrencyLevel(16).makeMap();
        Intrinsics.checkExpressionValueIsNotNull((new MapMaker()).concurrencyLevel(16).makeMap(), "MapMaker().concurrencyLevel(16).makeMap()");
        this.factionPointLogs = (new MapMaker()).concurrencyLevel(16).makeMap();
        this.factionNotifiedMap = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build().asMap();
        this.token = (new PointManager$token$1()).getType();
        this.factionLogToken = (new PointManager$factionLogToken$1()).getType();
        this.pointsBuyable = true;
        this.weeklyIncreasePercent = 10.0D;
    }

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"\000\016\n\000\n\002\030\002\n\000\n\002\020\016\n\000\020\000\032\0020\0012\006\020\002\032\0020\003H\n\006\002\b\004"}, d2 = {"<anonymous>", "Lcom/cosmicpvp/factionpoints/struct/PointChangeLogs;", "it", "", "apply"})
    static final class PointManager$logPointChange$1<T, R> implements Function<String, PointChangeLogs> {
        public static final PointManager$logPointChange$1 INSTANCE = new PointManager$logPointChange$1();

        @NotNull
        public final PointChangeLogs apply(@NotNull String it) {
            Intrinsics.checkParameterIsNotNull(it, "it");
            return new PointChangeLogs();
        }
    }

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"\000\016\n\000\n\002\030\002\n\000\n\002\020\016\n\000\020\000\032\0020\0012\006\020\002\032\0020\003H\n\006\002\b\004"}, d2 = {"<anonymous>", "Lcom/cosmicpvp/factionpoints/struct/PointData;", "it", "", "apply"})
    static final class PointManager$modifyFactionPoints$currentPoints$1<T, R> implements Function<String, PointData> {
        public static final PointManager$modifyFactionPoints$currentPoints$1 INSTANCE = new PointManager$modifyFactionPoints$currentPoints$1();

        @NotNull
        public final PointData apply(@NotNull String it) {
            Intrinsics.checkParameterIsNotNull(it, "it");
            return new PointData(0L);
        }
    }

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"\000\016\n\000\n\002\030\002\n\000\n\002\020\016\n\000\020\000\032\0020\0012\006\020\002\032\0020\003H\n\006\002\b\004"}, d2 = {"<anonymous>", "Lcom/cosmicpvp/factionpoints/struct/PointData;", "it", "", "apply"})
    static final class PointManager$setFactionPoints$pointData$1<T, R> implements Function<String, PointData> {
        public static final PointManager$setFactionPoints$pointData$1 INSTANCE = new PointManager$setFactionPoints$pointData$1();

        @NotNull
        public final PointData apply(@NotNull String it) {
            Intrinsics.checkParameterIsNotNull(it, "it");
            return new PointData(0L);
        }
    }

    @NotNull
    public final Map<String, PointChangeLogs> getFactionPointLogs() {
        return this.factionPointLogs;
    }

    public final void setFactionPointLogs(@NotNull Map<String, PointChangeLogs> var) {
        Intrinsics.checkParameterIsNotNull(var, "var");
        this.factionPointLogs = var;
    }

    @NotNull
    public final Map<Object, Object> getFactionNotifiedMap() {
        return this.factionNotifiedMap;
    }

    public final void setFactionNotifiedMap(@NotNull Map<Object, Object> var) {
        Intrinsics.checkParameterIsNotNull(var, "var");
        this.factionNotifiedMap = var;
    }

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"\000\027\n\000\n\002\030\002\n\002\020$\n\002\020\016\n\002\030\002\n\000*\001\000\b\n\030\0002\024\022\020\022\016\022\004\022\0020\003\022\004\022\0020\0040\0020\001\006\005"}, d2 = {"com/cosmicpvp/factionpoints/managers/PointManager$token$1", "Lnet/minecraft/util/com/google/gson/reflect/TypeToken;", "", "", "Lcom/cosmicpvp/factionpoints/struct/PointData;", "FactionPoints"})
    public static final class PointManager$token$1 extends TypeToken<Map<String, ? extends PointData>> {}

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"\000\027\n\000\n\002\030\002\n\002\020$\n\002\020\016\n\002\030\002\n\000*\001\000\b\n\030\0002\024\022\020\022\016\022\004\022\0020\003\022\004\022\0020\0040\0020\001\006\005"}, d2 = {"com/cosmicpvp/factionpoints/managers/PointManager$factionLogToken$1", "Lnet/minecraft/util/com/google/gson/reflect/TypeToken;", "", "", "Lcom/cosmicpvp/factionpoints/struct/PointChangeLogs;", "FactionPoints"})
    public static final class PointManager$factionLogToken$1 extends TypeToken<Map<String, ? extends PointChangeLogs>> {}

    public final boolean getPointsBuyable() {
        return this.pointsBuyable;
    }

    public final void setPointsBuyable(boolean var) {
        this.pointsBuyable = var;
    }

    public final double getCurrentWeeklyPercent() {
        return this.currentWeeklyPercent;
    }

    public final void setCurrentWeeklyPercent(double var) {
        this.currentWeeklyPercent = var;
    }

    public final double getWeeklyIncreasePercent() {
        return this.weeklyIncreasePercent;
    }

    public final void setWeeklyIncreasePercent(double var) {
        this.weeklyIncreasePercent = var;
    }

    public final long getLastSetExpiration() {
        return this.lastSetExpiration;
    }

    public final void setLastSetExpiration(long var) {
        this.lastSetExpiration = var;
    }

    public final void logPointChange(@NotNull Faction faction, @Nullable Player involved, @NotNull String reason, int points) {
        Intrinsics.checkParameterIsNotNull(faction, "faction");
        Intrinsics.checkParameterIsNotNull(reason, "reason");
        ((PointChangeLogs)this.factionPointLogs.computeIfAbsent(faction.getId(), PointManager$logPointChange$1.INSTANCE)).logChange(faction, involved, reason, points);
    }

    public final boolean togglePointsBuyable() {
        this.pointsBuyable = !this.pointsBuyable;
        Intrinsics.checkExpressionValueIsNotNull(FactionPoints.get(), "FactionPoints.get()");
        P.p.getConfig().set("pointsBuyable", Boolean.valueOf(this.pointsBuyable));
        P.p.saveConfig();
        return this.pointsBuyable;
    }

    @Nullable
    public final Long getFactionPoints(@Nullable Faction faction) {
        this.factionPoints.get(faction.getId());
        return (faction == null || !faction.isNormal()) ? null : (((PointData)this.factionPoints.get(faction.getId()) != null) ? Long.valueOf((this.factionPoints.get(faction.getId())).points) : null);
    }

    @Nullable
    public final PointData getFactionPointData(@NotNull Faction faction, boolean createIfNull) {
        Intrinsics.checkParameterIsNotNull(faction, "faction");
        PointData found = this.factionPoints.get(faction.getId());
        if (found == null && createIfNull) {
            found = new PointData(0L);
            Map<String, PointData> map = this.factionPoints;
            Intrinsics.checkExpressionValueIsNotNull(faction.getId(), "faction.id");
            String str = faction.getId();
            PointData pointData = found;
            map.put(str, pointData);
        }
        return found;
    }

    @Nullable
    public final Long modifyFactionPoints(@Nullable Faction faction, double modification) {
        return modifyFactionPoints(faction, (long)modification);
    }

    @Nullable
    public final Long modifyFactionPoints(@Nullable Faction faction, long modification) {
        return modifyFactionPoints(faction, modification, false);
    }

    @Nullable
    public final Long modifyFactionPoints(@Nullable Faction faction, long modification, boolean overrideAddition) {
        if (faction == null)
            return null;
        boolean addition = (modification >= 0L);
        String id = faction.getId();
        PointData currentPoints = this.factionPoints.computeIfAbsent(id, PointManager$modifyFactionPoints$currentPoints$1.INSTANCE);
        long oldPoints = currentPoints.points;
        long newPoints = currentPoints.points;
        boolean wasPositive = (newPoints >= 0L);
        if (addition) {
            newPoints += modification;
        } else {
            newPoints -= Math.abs(modification);
        }
        if (wasPositive && newPoints < 0L && addition) {
            String str1 = "[FactionPoints] Seems there was an overflow when adding points to " + id + " (" + faction.getTag() + ") previous=%s, newValue=%s, added=%s retaining previous..";
            Object[] arrayOfObject = { currentPoints, Long.valueOf(newPoints), Long.valueOf(modification) };
            Logger logger = Bukkit.getLogger();
            Intrinsics.checkExpressionValueIsNotNull(String.format(str1, Arrays.copyOf(arrayOfObject, arrayOfObject.length)), "java.lang.String.format(format, *args)");
            String str2 = String.format(str1, Arrays.copyOf(arrayOfObject, arrayOfObject.length));
            logger.info(str2);
            newPoints = currentPoints.points;
        }
        if (newPoints < 0L)
            newPoints = 0L;
        currentPoints.points = newPoints;
        FactionPoints.log("Modifying old points=" + oldPoints + " by " + modification + " for " + faction.getTag() + " (" + id + ") newPoints=" + newPoints);
        return Long.valueOf(newPoints);
    }

    public final void setFactionPoints(@NotNull Faction faction, long newPoints) {
        Intrinsics.checkParameterIsNotNull(faction, "faction");
        PointData pointData = this.factionPoints.computeIfAbsent(faction.getId(), PointManager$setFactionPoints$pointData$1.INSTANCE);
        pointData.points = newPoints;
    }

    public final void setFactionPoints(@NotNull Faction faction, double newPoints) {
        Intrinsics.checkParameterIsNotNull(faction, "faction");
        setFactionPoints(faction, (long)newPoints);
    }

    @Nullable
    public final PointData removeFactionPoints(@NotNull String faction) {
        Intrinsics.checkParameterIsNotNull(faction, "faction");
        this.factionPointLogs.remove(faction);
        return this.factionPoints.remove(faction);
    }

    public void onEnable(@NotNull FactionPoints FactionPoints) {
        Intrinsics.checkParameterIsNotNull(FactionPoints, "FactionPoints");
        try {
            this.factionPointsFile = JSONUtils.getOrCreateFile(P.p.getDataFolder(), "factionPointsData.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.factionLogFile = JSONUtils.getOrCreateFile(P.p.getDataFolder(), "pointLogs.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.factionPointsFile == null)
            Intrinsics.throwNpe();
        try {
            if (JSONUtils.fromJson(this.factionPointsFile, this.token, new HashMap<>()) == null)
                throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap<kotlin.String, com.cosmicpvp.factionpoints.struct.PointData>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.factionPoints = TypeIntrinsics.asMutableMap(JSONUtils.fromJson(this.factionPointsFile, this.token, new HashMap<>()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (JSONUtils.fromJson(this.factionLogFile, this.factionLogToken, new HashMap<>()) == null)
                throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap<kotlin.String, com.cosmicpvp.factionpoints.struct.PointChangeLogs>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.factionPointLogs = TypeIntrinsics.asMutableMap(JSONUtils.fromJson(this.factionLogFile, this.factionLogToken, new HashMap<>()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PointPurchaseMenu.POINTS_PER_DAY = P.p.getConfig().getInt("patches.fpoints.points-purchasable-per-day", PointPurchaseMenu.POINTS_PER_DAY);
        PointPurchaseMenu.COST_PER_POINT = P.p.getConfig().getInt("patches.fpoints.cost-per-point", 1000000);
        this.currentWeeklyPercent = P.p.getConfig().getDouble("patches.fpoints.current-week-increase", 100.0D);
        this.weeklyIncreasePercent = P.p.getConfig().getDouble("patches.fpoints.weekly-increase", 50.0D);
        Bukkit.getLogger().info("[FactionPoints] Loaded " + this.factionPoints.size() + " factions point data, weeklyPercent: " + this.currentWeeklyPercent + " Increase: " + this.weeklyIncreasePercent);
        this.loadedData = true;
        this.lastSetExpiration = P.p.getConfig().getLong("patches.fpoints.last-week-reset");
        if (this.lastSetExpiration == 0L) {
            this.lastSetExpiration = PointPurchaseMenu.getNextResetTime() - TimeUnit.HOURS.toMillis(24L);
            Bukkit.getLogger().info("Setting lastReset to current next reset since unable to find a previous reset time! New: " + this.lastSetExpiration);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(P.p, new PointManager$onEnable$1(),

                60L, 60L);
        Map<String, PointChangeLogs> map = this.factionPointLogs;
        Iterator<Map.Entry<String, PointChangeLogs>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (entry.getValue() != null)
                ((PointChangeLogs)entry.getValue()).cleanupOldLogs();
        }
    }

    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"\000\b\n\000\n\002\020\002\n\000\020\000\032\0020\001H\n\006\002\b\002"}, d2 = {"<anonymous>", "", "run"})
    final class PointManager$onEnable$1 implements Runnable {
        public final void run() {
            long timePassed = System.currentTimeMillis() - PointManager.this.getLastSetExpiration();
            if (timePassed >= TimeUnit.DAYS.toMillis(7L))
                PointManager.this.doWeeklyReset();
        }
    }

    public final void doWeeklyReset() {
        this.lastSetExpiration = System.currentTimeMillis();
        double newIncrease = 1.0D + 0.01D * this.weeklyIncreasePercent;
        double oldPercent = this.currentWeeklyPercent;
        int oldLimit = PointPurchaseMenu.POINTS_PER_DAY;
        this.currentWeeklyPercent = oldPercent * newIncrease;
        PointPurchaseMenu.POINTS_PER_DAY = (int)(oldLimit * newIncrease);
        String str1 = "Incrementing currentWeeklyPercent from %s -> %s by %s lastExpiration=%s, oldPointsPerDay =%s, newPointsPerDay=%s";
        Object[] arrayOfObject = { String.valueOf(oldPercent), String.valueOf(this.currentWeeklyPercent), String.valueOf(newIncrease), String.valueOf(this.lastSetExpiration), String.valueOf(oldLimit), String.valueOf(PointPurchaseMenu.POINTS_PER_DAY) };
        Logger logger = Bukkit.getLogger();
        Intrinsics.checkExpressionValueIsNotNull(String.format(str1, Arrays.copyOf(arrayOfObject, arrayOfObject.length)), "java.lang.String.format(format, *args)");
        String str2 = String.format(str1, Arrays.copyOf(arrayOfObject, arrayOfObject.length));
        logger.info(str2);
        Bukkit.broadcastMessage(CC.AquaB + "*** New Faction Point Multiplier: " + CC.AquaU + NumberUtils.formatMoney(this.currentWeeklyPercent) + "%" + CC.AquaB + " ***");
        saveExpirationData(false);
    }

    public final void saveExpirationData(boolean changeIncrease) {
        Intrinsics.checkExpressionValueIsNotNull(FactionPoints.get(), "FactionPoints.get()");
        P.p.getConfig().set("patches.fpoints.last-week-reset", Long.valueOf(this.lastSetExpiration));
        Intrinsics.checkExpressionValueIsNotNull(FactionPoints.get(), "FactionPoints.get()");
        P.p.getConfig().set("patches.fpoints.current-week-increase", Double.valueOf(this.currentWeeklyPercent));
        Intrinsics.checkExpressionValueIsNotNull(FactionPoints.get(), "FactionPoints.get()");
        P.p.getConfig().set("patches.fpoints.points-purchasable-per-day", Integer.valueOf(PointPurchaseMenu.POINTS_PER_DAY));
        if (changeIncrease) {
            Intrinsics.checkExpressionValueIsNotNull(FactionPoints.get(), "FactionPoints.get()");
            P.p.getConfig().set("patches.fpoints.weekly-increase", Double.valueOf(this.weeklyIncreasePercent));
        }
        P.p.saveConfig();
    }

    public void onDisable(@NotNull FactionPoints FactionPoints) {
        Intrinsics.checkParameterIsNotNull(FactionPoints, "FactionPoints");
        if (this.loadedData) {
            if (this.factionPointsFile == null)
                Intrinsics.throwNpe();
            try {
                JSONUtils.saveJSONToFile(this.factionPointsFile, this.factionPoints, this.token);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.factionLogFile == null)
                Intrinsics.throwNpe();
            try {
                JSONUtils.saveJSONToFile(this.factionLogFile, this.factionPointLogs, this.factionLogToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().info("[FactionPoints] Not saving faction points due to loadedData == false!");
        }
    }
}
