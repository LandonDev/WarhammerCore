/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.minecraft.util.com.google.common.collect.Lists
 *  org.bukkit.Location
 */
package com.massivecraft.factions;

import com.google.common.collect.Lists;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FactionWrapper {
    public ConcurrentHashMap<FLocation, Set<String>> chunkClaims = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Location> allyWarps = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Location> allAllyWarps = new ConcurrentHashMap<>();
    String factionId;

    public boolean addFactionAccess(FLocation l, Faction f) {
        String facString;
        Set<String> access = new HashSet<>();
        if (this.chunkClaims.containsKey(l)) {
            access = this.chunkClaims.get(l);
        }
        if (access.contains(facString = this.getFactionAccessString(f.getId()))) {
            return false;
        }
        access.add(facString);
        this.chunkClaims.put(l, access);
        return true;
    }

    public boolean addPlayerAccess(FLocation l, FPlayer f) {
        String facString;
        Set<String> access = new HashSet<>();
        if (this.chunkClaims.containsKey(l)) {
            access = this.chunkClaims.get(l);
        }
        if (access.contains(facString = this.getPlayerAccessString(f.getId()))) {
            return false;
        }
        access.add(facString);
        this.chunkClaims.put(l, access);
        return true;
    }

    public List<String> getPlayerAccess(FLocation fl) {
        ArrayList retr = Lists.newArrayList();
        if (!this.chunkClaims.containsKey(fl)) {
            return retr;
        }
        for (String s : this.chunkClaims.get(fl)) {
            FPlayer fp;
            if (!s.startsWith("player:") || (fp = FPlayers.i.get(s.split("player:")[1])) == null) continue;
            retr.add(fp.getNameAsync());
        }
        return retr;
    }

    public List<String> getFactionAccess(FLocation fl) {
        ArrayList retr = Lists.newArrayList();
        if (!this.chunkClaims.containsKey(fl)) {
            return retr;
        }
        for (String s : this.chunkClaims.get(fl)) {
            Faction f;
            if (!s.startsWith("faction:") || (f = Factions.i.get(s.split("faction:")[1])) == null) continue;
            retr.add(f.getTag());
        }
        return retr;
    }

    public Map<Faction, Set<FPlayer>> getFactionWithPlayersWithAccess(Faction owning, Set<String> perms) {
        LinkedHashMap<Faction, Set<FPlayer>> playersMap = new LinkedHashMap<Faction, Set<FPlayer>>();
        if (perms == null) {
            return playersMap;
        }
        perms.forEach(perm -> {
            Faction fac;
            FPlayer fplayer;
            String[] split = perm.split(":");
            String type = split[0];
            String name = split[1];
            if (type.equals("faction")) {
                Faction faction = Factions.i.get(name);
                if (faction != null && !playersMap.containsKey(faction)) {
                    playersMap.put(faction, new HashSet<>());
                }
            } else if (type.equals("player") && (fplayer = FPlayers.i.get(name)) != null && (fac = fplayer.getFaction()) != null && fac.isNormal() && fac.getRelationTo(owning) != Relation.ENEMY) {
                Set players = playersMap.computeIfAbsent(fac, e -> new HashSet<>());
                players.add(fplayer);
            }
        });
        return playersMap;
    }

    public LinkedList<EconomyParticipator> getFactionWithPlayersWithAccessList(Faction owning, Set<String> perms) {
        LinkedList<EconomyParticipator> players = new LinkedList<EconomyParticipator>();
        if (perms == null) {
            return players;
        }
        perms.forEach(perm -> {
            Faction fac;
            FPlayer fplayer;
            String[] split = perm.split(":");
            String type = split[0];
            String name = split[1];
            if (type.equals("faction")) {
                Faction faction = Factions.i.get(name);
                if (!(faction != null && faction.getRelationTo(owning) == Relation.ENEMY || players.contains(faction))) {
                    players.add(faction);
                }
            } else if (type.equals("player") && (fplayer = FPlayers.i.get(name)) != null && ((fac = fplayer.getFaction()) == null || fac.getRelationTo(owning) != Relation.ENEMY) && !players.contains(fplayer)) {
                players.add(fplayer);
            }
        });
        return players;
    }

    public Map<Faction, Set<FPlayer>> getFactionWithPlayersWithAccess(Faction owning, FLocation chunk) {
        Set<String> perms = this.chunkClaims.get(chunk);
        return this.getFactionWithPlayersWithAccess(owning, perms);
    }

    public void addAllAllyWarp(String name, Location l) {
        this.allAllyWarps.put(name, l);
    }

    public boolean hasAllWarp() {
        return this.allAllyWarps.size() > 0;
    }

    public boolean hasAllyWarp(Faction f) {
        return this.allyWarps.contains(f.getId());
    }

    public Location getAllyWarp(Faction f) {
        if (this.allyWarps.containsKey(f.getId())) {
            return this.allyWarps.get(f.getId());
        }
        return null;
    }

    public boolean removeFactionAccess(FLocation loc, Faction f) {
        String str;
        if (!this.isLocationTracked(loc)) {
            return false;
        }
        Set<String> access = this.chunkClaims.get(loc);
        if (access.contains(str = this.getFactionAccessString(f.getId()))) {
            access.remove(str);
            if (access.isEmpty()) {
                this.chunkClaims.remove(loc);
                FactionWrappers.removeLocation(this, loc);
            } else {
                this.chunkClaims.put(loc, access);
            }
            return true;
        }
        return false;
    }

    public boolean removePlayerAccess(FLocation loc, FPlayer fp) {
        String str;
        if (!this.isLocationTracked(loc)) {
            return false;
        }
        Set<String> access = this.chunkClaims.get(loc);
        if (access.contains(str = this.getPlayerAccessString(fp.getId()))) {
            access.remove(str);
            if (access.isEmpty()) {
                this.chunkClaims.remove(loc);
                FactionWrappers.removeLocation(this, loc);
            } else {
                this.chunkClaims.put(loc, access);
            }
            return true;
        }
        return false;
    }

    public void removeAllFactionAccess(Faction faction, boolean removePlayers) {
        HashSet toRemove = new HashSet<>();
        if (removePlayers) {
            faction.getFPlayers().forEach(pl -> toRemove.add(this.getPlayerAccessString(pl.getId())));
        }
        this.chunkClaims.forEach((loc, access) -> {
            String str = this.getFactionAccessString(faction.getId());
            access.remove(str);
            if (removePlayers) {
                toRemove.forEach(access::remove);
            }
        });
    }

    public boolean isLocationTracked(FLocation l) {
        return this.chunkClaims.containsKey(l);
    }

    public boolean doesFactionHaveAccess(FLocation l, Faction fac) {
        if (!this.isLocationTracked(l)) {
            return true;
        }
        for (String access : this.chunkClaims.get(l)) {
            if (!access.startsWith("faction:") || !this.getFactionAccessString(fac.getId()).equals(access)) continue;
            return true;
        }
        return false;
    }

    public boolean doesPlayerHaveChunkAccess(FLocation l, FPlayer fp, boolean checkFactions) {
        if (!this.isLocationTracked(l)) {
            return true;
        }
        Faction at = Board.getFactionAt(l);
        if (at != this.getFaction()) {
            return false;
        }
        for (String access : this.chunkClaims.get(l)) {
            if (!(access.startsWith("player:") ? access.split(":")[1].equals(fp.getId()) : access.startsWith("faction:") && checkFactions && access.split(":")[1].equals(fp.getFaction().getId()) && fp.getRole().isAtLeast(Role.NORMAL)))
                continue;
            return true;
        }
        if (fp.getFaction() == this.getFaction()) {
            return !Conf.ownedAreasEnabled || !Conf.ownedAreaDenyBuild && !Conf.ownedAreaPainBuild || fp.getFaction().playerHasOwnershipRights(fp, l);
        }
        return false;
    }

    public String getFactionAccessString(String id) {
        return "faction:" + id;
    }

    public String getPlayerAccessString(String id) {
        return "player:" + id;
    }

    public Faction getFaction() {
        return Factions.i.get(this.factionId);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public void setFactionId(String factionId) {
        this.factionId = factionId;
    }

    public void setChunkClaims(ConcurrentHashMap<FLocation, Set<String>> chunkClaims) {
        this.chunkClaims = chunkClaims;
    }

    public ConcurrentHashMap<String, Location> getAllyWarps() {
        return this.allyWarps;
    }

    public void setAllyWarps(ConcurrentHashMap<String, Location> allyWarps) {
        this.allyWarps = allyWarps;
    }

    public ConcurrentHashMap<String, Location> getAllAllyWarps() {
        return this.allAllyWarps;
    }

    public void setAllAllyWarps(ConcurrentHashMap<String, Location> allAllyWarps) {
        this.allAllyWarps = allAllyWarps;
    }
}

