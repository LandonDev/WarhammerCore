package landon.warhammercore.patchapi.patches.fpoints.utils.factions;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class FactionHandler {
    public abstract void initHandler();

    public abstract boolean isInOwnFaction(Player paramPlayer);

    public abstract boolean inSameFaction(Player paramPlayer1, Player paramPlayer2);

    public abstract boolean inInEnemyFaction(Player paramPlayer1, Player paramPlayer2);

    public abstract boolean inInAllyFaction(Player paramPlayer1, Player paramPlayer2);

    public abstract String getFactionTag(Player paramPlayer1, Player paramPlayer2);

    public abstract String getFactionTag(Player paramPlayer);

    public abstract String getFactionId(Player paramPlayer);

    public abstract String getFPlayerName(FPlayer paramFPlayer);

    public abstract String getFPlayerNameAndTag(FPlayer paramFPlayer);

    public abstract Collection<FPlayer> getAllFPlayers(boolean paramBoolean);

    public abstract Collection<Faction> getAllFactions();

    public abstract Map<String, Map<String, String>> dumpAsSaveFormat();

    public abstract boolean isFocusedPlayer(Faction paramFaction, String paramString);

    public abstract Faction getFactionAt(Location paramLocation);

    public abstract Faction getFactionAt(FLocation paramFLocation);

    public abstract Faction getFaction(Player paramPlayer, boolean paramBoolean);

    public abstract Faction getFaction(FPlayer paramFPlayer, boolean paramBoolean);

    public abstract void sendMessage(FPlayer paramFPlayer, String paramString);

    public abstract Faction getFactionByTag(String paramString);

    public abstract Faction getFactionById(String paramString);

    public abstract List<String> getFPlayerIdsByTag(String paramString);

    public abstract String getFactionIdByTag(String paramString);

    public abstract FPlayer getFPlayerByName(String paramString);

    public abstract Player getPlayer(FPlayer paramFPlayer);

    public abstract void setFocusing(Faction paramFaction, FPlayer paramFPlayer);

    public abstract FPlayer getFocusing(Faction paramFaction);

    public abstract boolean attemptClaim(FPlayer paramFPlayer, Faction paramFaction, Location paramLocation, boolean paramBoolean);

    public abstract Class<?> getMainFactionClass();

    public abstract Class<?> getMainFPlayerClass();

    public abstract List<String> getFPlayerIdsById(String paramString);

    public abstract FPlayer getFPlayer(Player paramPlayer);

    public abstract String getFactionId(Faction paramFaction);

    public abstract String getFPlayerId(FPlayer paramFPlayer);

    public abstract int getLand(Faction paramFaction);

    public abstract int getLandMax(Faction paramFaction);

    public abstract double getPower(Faction paramFaction);

    public abstract double getPowerMax(Faction paramFaction);

    public abstract double getDTR(Faction paramFaction);

    public abstract double getDTRMax(Faction paramFaction);

    public abstract double getDTRMin(Faction paramFaction);

    public abstract double getFactionBalance(Faction paramFaction);
}

