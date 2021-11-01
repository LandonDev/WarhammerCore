package landon.warhammercore.patchapi.patches.fpoints.utils.factions;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.reflection.ParamBuilder;
import com.massivecraft.factions.util.reflection.ReflectionUtils;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.PlayerEntity;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class WarhammerFactionHandler extends FactionHandler {
    private Factions instance;

    private FPlayers fPlayers;

    private Board board;

    private Field focusPlayerField;

    private Method entityGetId;

    private Method getPlayerFromFPlayer;

    public void initHandler() {
        try {
            Object fPlayers = ReflectionUtils.getObject(FPlayers.class, null, "i");
            this.fPlayers = (FPlayers)fPlayers;
            Object fInstance = ReflectionUtils.getObject(Factions.class, null, "i");
            this.instance = (Factions)fInstance;
            this.focusPlayerField = ReflectionUtils.getField(Faction.class, "focusedPlayer");
            this.entityGetId = ReflectionUtils.getMethod(Entity.class, "getId");
            this.getPlayerFromFPlayer = ReflectionUtils.getMethod(PlayerEntity.class, "getPlayer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInOwnFaction(Player player) {
        Faction at = Board.getFactionAt(new FLocation(player.getLocation()));
        FPlayer fpla = getFPlayer(player);
        return (at != null && fpla != null && at.getFPlayers().contains(fpla));
    }

    public boolean inSameFaction(Player player, Player other) {
        if (player != null && other != null && other.equals(player))
            return true;
        FPlayer pl = getFPlayer(player);
        FPlayer otherFPlayer = getFPlayer(other);
        return (pl != null && otherFPlayer != null && pl.hasFaction() && pl.getFaction().getFPlayers().contains(otherFPlayer));
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
        return fPlayer.getRelationTo((RelationParticipator)otherfPlayer);
    }

    public boolean inInAllyFaction(Player player, Player other) {
        return (getRelation(player, other) == Relation.ALLY);
    }

    public String getFactionTag(Player seeingMsg, Player checking) {
        FPlayer fplayer = getFPlayer(seeingMsg);
        FPlayer other = getFPlayer(checking);
        return other.getChatTag(fplayer);
    }

    public String getFactionTag(Player seeingMsg) {
        FPlayer fplayer = getFPlayer(seeingMsg);
        if (!fplayer.hasFaction())
            return null;
        return fplayer.getFaction().getTag();
    }

    public String getFactionId(Player seeingMsg) {
        FPlayer fplayer = getFPlayer(seeingMsg);
        if (!fplayer.hasFaction())
            return null;
        return fplayer.getFaction().getId();
    }

    public String getFPlayerName(FPlayer fplayer) {
        return fplayer.getNameAsync();
    }

    public String getFPlayerNameAndTag(FPlayer fplayer) {
        return (String) FactionUtils.callFPlayerMethod(fplayer, "getNameAndTagAsync", null);
    }

    public Collection<FPlayer> getAllFPlayers(boolean online) {
        return online ? this.fPlayers.getOnline() : this.fPlayers.get();
    }

    public Collection<Faction> getAllFactions() {
        return this.instance.get();
    }

    public Map<String, Map<String, String>> dumpAsSaveFormat() {
        try {
            Method saveFormat = ReflectionUtils.getMethod(Board.class, "dumpAsSaveFormat");
            return (Map<String, Map<String, String>>)saveFormat.invoke(null, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Faction getFactionAt(Location location) {
        return Board.getFactionAt(new FLocation(location));
    }

    public Faction getFactionAt(FLocation location) {
        return Board.getFactionAt(location);
    }

    public List<String> getFPlayerIdsByTag(String tag) {
        Faction faction = getFactionByTag(tag);
        if (faction == null)
            return null;
        List<String> retr = Lists.newArrayList();
        faction.getFPlayers().forEach(mem -> retr.add(mem.getId()));
        return retr;
    }

    public String getFactionIdByTag(String tag) {
        Faction faction = getFactionByTag(tag);
        if (faction == null)
            return null;
        return faction.getId();
    }

    public FPlayer getFPlayerByName(String name) {
        return this.fPlayers.get(name);
    }

    public Player getPlayer(FPlayer player) {
        try {
            return (Player)this.getPlayerFromFPlayer.invoke(player, new Object[0]);
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Faction getFaction(FPlayer player, boolean includeWilderness) {
        Faction faction = player.getFaction();
        if (!includeWilderness && faction.isNone())
            return null;
        return faction;
    }

    public void sendMessage(FPlayer fplayer, String msg) {
        ReflectionUtils.callReflectionMethod(fplayer, PlayerEntity.class, "sendMessage", new ParamBuilder(String.class, msg));
    }

    public boolean isFocusedPlayer(Faction faction, String name) {
        try {
            FPlayer focusing = (FPlayer)this.focusPlayerField.get(faction);
            if (focusing == null)
                return false;
            if (focusing.getNameAsync().equals(name))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public FPlayer getFocusing(Faction faction) {
        return (FPlayer)ReflectionUtils.getObject(Faction.class, faction, "focusedPlayer");
    }

    public void setFocusing(Faction faction, FPlayer player) {
        ReflectionUtils.setObject(faction, this.focusPlayerField, player);
    }

    public boolean attemptClaim(FPlayer fplayer, Faction faction, Location location, boolean notifyFailure) {
        return fplayer.attemptClaimAsync(faction, location, notifyFailure);
    }

    public Class<?> getMainFactionClass() {
        return Faction.class;
    }

    public Class<?> getMainFPlayerClass() {
        return FPlayer.class;
    }

    public List<String> getFPlayerIdsById(String id) {
        Faction faction = getFactionById(id);
        if (faction == null)
            return null;
        List<String> retr = Lists.newArrayList();
        faction.getFPlayers().forEach(mem -> retr.add(mem.getId()));
        return retr;
    }

    public Faction getFaction(Player player, boolean includeWilderness) {
        FPlayer fplayer = getFPlayer(player);
        if (fplayer == null) {
            if (includeWilderness)
                return this.instance.getNone();
            return null;
        }
        Faction fac = fplayer.getFaction();
        if (!includeWilderness && fac.isNone())
            return null;
        return fac;
    }

    public Faction getFactionByTag(String tag) {
        return this.instance.getByTag(tag);
    }

    public Faction getFactionById(String id) {
        return this.instance.get(id);
    }

    public FPlayer getFPlayer(Player player) {
        try {
            Method method = ReflectionUtils.getMethod(PlayerEntityCollection.class, "get", new Class[] { OfflinePlayer.class });
            return (FPlayer)method.invoke(this.fPlayers, new Object[] { player });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFactionId(Faction faction) {
        return (String)ReflectionUtils.invokeMethod(this.entityGetId, faction);
    }

    public String getFPlayerId(FPlayer fplayer) {
        return (String)ReflectionUtils.invokeMethod(this.entityGetId, fplayer);
    }

    public int getLand(Faction faction) {
        return faction.getLandRounded();
    }

    public int getLandMax(Faction faction) {
        return (int)Math.round(getPowerMax(faction));
    }

    public double getPower(Faction faction) {
        return faction.getPower();
    }

    public double getPowerMax(Faction faction) {
        return faction.getPowerMax();
    }

    public double getDTR(Faction faction) {
        return 0.0D;
    }

    public double getDTRMax(Faction faction) {
        return 0.0D;
    }

    public double getDTRMin(Faction faction) {
        return 0.0D;
    }

    public double getFactionBalance(Faction faction) {
        return ((Double)ReflectionUtils.callReflectionMethod(null, Econ.class, "getFactionBalance", null)).doubleValue();
    }

    private FPlayers getFPlayers() {
        return this.fPlayers;
    }
}

