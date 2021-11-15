package landon.warhammercore.patchapi.patches.fpoints.utils;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.util.reflection.ParamBuilder;
import com.massivecraft.factions.util.reflection.ReflectionUtils;
import landon.warhammercore.patchapi.patches.fpoints.utils.factions.FactionHandler;
import landon.warhammercore.patchapi.patches.fpoints.utils.factions.HCFactionHandler;
import landon.warhammercore.patchapi.patches.fpoints.utils.factions.WarhammerFactionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionUtils {
    private static FactionHandler factionHandler;

    private static Method getFactionTag;

    public static void destroy() {
        factionHandler = null;
    }

    public static void init() {
        try {
            Class.forName("com.massivecraft.factions.Faction");
            getFactionTag = ReflectionUtils.getMethod(Faction.class, "getTag");
        } catch (ClassNotFoundException|NoClassDefFoundError ex) {
            Bukkit.getLogger().info("NO FACTIONS CLASS FOUND, NOT ENABLED???");
            return;
        } catch (Exception exception) {}
        if ((new File("plugins/HCFactions.jar")).exists()) {
            factionHandler = new HCFactionHandler();
            factionHandler.initHandler();
            return;
        }
        factionHandler = new WarhammerFactionHandler();
        factionHandler.initHandler();
    }

    public static boolean isHCF() {
        return factionHandler instanceof HCFactionHandler;
    }

    public static int getLand(Faction faction) {
        return factionHandler.getLand(faction);
    }

    public static int getLandMax(Faction faction) {
        return factionHandler.getLandMax(faction);
    }

    public static double getPower(Faction faction) {
        return factionHandler.getPower(faction);
    }

    public static double getPowerMax(Faction faction) {
        return factionHandler.getPowerMax(faction);
    }

    public static double getDTR(Faction faction) {
        return factionHandler.getDTR(faction);
    }

    public static double getDTRMax(Faction faction) {
        return factionHandler.getDTRMax(faction);
    }

    public static double getDTRMin(Faction faction) {
        return factionHandler.getDTRMin(faction);
    }

    public static double getFactionBalance(Faction faction) {
        return factionHandler.getFactionBalance(faction);
    }

    public static boolean isFocusedPlayer(Faction faction, String playerName) {
        return factionHandler.isFocusedPlayer(faction, playerName);
    }

    public static String getPlayerNameAndTag(FPlayer fplayer) {
        return factionHandler.getFPlayerNameAndTag(fplayer);
    }

    public static String getPlayerName(FPlayer fplayer) {
        return factionHandler.getFPlayerName(fplayer);
    }

    public static String getChatTag(FPlayer fplayer, RelationParticipator relation) {
        ParamBuilder builder = new ParamBuilder();
        if (relation instanceof Faction) {
            Faction fac = (Faction)relation;
            builder.add(Faction.class, fac);
        } else if (relation instanceof FPlayer) {
            FPlayer fac = (FPlayer)relation;
            builder.add(FPlayer.class, fac);
        }
        return (String)callFPlayerMethod(fplayer, "getChatTag", builder);
    }

    public static boolean attemptClaim(FPlayer fplayer, Faction faction, Location location, boolean notifyFailure) {
        return factionHandler.attemptClaim(fplayer, faction, location, notifyFailure);
    }

    public static boolean isNormal(Faction faction) {
        return (faction != null && ((Boolean)ReflectionUtils.callReflectionMethod(faction, Faction.class, "isNormal", null)).booleanValue());
    }

    public static boolean isWarZone(Faction faction) {
        return (faction != null && ((Boolean)ReflectionUtils.callReflectionMethod(faction, Faction.class, "isWarZone", null)).booleanValue());
    }

    public static boolean isWilderness(Faction faction) {
        return ((Boolean)ReflectionUtils.callReflectionMethod(faction, Faction.class, "isNone", null)).booleanValue();
    }

    public static boolean isSafeZone(Faction faction) {
        return (faction != null && ((Boolean)ReflectionUtils.callReflectionMethod(faction, Faction.class, "isSafeZone", null)).booleanValue());
    }

    public static boolean isWarZone(Location location) {
        return isWarZone(getFactionAt(location));
    }

    public static boolean isSafeZone(Location location) {
        return isSafeZone(getFactionAt(location));
    }

    public static Player getPlayer(FPlayer fplayer) {
        return factionHandler.getPlayer(fplayer);
    }

    public static Faction getNone() {
        return getFactionById("0");
    }

    public static Faction getWarzone() {
        return getFactionById("-2");
    }

    public static Faction getSafeZone() {
        return getFactionById("-1");
    }

    public static Collection<FPlayer> getFPlayers(boolean onlineOnly) {
        return factionHandler.getAllFPlayers(onlineOnly);
    }

    public static Collection<Faction> getAllFactions() {
        return factionHandler.getAllFactions();
    }

    public static boolean isWilderness(Location location) {
        Faction at = getFactionAt(location);
        return (at == null || isWilderness(at));
    }

    public static Relation getRelation(RelationParticipator part, RelationParticipator part2) {
        return (Relation)ReflectionUtils.callReflectionMethod(part, RelationParticipator.class, "getRelationTo", new ParamBuilder(RelationParticipator.class, part2));
    }

    public static boolean isNonPermFaction(Location destination) {
        Faction at = FactionUtils.getFactionAt(destination);
        return (at != null && isNonPermFaction(at));
    }

    public static boolean isNonPermFaction(Faction at) {
        if (at != null && !((Boolean)FactionUtils.callFactionMethod(at, "isSafeZone", null)).booleanValue() &&
                !((Boolean)FactionUtils.callFactionMethod(at, "isNone", null)).booleanValue() &&
                !((Boolean)FactionUtils.callFactionMethod(at, "isWarZone", null)).booleanValue())
            return true;
        return false;
    }

    public static String getTag(Faction faction) {
        return (String)FactionUtils.callFactionMethod(faction, "getTag", null);
    }

    public static boolean isFactionMember(Faction faction, FPlayer player) {
        return (faction != null && player != null && ReflectionUtils.callReflectionMethod(faction, RelationParticipator.class, "getRelationTo", new ParamBuilder(RelationParticipator.class, player)) == Relation.MEMBER);
    }

    public static Relation getRelationTo(RelationParticipator first, RelationParticipator second) {
        return (Relation)ReflectionUtils.callReflectionMethod(first, RelationParticipator.class, "getRelationTo", new ParamBuilder(RelationParticipator.class, second));
    }

    public static boolean isInsideOwnFaction(Player player) {
        return factionHandler.isInOwnFaction(player);
    }

    public static boolean inSameFaction(Player player, Player other) {
        return factionHandler.inSameFaction(player, other);
    }

    public static boolean isInEnemyFaction(Player player, Player other) {
        return factionHandler.inInEnemyFaction(player, other);
    }

    public static boolean isInAllyFaction(Player player, Player other) {
        return factionHandler.inInAllyFaction(player, other);
    }

    public static String getFactionTag(Player seeingMsg, Player checking) {
        return factionHandler.getFactionTag(seeingMsg, checking);
    }

    public static String getFactionTag(Player seeingMsg) {
        return factionHandler.getFactionTag(seeingMsg);
    }

    public static String getFactionTag(Faction faction) {
        if (faction == null)
            return "";
        if (getFactionTag == null)
            Bukkit.getLogger().info("getTag null!");
        return (String)ReflectionUtils.invokeMethod(getFactionTag, faction);
    }

    public static String getFactionId(Player player) {
        return factionHandler.getFactionId(player);
    }

    public static String getFactionId(Faction faction) {
        return factionHandler.getFactionId(faction);
    }

    public static String getFPlayerId(FPlayer fplayer) {
        return factionHandler.getFPlayerId(fplayer);
    }

    public static Map<String, Map<String, String>> dumpAsSaveFormat() {
        return factionHandler.dumpAsSaveFormat();
    }

    public static Faction getFactionAt(Location location) {
        return factionHandler.getFactionAt(location);
    }

    public static Faction getFactionAt(FLocation location) {
        return factionHandler.getFactionAt(location);
    }

    public static Faction getFaction(Player player) {
        return factionHandler.getFaction(player, true);
    }

    public static Faction getFaction(Player player, boolean includeWilderness) {
        return factionHandler.getFaction(player, includeWilderness);
    }

    public static Faction getFaction(FPlayer player) {
        return factionHandler.getFaction(player, true);
    }

    public static Faction getFaction(FPlayer player, boolean includeWilderness) {
        return factionHandler.getFaction(player, includeWilderness);
    }

    public static void sendMessage(FPlayer fplayer, String msg) {
        factionHandler.sendMessage(fplayer, msg);
    }

    public static Faction getFactionByTag(String tag) {
        return factionHandler.getFactionByTag(tag);
    }

    public static FPlayer getFPlayer(Player player) {
        return factionHandler.getFPlayer(player);
    }

    public static Faction getFactionById(String id) {
        return factionHandler.getFactionById(id);
    }

    public static String getFactionIdByTag(String tag) {
        return factionHandler.getFactionIdByTag(tag);
    }

    public static List<String> getFPlayerIdsByTag(String tag) {
        return factionHandler.getFPlayerIdsByTag(tag);
    }

    public static FPlayer getFocusing(Faction faction) {
        return factionHandler.getFocusing(faction);
    }

    public static void setFocusing(Faction faction, Player player) {
        setFocusing(faction, getFPlayer(player));
    }

    public static void setFocusing(Faction faction, FPlayer player) {
        factionHandler.setFocusing(faction, player);
    }

    public static FPlayer getFPlayerByName(String name) {
        return factionHandler.getFPlayerByName(name);
    }

    public static List<String> getFPlayerIdsById(String factionName) {
        return factionHandler.getFPlayerIdsById(factionName);
    }

    public static Object callFPlayerMethod(FPlayer fplayer, String method) {
        return ReflectionUtils.callReflectionMethod(fplayer, factionHandler.getMainFPlayerClass(), method, null);
    }

    public static Object callFPlayerMethod(FPlayer fplayer, String method, ParamBuilder builder) {
        return ReflectionUtils.callReflectionMethod(fplayer, factionHandler.getMainFPlayerClass(), method, builder);
    }

    public static Object callFactionMethod(String id, String method, ParamBuilder builder) {
        Faction fac = getFactionByTag(id);
        return ReflectionUtils.callReflectionMethod(fac, factionHandler.getMainFactionClass(), method, builder);
    }

    public static Object callFactionMethod(Faction faction, String method, ParamBuilder builder) {
        return ReflectionUtils.callReflectionMethod(faction, factionHandler.getMainFactionClass(), method, builder);
    }

    public static Map<String, Relation> getRelationWishes(Faction faction) {
        return (Map<String, Relation>)ReflectionUtils.getObject(factionHandler.getMainFactionClass(), faction, "relationWish");
    }

    public static Object callFactionMethodByTag(String tag, String method, ParamBuilder builder) {
        Faction fac = getFactionByTag(tag);
        return ReflectionUtils.callReflectionMethod(fac, factionHandler.getMainFactionClass(), method, builder);
    }
}

