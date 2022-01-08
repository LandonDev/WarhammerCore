package landon.core.patchapi.patches.fpoints.utils.factions;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massivecraft.factions.util.reflection.ParamBuilder;
import com.massivecraft.factions.util.reflection.ReflectionUtils;
import landon.core.patchapi.patches.fpoints.utils.FactionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HCFactionHandler extends FactionHandler {
    private Factions instance;

    private FPlayers fPlayers;

    private Board board;

    private Class<?> memoryFPlayersClass;

    private Class<?> memoryFPlayerClass;

    private Class<?> jsonFactionClass;

    private Class<?> memoryFactionClass;

    private Class<?> memoryBoard;

    private Method factionGetId;

    private Method fplayerGetId;

    private Method fPlayerGetFaction;

    private Method fPlayerHasFaction;

    private Method getFplayerById;

    private Method getFplayerBytag;

    private Method isNone;

    private Method getFactionTag;

    private Method getPlayer;

    private Field focusPlayerField;

    public void initHandler() {
        Method getInstance = ReflectionUtils.getMethod(FPlayers.class, "getInstance");
        try {
            this.fPlayers = (FPlayers)getInstance.invoke(null, new Object[0]);
            Method fInstance = ReflectionUtils.getMethod(Factions.class, "getInstance");
            this.instance = (Factions)fInstance.invoke(null, new Object[0]);
            this.board = Board.getInstance();
            this.memoryFPlayerClass = Class.forName("com.massivecraft.factions.zcore.persist.MemoryFPlayer");
            this.memoryFPlayersClass = Class.forName("com.massivecraft.factions.zcore.persist.MemoryFPlayers");
            this.jsonFactionClass = Class.forName("com.massivecraft.factions.zcore.persist.json.JSONFaction");
            this.memoryFactionClass = Class.forName("com.massivecraft.factions.zcore.persist.MemoryFaction");
            this.factionGetId = ReflectionUtils.getMethod(Faction.class, "getId");
            this.fplayerGetId = ReflectionUtils.getMethod(FPlayer.class, "getId");
            this.fPlayerGetFaction = ReflectionUtils.getMethod(this.memoryFPlayerClass, "getFaction");
            this.fPlayerHasFaction = ReflectionUtils.getMethod(this.memoryFPlayerClass, "hasFaction");
            this.memoryBoard = Class.forName("com.massivecraft.factions.zcore.persist.MemoryBlockBoard");
            this.getFplayerBytag = ReflectionUtils.getMethod(this.memoryFPlayersClass, "getByTag", new Class[] { String.class });
            this.getFplayerById = ReflectionUtils.getMethod(this.memoryFPlayersClass, "getById", new Class[] { String.class });
            this.focusPlayerField = ReflectionUtils.getField(this.memoryFactionClass, "focusedPlayer");
            this.isNone = ReflectionUtils.getMethod(this.memoryFactionClass, "isNone");
            this.getPlayer = ReflectionUtils.getMethod(FPlayer.class, "getPlayer");
            this.getFactionTag = ReflectionUtils.getMethod(Faction.class, "getTag");
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isInOwnFaction(Player player) {
        Faction at = getFactionAt(player.getLocation());
        FPlayer fpla = getFPlayer(player);
        return (at != null && fpla != null && (Relation)ReflectionUtils.callReflectionMethod(fpla, this.memoryFPlayerClass, "getRelationTo", new ParamBuilder(RelationParticipator.class, at)) == Relation.MEMBER);
    }

    public boolean inSameFaction(Player player, Player other) {
        return (getRelation(player, other) == Relation.MEMBER);
    }

    public boolean inInEnemyFaction(Player player, Player other) {
        return (getRelation(player, other) == Relation.ENEMY);
    }

    private Relation getRelation(Player player, Player other) {
        if (player == other)
            return Relation.MEMBER;
        FPlayer fPlayer = getFPlayer(player);
        FPlayer otherfPlayer = getFPlayer(other);
        if (fPlayer == null || otherfPlayer == null)
            return Relation.NEUTRAL;
        return (Relation)ReflectionUtils.callReflectionMethod(fPlayer, this.memoryFPlayerClass, "getRelationTo", new ParamBuilder(RelationParticipator.class, otherfPlayer));
    }

    public boolean inInAllyFaction(Player player, Player other) {
        return (getRelation(player, other) == Relation.ALLY);
    }

    public String getFactionTag(Player seeingMsg, Player checking) {
        FPlayer fplayer = getFPlayer(seeingMsg);
        FPlayer other = getFPlayer(checking);
        return (String)ReflectionUtils.callReflectionMethod(other, this.memoryFPlayerClass, "getChatTag", new ParamBuilder(FPlayer.class, fplayer));
    }

    public String getFactionTag(Player seeingMsg) {
        Faction faction = getFaction(seeingMsg, false);
        if (faction == null)
            return null;
        return (String)ReflectionUtils.callReflectionMethod(faction, this.memoryFactionClass, "getTag");
    }

    public String getFactionId(Player seeingMsg) {
        Faction faction = getFaction(seeingMsg, false);
        if (faction == null)
            return null;
        return (String)ReflectionUtils.invokeMethod(this.factionGetId, faction);
    }

    public String getFPlayerName(FPlayer fplayer) {
        return (String) FactionUtils.callFPlayerMethod(fplayer, "getName", null);
    }

    public String getFPlayerNameAndTag(FPlayer fplayer) {
        return (String)FactionUtils.callFPlayerMethod(fplayer, "getNameAndTag", null);
    }

    public Map<String, Map<String, String>> dumpAsSaveFormat() {
        try {
            Class<?> boardClass = Class.forName("com.massivecraft.factions.zcore.persist.json.JSONBlockBoard");
            if (this.board != null && boardClass.isInstance(this.board)) {
                Object jsonBoard = boardClass.cast(this.board);
                Method dumpAsSaveFormat = ReflectionUtils.getMethod(jsonBoard.getClass(), "dumpAsSaveFormat");
                return (Map<String, Map<String, String>>)dumpAsSaveFormat.invoke(jsonBoard, new Object[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public FPlayer getFPlayerByName(String name) {
        try {
            return (FPlayer)this.getFplayerBytag.invoke(this.fPlayers, new Object[] { name });
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player getPlayer(FPlayer player) {
        try {
            return (Player)this.getPlayer.invoke(player, new Object[0]);
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Faction getFactionAt(Location location) {
        return getFactionAt(new FLocation(location));
    }

    public Faction getFactionAt(FLocation location) {
        return (Faction)ReflectionUtils.callReflectionMethod(this.board, this.memoryBoard, "getFactionAt", new ParamBuilder(FLocation.class, location));
    }

    public Faction getFaction(Player player, boolean includeWilderness) {
        FPlayer fplayer = getFPlayer(player);
        return (Faction)ReflectionUtils.invokeMethod(this.fPlayerGetFaction, fplayer);
    }

    public Faction getFaction(FPlayer player, boolean includeWilderness) {
        Faction faction = (Faction)ReflectionUtils.invokeMethod(this.fPlayerGetFaction, player);
        if (!includeWilderness && ((Boolean)ReflectionUtils.invokeMethod(this.isNone, faction)).booleanValue())
            return null;
        return faction;
    }

    public void sendMessage(FPlayer fplayer, String msg) {
        ReflectionUtils.callReflectionMethod(fplayer, FPlayer.class, "sendMessage", new ParamBuilder(String.class, msg));
    }

    public Faction getFactionByTag(String tag) {
        Faction fac = this.instance.getByTag(tag);
        return (Faction)this.jsonFactionClass.cast(fac);
    }

    private String getFacId(Faction faction) {
        return (String)ReflectionUtils.callReflectionMethod(faction, this.memoryFactionClass, "getId", null);
    }

    public Faction getFactionById(String tag) {
        try {
            Method getFactionById = ReflectionUtils.getMethod(Factions.class, "getFactionById", new Class[] { String.class });
            return (Faction)getFactionById.invoke(this.instance, new Object[] { tag });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getFPlayerIdsByTag(String tag) {
        Faction faction = getFactionByTag(tag);
        if (faction == null)
            return null;
        return getFPlayerAccountIds(faction);
    }

    public String getFactionIdByTag(String tag) {
        Faction faction = getFactionByTag(tag);
        if (faction == null)
            return null;
        return getFacId(faction);
    }

    public boolean isFocusedPlayer(Faction faction, String name) {
        try {
            FPlayer focusing = (FPlayer)this.focusPlayerField.get(faction);
            if (focusing == null)
                return false;
            String fName = getFPlayerName(focusing);
            if (fName.equals(name))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public FPlayer getFocusing(Faction faction) {
        try {
            return (FPlayer)this.focusPlayerField.get(faction);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFocusing(Faction faction, FPlayer player) {
        ReflectionUtils.setObject(faction, this.focusPlayerField, player);
    }

    public boolean attemptClaim(FPlayer fplayer, Faction faction, Location location, boolean notifyFailure) {
        return ((Boolean)ReflectionUtils.callReflectionMethod(fplayer, FPlayer.class, "attemptClaim", (new ParamBuilder(Faction.class, faction))
                .add(Location.class, location).add(boolean.class, Boolean.valueOf(notifyFailure)))).booleanValue();
    }

    public Class<?> getMainFactionClass() {
        return this.memoryFactionClass;
    }

    public Class<?> getMainFPlayerClass() {
        return this.memoryFPlayerClass;
    }

    public List<String> getFPlayerIdsById(String id) {
        Faction faction = getFactionById(id);
        if (faction == null)
            return null;
        return getFPlayerAccountIds(faction);
    }

    private List<String> getFPlayerAccountIds(Faction fac) {
        List<String> retr = Lists.newArrayList();
        Set<FPlayer> players = (Set<FPlayer>)ReflectionUtils.callReflectionMethod(fac, this.memoryFactionClass, "getFPlayers", null);
        players.forEach(pl -> retr.add((String)ReflectionUtils.callReflectionMethod(pl, FPlayer.class, "getAccountId", null)));
        return retr;
    }

    public FPlayer getFPlayer(Player player) {
        try {
            Method method = ReflectionUtils.getMethod(this.memoryFPlayersClass, "getByPlayer", new Class[] { Player.class });
            return (FPlayer)method.invoke(this.fPlayers, new Object[] { player });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFactionId(Faction faction) {
        return (String)ReflectionUtils.invokeMethod(this.factionGetId, faction);
    }

    public String getFPlayerId(FPlayer fplayer) {
        return (String)ReflectionUtils.invokeMethod(this.fplayerGetId, fplayer);
    }

    public int getLand(Faction faction) {
        return ((Integer)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getLand", null)).intValue();
    }

    public int getLandMax(Faction faction) {
        return ((Integer)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getMaxLand", null)).intValue();
    }

    public double getPower(Faction faction) {
        return 0.0D;
    }

    public double getPowerMax(Faction faction) {
        return 0.0D;
    }

    public double getDTR(Faction faction) {
        return ((Double)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getDTR", null)).doubleValue();
    }

    public double getDTRMax(Faction faction) {
        return ((Double)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getMaxDTR", null)).doubleValue();
    }

    public double getDTRMin(Faction faction) {
        return ((Double)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getMinDTR", null)).doubleValue();
    }

    public double getFactionBalance(Faction faction) {
        return ((Double)ReflectionUtils.getObject(Faction.class, faction, "money")).doubleValue();
    }

    private String getAccountId(Faction faction) {
        return (String)ReflectionUtils.callReflectionMethod(faction, Faction.class, "getAccountId", null);
    }

    private FPlayers getFPlayers() {
        return this.fPlayers;
    }

    public Collection<FPlayer> getAllFPlayers(boolean online) {
        return online ? (Collection<FPlayer>)ReflectionUtils.callReflectionMethod(getFPlayers(), FPlayers.class, "getOnlinePlayers", null) :
                (Collection<FPlayer>)ReflectionUtils.callReflectionMethod(getFPlayers(), FPlayers.class, "getAllFPlayers", null);
    }

    public Collection<Faction> getAllFactions() {
        return (List)ReflectionUtils.callReflectionMethod(this.instance, Factions.class, "getAllFactions", null);
    }
}
